import pytest
from custom_env.states import (AttackState, BaseState, StateMachine,
                               NormalState)
from gymnasium.utils import seeding


class TestStateMachine:
    def setup_method(self):
        self.generator, self.seed = seeding.np_random(seed=None)
        self.generator2, seed2 = seeding.np_random(seed=self.seed)
        assert self.seed == seed2, "Seeds should be the same"

    def test_init(self):
        self.machine = StateMachine(self.generator)
        assert self.generator is not None, "Generator should not be None"
        assert self.machine, "Generator should be the same"
        assert self.machine._np_random == self.generator, "Generator should be the same"
        assert self.machine.get_current_state(
        ) == NormalState, f"Initial state should be {NormalState.__name__}"
        assert self.machine.get_log() == [], "Register should be empty"
        assert self.machine.normal_packet_generator, "Normal packet generator should not be None"
        assert self.machine.dos_packet_generator, "DoS packet generator should not be None"

    def test_get_random(self):
        for _ in range(10):
            assert self.generator.random() == self.generator2.random()

    def test_get_random_choice(self):
        choices: list[int] = [1, 2, 3, 4, 5]
        self.machine = StateMachine(self.generator)
        self.machine2 = StateMachine(self.generator2)

        for _ in range(10):
            assert self.machine.get_random_choice(choices) == self.machine2.get_random_choice(
                choices), "Choices should be the same"

    def test_update_state(self):
        self.machine = StateMachine(self.generator)
        self.machine2 = StateMachine(self.generator2)

        self.machine.update_state()
        self.machine2.update_state()

        assert self.machine.get_current_state() == self.machine2.get_current_state(), "States should be the same"

    def test_get_log(self):
        self.machine = StateMachine(self.generator)
        assert self.machine.get_log() == [], "Initial register should be empty"

        self.machine.update_state()
        assert len(self.machine.get_log(
        )) == 1, "Register should have one entry after one state change"
        assert self.machine.get_log()[0] in [
            NormalState.__name__, AttackState.__name__], "Register should contain the state name"

        self.machine.update_state()
        assert len(self.machine.get_log(
        )) == 2, "Register should have two entries after two state changes"
        assert self.machine.get_log()[1] in [
            NormalState.__name__, AttackState.__name__], "Register should contain the state name"

    def test_generate_packets(self):
        self.machine = StateMachine(self.generator)

        packets: list[dict[str, int]] = self.machine.generate_packets()
        assert isinstance(packets, list), "Packets should be a list"
        self.machine.current_state = AttackState

        packets = self.machine.generate_packets()
        assert isinstance(packets, list), "Packets should be a list"


class TestBaseState:
    def setup_method(self):
        self.generator, self.seed = seeding.np_random(seed=None)

    def test_change_prob(self):
        self.machine = StateMachine(self.generator)

        prev_state: BaseState = self.machine.get_current_state()
        prev_state.change_prob = lambda: 1.0

        for _ in range(10):
            prev_state.change_state(self.machine)
            state: BaseState = self.machine.get_current_state()
            assert state != prev_state, "State should be different from previous state"
            prev_state = state
            assert issubclass(
                state, BaseState), "State should be an instance of BaseState"
            assert prev_state.change_prob() > 0, "Probability should be greater than 0"
            prev_state.change_prob = lambda: 1.0

    def test_change_prob(self):
        assert BaseState.change_prob() is None, "Default probability should be None"

    def test_get_possible_states(self):
        states: tuple[type[NormalState], type[AttackState]] = (
            NormalState,
            AttackState)
        size: int = len(states)
        for state in states:
            assert state in states, "State should be in the list of states"

            assert len(state.get_possible_states(states)) == size - \
                1, "Default implementation should return an empty list"
            assert state.get_possible_states(states) == [
                s for s in states if s != state], "Default implementation should return the others states"
            
