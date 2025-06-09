from __future__ import annotations

from abc import ABC, abstractmethod
from collections.abc import Generator
from functools import cache
from typing import override

import numpy as np
from gymnasium.utils import seeding
from .packet_generators import DoSPacketGenerator, PacketGenerator


class StateMachine:
    def __init__(self, random_generator: np.random.Generator = seeding.np_random(seed=None)[0]) -> None:
        self.current_state: type[BaseState] = NormalState
        self.available_states: tuple[type[BaseState]] = (
            NormalState,
            AttackState)
        self.state_registry: list[BaseState] = []

        self._np_random: Generator = random_generator
        self.normal_packet_generator = PacketGenerator(generator=random_generator)
        self.dos_packet_generator = DoSPacketGenerator(generator=random_generator)

    def get_random(self) -> float:
        """Gets a random float value.

        Returns:
            float: A random float value.
        """        
        return self._np_random.random()

    def get_current_state(self) -> BaseState:
        """Gets the current state of the state machine.

        Returns:
            BaseState: The current state of the state machine.
        """        
        return self.current_state

    def get_random_choice(self, choices):
        """Gets a random choice from the given list of choices.

        Args:
            choices (list): A list of choices to select from.

        Returns:
            _type_: The randomly selected choice from the list.
        """        
        return self._np_random.choice(choices)

    def update_state(self) -> None:
        """Updates the state of the state machine.
        """        
        self.current_state.change_state(self)
        self.state_registry.append(self.current_state.__name__)

    def generate_packets(self) -> list[dict[str, int]]:
        """Generates packets based on the current state.

        Returns:
            list[dict[str, int]]: A list of generated packets.
        """        
        if self.current_state == NormalState:
            packets: list[dict[str, int]] = self.normal_packet_generator.generate_packets()
        else:
            packets = self.dos_packet_generator.generate_packets()
        return packets

    def set_state(self, next_state_class: type[BaseState]) -> None:
        """Sets the next state of the state machine.

        Args:
            next_state_class (type[BaseState]): The new state to transition to.
        """        
        self.current_state = next_state_class
        if next_state_class == NormalState:
            self.normal_packet_generator = PacketGenerator(generator=self._np_random)
        elif next_state_class == AttackState:
            self.dos_packet_generator = DoSPacketGenerator(generator=self._np_random)
            
    def get_log(self) -> list[BaseState]:
        """Gets the log of state transitions.

        Returns:
            list[BaseState]: The log of state transitions.
        """        
        return self.state_registry


class BaseState(ABC):
    @classmethod
    def change_state(cls, machine: StateMachine) -> None:
        """Changes the state of the state machine.

        Args:
            machine (StateMachine): The state machine to change the state of.
        """        
        if machine.get_random() < cls.change_prob():
            new_state: BaseState = machine.get_random_choice(
                cls.get_possible_states(machine.available_states))
            machine.set_state(new_state)

    @classmethod
    @abstractmethod
    def change_prob(cls) -> float:
        """Gets the probability of state change.

        Returns:
            float: The probability of state change.
        """        
        pass

    @classmethod
    @cache
    def get_possible_states(cls, states) -> list[BaseState]:
        """Gets the possible states for the state machine.

        Args:
            states (list[BaseState]): The list of available states.

        Returns:
            list[BaseState]: The list of possible states.
        """        
        return list(set(states).difference({cls}))


class NormalState(BaseState):
    @classmethod
    @override
    def change_prob(cls) -> float:
        return 0.1


class AttackState(BaseState):
    @classmethod
    @override
    def change_prob(cls) -> float:
        return 0.05
