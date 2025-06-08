from __future__ import annotations

from collections import deque
from typing import Optional

import gymnasium as gym
import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
from gymnasium.spaces import Box, Dict, Discrete
from gymnasium.utils import seeding
from pandas import DataFrame, Series
from stable_baselines3 import PPO
from stable_baselines3.common.logger import Logger, configure
from stable_baselines3.common.monitor import Monitor

from .actions import Action
from .states import AttackState, BaseState, MaquinaDeEstados, NormalState
from .utils import Color, Location
import rich
import tqdm


class RouterEnv(gym.Env):
    total_time: float = 400.0  # Num steps

    def __init__(self, max_len=250, seed: Optional[int] = None):

        super(RouterEnv, self).__init__()
        if max_len < 1:
            raise ValueError("max_len must be greater than 0")

        self.max_len: int = max_len

        duration_step: float = 1.0
        duration_step *= 1e-3  # En segundos
        velocidad_procesamiento: float = 5e6/8  # bytes por segundo de procesamiento
        self.rate: float = velocidad_procesamiento * \
            duration_step  # bytes por step de procesamiento

        self._np_random, self._np_random_seed = seeding.np_random(seed)
        self._set_initial_values()

        self.observation_space = Dict({
            "OcupacionCola": Box(low=0, high=1, dtype=np.float32),
            "Descartados": Box(low=0, high=np.inf, dtype=np.int16),
        })
        self.action_space = Discrete(len(Action))

    def get_seed(self) -> int | None:
        return self._np_random_seed

    def _set_initial_values(self, ):
        self.queue = deque(maxlen=self.max_len)
        self.descartados: int = 0
        self.current_action: Action = Action.ALLOW
        self.action_count: int = 1
        self.uds_tiempo_pasado: float = 0.0
        self.mb_restantes: float = -1.0

        self.last_ocupacion: float = 0.0

        self.maquina = MaquinaDeEstados(self._np_random)

    def reset(self, seed: Optional[int] = None, options: Optional[dict] = None):
        if seed is not None and seed != self._np_random_seed:
            self._np_random, self._np_random_seed = seeding.np_random(seed)
        self._set_initial_values()

        observation = self._get_obs()
        info = self._get_info()
        return observation, info

    def _get_obs(self):
        return {
            "OcupacionCola": np.array([self.get_ocupacion()], dtype=np.float32),
            "Descartados": np.array([self.descartados], dtype=np.int16),
        }

    def _get_info(self):
        return {"Stats": {
            "EstadoMaquina": self.maquina.get_estado().__name__,
            "NumPaquetes":  len(self.queue),
            "TamañoTotal": self.get_tam_ocu(),
            "Action": self.current_action,
            "OcupacionActual": self.get_ocupacion(),
            "Descartados": self.descartados,
        },
        }

    def get_tam_ocu(self) -> float:
        tam_total = 0.0
        for paquete in self.queue:
            tam_total += float(paquete["SIZE"])
        return tam_total

    def packet_input(self, input: list[dict[str, any]] = None) -> int:
        if input is not None:
            paquetes = input
        else:
            paquetes = self.maquina.generate_packets()
            self.maquina.cambiar_estado()

        if self.current_action == Action.DENY:
            return len(paquetes)

        if len(self.queue) + len(paquetes) > self.max_len:

            espacio_libre = self.max_len - len(self.queue)
            self.queue.extend(paquetes[:espacio_libre])

            assert len(self.queue) == self.max_len

            return len(paquetes) - (espacio_libre)

        self.queue.extend(paquetes)
        return 0  # No se han descartado paquetes

    def registrar_accion(self, action: Action):
        if action == self.current_action:
            self.action_count += 1
        else:
            self.action_count = 1
            self.current_action = action

    def step(self, action_num: int):

        self.descartados = 0
        action: Action = Action.int_to_action(action_num)
        self.registrar_accion(action)

        descartados: int = self.packet_input()

        self.procesar_por_tamaño()

        reward: float = self.get_reward(descartados, action)
        self.descartados = descartados
        self.last_ocupacion = self.get_ocupacion()

        observation = self._get_obs()
        # True si se desvía del comportamiento normal para abortar, necesitaría un reset
        truncated = False
        info = self._get_info()

        self.uds_tiempo_pasado += 1
        finished: bool = self._is_finished_execution()

        return observation, reward, finished, truncated, info

    def get_ocupacion(self) -> float:
        return len(self.queue) / self.max_len

    def procesar_por_tamaño(self):

        if len(self.queue) == 0:
            return

        # print(self.queue)
        tam_procesado = 0.0
        # Calcula los mb que faltan por procesar

        while tam_procesado < self.rate and len(self.queue) > 0:
            if self.mb_restantes == 0:
                self.queue.popleft()  # Quita el paquete que se ha procesado
                if len(self.queue) == 0:
                    break
                # Nuevo paquete
                paquete = self.queue[0]
                # Calcula los mb que faltan por procesar
                self.mb_restantes = paquete["SIZE"]
            else:
                # Procesar
                procesado_local: float = min(self.mb_restantes,  # Procesar lo que queda del paquete
                                             self.rate-tam_procesado)  # Procesar lo que queda del paso
                self.mb_restantes -= procesado_local
                tam_procesado += procesado_local

    def _is_finished_execution(self) -> bool:
        # Terminar solo después de 10 pasos
        return self.uds_tiempo_pasado >= self.total_time

    def close(self):
        # Cerrar el entorno, liberar recursos, cerrar conexiones, etc
        return super().close()

    def render(self, mode='human'):
        # Renderizar el entorno
        return super().render(mode=mode)

    def registro_Estados(self) -> list[BaseState]:
        return self.maquina.get_registro()

    def get_reward(self, descartados: int, action: Action) -> float:
        return reward(descartados,  action, self.get_ocupacion(), self.last_ocupacion)


