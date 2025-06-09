from __future__ import annotations

from collections import deque
from typing import Optional

import gymnasium as gym
import numpy as np
from gymnasium.spaces import Box, Dict, Discrete
from gymnasium.utils import seeding


from .actions import Action
from .states import BaseState, StateMachine


class RouterEnv(gym.Env):
    """Router environment for simulating packet routing.

    Args:
        gym (_type_): Base class for the environment.

    Raises:
        ValueError: If max_len is less than 1.

    Returns:
        RouterEnv: Router environment instance.
    """
    total_time: float = 400.0  # Num steps

    def __init__(self, max_len=250, seed: Optional[int] = None) -> None:

        super(RouterEnv, self).__init__()
        if max_len < 1:
            raise ValueError("max_len must be greater than 0")

        self.max_len: int = max_len

        duration_step: float = 1.0
        duration_step *= 1e-3  # In seconds
        processing_speed: float = 5e6/8  # bytes per second of processing
        self.rate: float = processing_speed * \
            duration_step  # bytes per step of processing

        self._np_random, self._np_random_seed = seeding.np_random(seed)
        self._set_initial_values()

        self.observation_space = Dict({
            "OcupacionCola": Box(low=0, high=1, dtype=np.float32),
            "Descartados": Box(low=0, high=np.inf, dtype=np.int16),
        })
        self.action_space = Discrete(len(Action))

    def get_seed(self) -> int | None:
        """Gets the random seed for the environment.

        Returns:
            int | None: The random seed for the environment.
        """
        return self._np_random_seed

    def _set_initial_values(self, ) -> None:
        """Sets the initial values for the environment.
        """

        self.queue = deque(maxlen=self.max_len)
        self.discarded_packets: int = 0
        self.current_action: Action = Action.ALLOW
        self.action_count: int = 1
        self.elapsed_time_units: float = 0.0
        self.remaining_mb: float = -1.0

        self.last_ocupacion: float = 0.0

        self.state_machine = StateMachine(self._np_random)

    def reset(self, seed: Optional[int] = None, options: Optional[dict] = None):
        if seed is not None and seed != self._np_random_seed:
            self._np_random, self._np_random_seed = seeding.np_random(seed)
        self._set_initial_values()

        observation = self._get_obs()
        info = self._get_info()
        return observation, info

    def _get_obs(self):
        """Gets the current observation of the environment.

        Returns:
            dict[str, np.ndarray]: A dictionary representing the current observation.
        """
        return {
            "OcupacionCola": np.array([self.get_occupancy()], dtype=np.float32),
            "Descartados": np.array([self.discarded_packets], dtype=np.int16),
        }

    def _get_info(self):
        """Gets the current information of the environment.

        Returns:
            dict[str, any]: A dictionary representing the current information.
        """
        return {"Stats": {
            "EstadoMaquina": self.state_machine.get_current_state().__name__,
            "NumPaquetes":  len(self.queue),
            "TamañoTotal": self.get_size_ocu(),
            "Action": self.current_action,
            "OcupacionActual": self.get_occupancy(),
            "Descartados": self.discarded_packets,
        },
        }

    def get_size_ocu(self) -> float:
        """Gets the total size of the packets in the queue.

        Returns:
            float: The total size of the packets in the queue.
        """
        total_packet_size = 0.0
        for packet in self.queue:
            total_packet_size += float(packet["SIZE"])
        return total_packet_size

    def packet_input(self, input: list[dict[str, any]] = None) -> int:
        """Processes incoming packets and updates the queue.

        Args:
            input (list[dict[str, any]], optional): A list of packets to process. Defaults to None.

        Returns:
            int: The number of packets discarded.
        """
        if input is not None:
            packets = input
        else:
            packets = self.state_machine.generate_packets()
            self.state_machine.update_state()

        if self.current_action == Action.DENY:
            return len(packets)

        if len(self.queue) + len(packets) > self.max_len:

            available_space = self.max_len - len(self.queue)
            self.queue.extend(packets[:available_space])

            assert len(self.queue) == self.max_len

            return len(packets) - (available_space)

        self.queue.extend(packets)
        return 0

    def log_action(self, action: Action) -> None:
        """Logs the action taken in the environment.

        Args:
            action (Action): The action taken in the environment.
        """
        if action == self.current_action:
            self.action_count += 1
        else:
            self.action_count = 1
            self.current_action = action

    def step(self, action_num: int):
        """Performs a step in the environment.

        Args:
            action_num (int): The action number to perform.

        Returns:
            dict[str, np.ndarray]: A dictionary representing the current observation.
        """

        self.discarded_packets = 0
        action: Action = Action.int_to_action(action_num)
        self.log_action(action)

        dropped_packets: int = self.packet_input()

        self.process_by_size()

        reward: float = self.get_reward(dropped_packets, action)
        self.discarded_packets = dropped_packets
        self.last_ocupacion = self.get_occupancy()

        observation = self._get_obs()
        truncated = False
        info = self._get_info()

        self.elapsed_time_units += 1
        finished: bool = self._is_finished_execution()

        return observation, reward, finished, truncated, info

    def get_occupancy(self) -> float:
        """Gets the current occupancy of the queue.

        Returns:
            float: The current occupancy of the queue.
        """
        return len(self.queue) / self.max_len

    def process_by_size(self) -> None:
        """Processes the packets in the queue by size.
        """

        if len(self.queue) == 0:
            return

        processed_size = 0.0

        while processed_size < self.rate and len(self.queue) > 0:
            if self.remaining_mb == 0:
                self.queue.popleft()
                if len(self.queue) == 0:
                    break
                paquete = self.queue[0]
                self.remaining_mb = paquete["SIZE"]
            else:
                procesado_local: float = min(self.remaining_mb,
                                             self.rate-processed_size)
                self.remaining_mb -= procesado_local
                processed_size += procesado_local

    def _is_finished_execution(self) -> bool:
        """Checks if the execution is finished.

        Returns:
            bool: True if the execution is finished, False otherwise.
        """
        return self.elapsed_time_units >= self.total_time

    def close(self) -> None:
        """Closes the environment and releases resources.
        """
        return super().close()

    def get_state_records(self) -> list[BaseState]:
        """Gets the state records from the environment.

        Returns:
            list[BaseState]: The state records from the environment.
        """
        return self.state_machine.get_log()

    def get_reward(self, discarded_packets: int, action: Action) -> float:
        """Calculates the reward for the given action and discarded packets.

        Args:
            descartados (int): The number of discarded packets.
            action (Action): The action taken.

        Returns:
            float: The calculated reward.
        """
        return reward(discarded_packets,  action, self.get_occupancy(), self.last_ocupacion)


