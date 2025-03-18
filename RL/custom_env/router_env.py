from __future__ import annotations

from calendar import c
from collections import deque
from typing import Optional

import gymnasium as gym
import numpy as np
from gymnasium.spaces import Box, Dict, Discrete
from gymnasium.utils import seeding

from .actions import Acciones
from .states import MaquinaDeEstados


class RouterEnv(gym.Env):
    total_time: float = 400.0  # Num steps

    def __init__(self, max_len=250, seed: Optional[int] = None):

        super(RouterEnv, self).__init__()
        if max_len < 1:
            raise ValueError("max_len must be greater than 0")

        self.max_len: int = max_len

        duraction_step: float = 1.0
        duraction_step *= 1e-3  # En segundos
        velocidad_procesamiento: float = 5e6/8  # bytes por segundo de procesamiento
        self.rate: float = velocidad_procesamiento * \
            duraction_step  # bytes por step de procesamiento

        self._set_initial_values(seed)
        
        self.observation_space = Dict({
            "OcupacionCola": Box(low=0, high=1, dtype=np.float32),
            "Descartados": Box(low=0, high=np.inf, dtype=np.int16),  
        })
        self.action_space = Discrete(len(Acciones))

    def _set_initial_values(self, seed):
        self.queue = deque(maxlen=self.max_len)
        self.descartados: int = 0
        # self.step_durations: list[float] = []
        self._np_random, self._np_random_seed = seeding.np_random(seed)
        self.current_action: Acciones = Acciones.PERMITIR
        self.action_count: int = 1
        self.uds_tiempo_pasado: float = 0.0
        self.mb_restantes: float = -1.0

        self.maquina = MaquinaDeEstados(self._np_random)

    def reset(self, seed: Optional[int] = None, options: Optional[dict] = None):
        self._set_initial_values(seed)

        observation = self._get_obs()
        info = self._get_info()
        return observation, info

    def _get_obs(self):
        return {
            "OcupacionCola": np.array([self.get_ocupacion()],dtype=np.float32),
            "Descartados": np.array([self.descartados],dtype=np.int16),
        }
        return np.array([self.get_ocupacion(),2], dtype=np.float32)

    def _get_info(self):
        # npack, tam_total, ocu_act, *_ = self.calculate_queue_stats()
        npack = len(self.queue)
        return {"Stats": {
            # "Queue": np.array(self.queue),
            "EstadoMaquina": self.maquina.get_estado().__name__,
            "NumPaquetes": npack,
            "TamañoTotal": self.get_tam_ocu(),
            "Action": self.current_action,
            "OcupacionActual": self.get_ocupacion(),
            "Descartados": self.descartados,
        },
        }

    def calculate_queue_stats(self):
        tam_total = 0
        for paquete in self.queue:
            tam_total += int(paquete["SIZE"])
        tam_promedio: float = tam_total / \
            len(self.queue) if len(self.queue) > 0 else 0.0

        num_packets: int = len(self.queue)
        ocu_act: float = self.get_ocupacion()

        return np.array([ocu_act], dtype=np.float32)
        return np.array([num_packets, tam_total, ocu_act,
                         Acciones.action_to_int(self.current_action), self.action_count], dtype=np.float32)

    def get_tam_ocu(self):
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

        if self.current_action == Acciones.DENEGAR:
            return len(paquetes)

        if len(self.queue) + len(paquetes) > self.max_len:

            espacio_libre = self.max_len - len(self.queue)
            self.queue.extend(paquetes[:espacio_libre])

            assert len(self.queue) == self.max_len

            return len(paquetes) - (espacio_libre)

        self.queue.extend(paquetes)
        return 0  # No se han descartado paquetes

    def registrar_accion(self, action: Acciones):
        if action == self.current_action:
            self.action_count += 1
        else:
            self.action_count = 1
            self.current_action = action

    def step(self, action_num: int):

        self.descartados = 0 
        action: Acciones = Acciones.int_to_action(action_num)
        self.registrar_accion(action)

        descartados: int = self.packet_input()

        self.procesar_por_tamaño()

        reward: float = self.get_reward(descartados, action)
        self.descartados = descartados
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

        if self.mb_restantes == -1 and len(self.queue) > 0:
            paquete = self.queue[0]
            self.mb_restantes = paquete["SIZE"]

        while tam_procesado < self.rate and len(self.queue) > 0:
            if self.mb_restantes == 0:
                p2 = self.queue.popleft()  # Quita el paquete que se ha procesado
                if len(self.queue) == 0:
                    self.mb_restantes == -1
                    break
                # Nuevo paquete
                paquete = self.queue[0]
                #assert p2 != paquete
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
    def registro_Estados(self):
        return self.maquina.get_Registro()

    def get_reward(self, descartados, action: Acciones) -> float:
        reward = 0.0
        # Penaliza por ocupacion
        #reward -= descartados * 2
        #reward += action.value
        actual: float = self.get_ocupacion()
        #reward += actual*5
        # TODO: hacer que cuanto menos este ocupado y mas este sea peor, y que por el centro sea mejor

        # Penalización severa por descartar paquetes
        # Probar con la lista guardando las recompensas anteriores

        #reward += (1-actual)*10
        # TODO probar a separar la recompensa de descartados por los que son porque no entran o los que son porque se descartan
        c=0.25
        if descartados > 0:
            if action == Acciones.PERMITIR:
                reward -= (descartados**2) * c*2
            else:
                reward -= (descartados) * c
        else:
            reward += 1.0
        """
        c = 100
        if action == Acciones.PERMITIR :
            if descartados == 0:
                reward += c*2
            else:
                reward -= c*4
        else:
            reward -= c*2

        match action:
            case Acciones.PERMITIR:
                reward += 5.0
            case Acciones.DENEGAR:
                reward -= 2.0
            case _:
                reward += 0.0

        if descartados > 0 and action == Acciones.PERMITIR:
            reward -= 100.0
        else:
            reward += 10.0
        """
        return reward
        

