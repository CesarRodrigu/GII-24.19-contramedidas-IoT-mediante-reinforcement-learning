from copy import deepcopy
from matplotlib.pylab import Generator
import pytest
from unittest.mock import patch, MagicMock
import numpy as np
from gymnasium.utils import seeding
from custom_env.states import MaquinaDeEstados, NormalState, AttackState, Packet_Generator, DOS_Packet_Generator


class TestMaquinaDeEstados:
    def setup_method(self):
        self.generator, self.seed = seeding.np_random(seed=None)
        self.generator2, seed2 = seeding.np_random(seed=self.seed)
        assert self.seed == seed2, "Seeds should be the same"

    def test_init(self):
        self.machine = MaquinaDeEstados(self.generator)
        assert self.generator is not None, "Generator should not be None"
        assert self.machine, "Generator should be the same"
        assert self.machine._np_random == self.generator, "Generator should be the same"
        assert self.machine.get_estado(
        ) == NormalState, f"Initial state should be {NormalState.__name__}"
        assert self.machine.get_registro() == [], "Register should be empty"
        assert self.machine.normal, "Normal packet generator should not be None"
        assert self.machine.DoS, "DoS packet generator should not be None"

    def test_get_random(self):
        for _ in range(10):
            assert self.generator.random() == self.generator2.random()

    def test_get_random_choice(self):
        choices: list[int] = [1, 2, 3, 4, 5]
        self.machine = MaquinaDeEstados(self.generator)
        self.machine2 = MaquinaDeEstados(self.generator2)

        for _ in range(10):
            assert self.machine.get_random_choice(choices) == self.machine2.get_random_choice(
                choices), "Choices should be the same"

    def test_cambiar_estado(self):
        self.machine = MaquinaDeEstados(self.generator)
        self.machine2 = MaquinaDeEstados(self.generator2)

        self.machine.cambiar_estado()
        self.machine2.cambiar_estado()

        assert self.machine.get_estado() == self.machine2.get_estado(), "States should be the same"

    def test_get_registro(self):
        self.machine = MaquinaDeEstados(self.generator)
        assert self.machine.get_registro() == [], "Initial register should be empty"

        self.machine.cambiar_estado()
        assert len(self.machine.get_registro(
        )) == 1, "Register should have one entry after one state change"
        assert self.machine.get_registro()[0] in [
            NormalState.__name__, AttackState.__name__], "Register should contain the state name"

        self.machine.cambiar_estado()
        assert len(self.machine.get_registro(
        )) == 2, "Register should have two entries after two state changes"
        assert self.machine.get_registro()[1] in [
            NormalState.__name__, AttackState.__name__], "Register should contain the state name"