def reward(descartados: int,
           action: Action,
           ocu_actual: float = 0.0,
           ocu_ant: float = 0.0,
           c: float = 0.35,
           c2: float = 0.2,
           c3: float = 0.01,
           c4: float = 7.5,
           c5: float = 1.0,
           ) -> float:
    reward = 0.0
    if descartados > 0:
        if action == Action.ALLOW:
            reward -= (descartados**2) * c
        else:
            reward -= (descartados) * c2

        mejora: float = ocu_ant - ocu_actual
        reward += mejora * ocu_actual * c3
    else:
        if action == Action.ALLOW:
            reward += (1.0 - ocu_actual) * c4 + c5
        else:
            reward += (1.0 - ocu_actual) * c4
    return round(reward, 2)


def train(seed: int, timesteps: int, logs_path: str, progress_bar: bool = True) -> None:
    env: RouterEnv = RouterEnv(seed=seed)
    print("Seed: ", env.get_seed())
    new_logger: Logger = configure(logs_path, ["stdout", "csv"])
    env = Monitor(env, logs_path)

    model: PPO = PPO("MultiInputPolicy", env,
                     verbose=True, seed=seed)

    model.set_logger(new_logger)
    try:
        model.learn(total_timesteps=int(timesteps),
                    progress_bar=progress_bar)
    except KeyboardInterrupt:
        print("Interrupted")
    return model


def get_train_graph(logs_path: str, metric: str = 'train/explained_variance', save: bool = False) -> None:
    train_data_log: DataFrame = pd.read_csv(logs_path+"progress.csv")
    train_data_monitor: DataFrame = pd.read_csv(
        logs_path+"monitor.csv", header=1)

    print("Avaliable metrics: ", train_data_log.columns.to_list())
    if metric not in train_data_log.columns.to_list():
        print(
            f"Metric '{metric}' not found in logs. Probably need more training steps.")
        print("Exiting...")
        return

    fig, ax1 = plt.subplots()

    iterations: Series[int] = train_data_log['time/iterations'].astype(int)

    ax1.set_xlabel('Iterations')
    color = Color.BLUE.value
    ax1.set_ylabel('Reward Mean', color=color)
    ax1.plot(iterations,
             train_data_log['rollout/ep_rew_mean'], color=color, label='Reward Mean')
    ax1.tick_params(axis='y', labelcolor=color)

    col = metric

    ax2 = ax1.twinx()
    color = Color.RED.value
    ax2.set_ylabel(col, color=color)
    ax2.plot(iterations, train_data_log[col], color=color, label=col)
    ax2.tick_params(axis='y', labelcolor=color)
    ax2.set_title('Reward vs ' + col)

    fig.tight_layout()
    fig.legend(loc=Location.UPPER_LEFT.value, bbox_to_anchor=(0.12, 0.9))

    if save is True:
        fig.savefig(logs_path + "train_graph.png")
    else:
        plt.show()

    fig, ax1 = plt.subplots()

    episodes = range(len(train_data_monitor['l']))

    ax1.set_xlabel('Episodes')
    color = Color.BLUE.value
    ax1.set_ylabel('Reward', color=color)
    ax1.plot(episodes, train_data_monitor['r'], color=color, label='Reward')
    ax1.tick_params(axis='y', labelcolor=color)
    ax1.set_title('Reward vs Time')

    ax2 = ax1.twinx()
    color = Color.RED.value
    ax2.set_ylabel('time', color=color)
    ax2.plot(episodes, train_data_monitor['t'], color=color, label='time')
    ax2.tick_params(axis='y', labelcolor=color)

    fig.tight_layout()
    fig.legend(loc=Location.UPPER_LEFT.value, bbox_to_anchor=(0.12, 0.9))

    if save is True:
        fig.savefig(logs_path + "train_graph_reward_time.png")
    else:
        plt.show()