def reward(discarded: int,
           action: Action,
           ocu_actual: float = 0.0,
           ocu_ant: float = 0.0,
           c: float = 0.35,
           c2: float = 0.2,
           c3: float = 0.01,
           c4: float = 7.5,
           c5: float = 1.0,
           ) -> float:
    """Calculates the reward for the given action and discarded packets.

    Args:
        discarded (int): The number of discarded packets.
        action (Action): The action taken.
        ocu_actual (float, optional): The current occupancy. Defaults to 0.0.
        ocu_ant (float, optional): The previous occupancy. Defaults to 0.0.
        c (float, optional): Constant. Defaults to 0.35.
        c2 (float, optional): Constant. Defaults to 0.2.
        c3 (float, optional): Constant. Defaults to 0.01.
        c4 (float, optional): Constant. Defaults to 7.5.
        c5 (float, optional): Constant. Defaults to 1.0.

    Returns:
        float: The calculated reward.
    """
    reward = 0.0
    if discarded > 0:
        if action == Action.ALLOW:
            reward -= (discarded**2) * c
        else:
            reward -= (discarded) * c2

        mejora: float = ocu_ant - ocu_actual
        reward += mejora * ocu_actual * c3
    else:
        if action == Action.ALLOW:
            reward += (1.0 - ocu_actual) * c4 + c5
        else:
            reward += (1.0 - ocu_actual) * c4
    return round(reward, 2)
