from __future__ import annotations

from abc import ABC, abstractmethod
from functools import cache
from typing import override

import numpy as np
from gymnasium.utils import seeding
from matplotlib.pylab import Generator

from .packet_generators import DoSPacketGenerator, PacketGenerator


class MaquinaDeEstados:
    def __init__(self, generador: np.random.Generator = seeding.np_random(seed=None)[0]) -> None:
        self.estado: type[BaseState] = NormalState
        self.estados_posibles: tuple[type[BaseState]] = (
            NormalState,
            AttackState)
        self.registro_estados: list[BaseState] = []

        self._np_random: Generator = generador
        self.normal = PacketGenerator(generator=generador)
        self.dos = DoSPacketGenerator(generator=generador)

    def get_random(self) -> float:
        return self._np_random.random()

    def get_estado(self) -> BaseState:
        return self.estado

    def get_random_choice(self, choices):
        return self._np_random.choice(choices)

    def cambiar_estado(self) -> None:
        self.estado.cambiar(self)
        self.registro_estados.append(self.estado.__name__)

    def generate_packets(self) -> list[dict[str, int]]:
        if self.estado == NormalState:
            paquetes: list[dict[str, int]] = self.normal.generate_packets()
        else:
            paquetes = self.dos.generate_packets()
        return paquetes

    def set_estado(self, estado: type[BaseState]) -> None:
        self.estado = estado
        if estado == NormalState:
            self.normal = PacketGenerator(generator=self._np_random)
        elif estado == AttackState:
            self.dos = DoSPacketGenerator(generator=self._np_random)
            
    def get_registro(self) -> list[BaseState]:
        return self.registro_estados


class BaseState(ABC):
    @classmethod
    def cambiar(cls, maquina: MaquinaDeEstados) -> None:
        """Método de clase que cambiará el estado"""
        if maquina.get_random() < cls.prob_cambiar():
            # Elegir aleatoriamente un nuevo estado diferente al actual
            new_state: BaseState = maquina.get_random_choice(
                cls.get_estados_posibles(maquina.estados_posibles))
            maquina.set_estado(new_state)

    @classmethod
    @abstractmethod
    def prob_cambiar(cls) -> float:
        """Método de clase que devuelve la probabilidad de cambio de estado"""
        pass

    @classmethod
    @cache
    def get_estados_posibles(cls, estados) -> list[BaseState]:
        """Método de clase que devuelve los estados posibles"""
        return list(set(estados).difference({cls}))


class NormalState(BaseState):
    @classmethod
    @override
    def prob_cambiar(cls) -> float:
        return 0.1


class AttackState(BaseState):
    @classmethod
    @override
    def prob_cambiar(cls) -> float:
        return 0.05