def get_graphs(name: str, logs_path: str, seed: int = 289980628190634006122751570777790489191, save: bool = False) -> None:
    env: RouterEnv = RouterEnv(seed=seed)
    seed: int = env.get_seed()
    model: PPO = PPO.load(name, print_system_info=True)

    num_steps = 1
    obs, _ = env.reset()

    stats = []
    rewards = []

    _states = None
    ant = env._get_info()
    for episode in range(num_steps):

        done = False
        step_counter = 0
        while not done:
            action, _states = model.predict(obs, deterministic=True)
            obs, reward, done, terminated, info = env.step(action)

            stats.append(info["Stats"])
            rewards.append(reward)

            done: bool = done or terminated

        env.reset()
        step_counter += 1

    acciones = [x["Action"] for x in stats]

    def calcular_stats_acciones(acciones) -> None:
        if isinstance(acciones[-1], int):
            acciones = [Action.int_to_action(a) for a in acciones]
        print(f"{Action.DENY.name}: {acciones.count(Action.DENY)/len(acciones):.2%}")
        print(f"{Action.ALLOW.name}: {acciones.count(Action.ALLOW)/len(acciones):.2%}")
    calcular_stats_acciones(acciones)

    print(f"Seed: {env.get_seed()}")

    fig, ax1 = plt.subplots()
    ocu = [x["OcupacionActual"] for x in stats]
    att = 1
    norm = 0

    estados: list[int] = [norm if x["EstadoMaquina"] ==
                          NormalState.__name__ else att for x in stats]

    acciones: list[int] = [Action.action_to_int(x["Action"]) for x in stats]

    ax1.plot(
        acciones, label=f"Action: Allow {Action.action_to_int(Action.ALLOW)}, Deny {Action.action_to_int(Action.DENY)}", color='#0000FF')
    ax1.plot(ocu, label="Queue Occupancy", color='green')
    ax1.set_xlabel("Episodes")
    ax1.set_ylabel("Queue Occupancy (in %)")
    ax1.set_title("Queue Occupancy vs Actions")

    ax2 = ax1.twinx()
    ax2.bar(range(len(estados)), estados, color='red',
            label="State", alpha=0.20, width=1)
    ax2.set_ylabel("Flow Type")
    ax2.set_yticks([0, 1], [NormalState.__name__, AttackState.__name__])

    lines, labels = ax1.get_legend_handles_labels()
    lines2, labels2 = ax2.get_legend_handles_labels()
    ax1.legend(lines + lines2, labels + labels2)

    if save is True:
        fig.savefig(logs_path + "queue_actions.png")
    else:
        plt.show()

    fig, ax1 = plt.subplots()

    ax1.set_xlabel('Steps')
    ax1.set_ylabel('Acciones y Ocupación')

    ax1.step(range(len(acciones)), acciones,
             label="Acciones", where='post', color='green')
    ax1.step(range(len(ocu)), ocu, label="Ocupación",
             where='mid', color='blue')

    ticks = list(ax1.get_yticks())

    ax1.set_yticks(sorted(ticks))

    ax1.axhline(y=Action.action_to_int(Action.DENY), color='red',
                linestyle='dotted', linewidth=0.8, label='Denegar')
    ax1.axhline(y=Action.action_to_int(Action.ALLOW),
                color='green', linestyle='dotted', linewidth=0.8, label='Permitir')

    ax1.legend(loc=Location.UPPER_LEFT.value)

    ax2 = ax1.twinx()
    ax2.set_ylabel('Recompensas')
    ax2.step(range(len(rewards)), rewards,
             label="Recompensas", where='mid', color='r')

    ax2.legend(loc=Location.UPPER_RIGHT.value)

    fig.tight_layout()
    plt.title("Recompensas y acciones tomadas")
    if save is True:
        fig.savefig(logs_path + "rewards_actions.png")
    else:
        plt.show()
    tam = [x["TamañoTotal"] for x in stats]

    fig, ax1 = plt.subplots()

    train_data_monitor: DataFrame = pd.read_csv(
        logs_path+"monitor.csv", header=1)

    episodes = range(len(train_data_monitor['l']))

    ax1.set_xlabel('Episodes')
    color = Color.BLUE.value
    label = 'Ocupación'
    ax1.set_ylabel(label, color=color)
    ax1.plot(range(len(ocu)), ocu, color=color, label=label)
    ax1.tick_params(axis='y', labelcolor=color)
    ax1.set_title('Ocupación vs Tamaño')

    ax2 = ax1.twinx()
    color = Color.RED.value
    label = 'tamaño total'
    ax2.set_ylabel(label, color=color)
    ax2.plot(range(len(tam)), tam, color=color, label=label)
    ax2.tick_params(axis='y', labelcolor=color)

    fig.tight_layout()
    fig.legend(loc=Location.UPPER_LEFT.value, bbox_to_anchor=(0.12, 0.9))

    if save is True:
        fig.savefig(logs_path + "queue_ocupation.png")
    else:
        plt.show()

    print(f"La ocupación máxima es de {np.max(ocu):.0%}")
    print(f"La ocupación mínima es de {np.min(ocu):.0%}")
