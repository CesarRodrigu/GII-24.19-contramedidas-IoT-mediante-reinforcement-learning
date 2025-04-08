import pytest
from custom_env.states import (AttackState, BaseState, MaquinaDeEstados,
                               NormalState)
from gymnasium.utils import seeding


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
        assert self.machine.dos, "DoS packet generator should not be None"

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

    def test_generate_packets(self):
        self.machine = MaquinaDeEstados(self.generator)

        packets: list[dict[str, int]] = self.machine.generate_packets()
        assert isinstance(packets, list), "Packets should be a list"
        self.machine.estado = AttackState

        packets = self.machine.generate_packets()
        assert isinstance(packets, list), "Packets should be a list"


class TestBaseState:
    def setup_method(self):
        self.generator, self.seed = seeding.np_random(seed=None)

    def test_cambiar(self):
        self.machine = MaquinaDeEstados(self.generator)

        prev_state: BaseState = self.machine.get_estado()
        prev_state.prob_cambiar = lambda: 1.0

        for _ in range(10):
            prev_state.cambiar(self.machine)
            state: BaseState = self.machine.get_estado()
            assert state != prev_state, "State should be different from previous state"
            prev_state = state
            assert issubclass(
                state, BaseState), "State should be an instance of BaseState"
            assert prev_state.prob_cambiar() > 0, "Probability should be greater than 0"
            prev_state.prob_cambiar = lambda: 1.0

    def test_prob_cambiar(self):
        assert BaseState.prob_cambiar() is None, "Default probability should be None"

    def test_get_estados_posibles(self):
        states: tuple[type[NormalState], type[AttackState]] = (
            NormalState,
            AttackState)
        size: int = len(states)
        for state in states:
            assert state in states, "State should be in the list of states"

            assert len(state.get_estados_posibles(states)) == size - \
                1, "Default implementation should return an empty list"
            assert state.get_estados_posibles(states) == [
                s for s in states if s != state], "Default implementation should return the others states"
            
