from __future__ import annotations

from abc import ABC, abstractmethod
from functools import cache
from typing import override

import numpy as np
from gymnasium.utils import seeding
from .packet_generators import DOS_Packet_Generator, Packet_Generator


class MaquinaDeEstados:
    def __init__(self, generador: np.random.Generator = seeding.np_random(seed=None)[0]):
        self.estado: BaseState = NormalState
        self.estados_posibles: tuple[type[BaseState]] = (
            NormalState,
            AttackState)
        self.registro_estados: list[BaseState] = []
        self._np_random = generador
        self.normal = Packet_Generator(seed=None)
        self.DoS = DOS_Packet_Generator(seed=None)

    def get_random(self):
        return self._np_random.random()

    def get_estado(self):
        return self.estado

    def get_random_choice(self, choices):
        return self._np_random.choice(choices)

    def cambiar_estado(self):
        self.estado.cambiar(self)
        self.registro_estados.append(self.estado.__name__)

    def generate_packets(self):
        if self.estado == NormalState:
            paquetes = self.normal.generate_packets()
        else:
            paquetes = self.DoS.generate_packets()
        return paquetes
    def get_Registro(self) -> list[BaseState]:
        return self.registro_estados


class BaseState(ABC):
    @classmethod
    @abstractmethod
    def cambiar(cls, maquina: MaquinaDeEstados):
        """Método de clase que cambiará el estado"""
        if maquina.get_random() < cls.probCambiar():
            # Elegir aleatoriamente un nuevo estado diferente al actual
            new_state: BaseState = maquina.get_random_choice(
                cls.get_estados_posibles(maquina.estados_posibles))
            maquina.estado = new_state

    @classmethod
    @abstractmethod
    def probCambiar(cls) -> float:
        """Método de clase que devuelve la probabilidad de cambio de estado"""
        pass

    @classmethod
    @abstractmethod
    @cache
    def get_estados_posibles(cls, estados) -> list[BaseState]:
        """Método de clase que devuelve los estados posibles"""
        return list(set(estados).difference({cls}))


class NormalState(BaseState):
    @classmethod
    @override
    def probCambiar(cls):
        return 0.1


class AttackState(BaseState):
    @classmethod
    @override
    def probCambiar(cls):
        return 0.05

