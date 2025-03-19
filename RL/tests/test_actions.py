import pytest
from custom_env.actions import Acciones


class TestAcciones:
    def test_acciones(self):
        for action in Acciones:
            assert action is not None, "Acciones should not be None"

    def test_int_to_action(self):
        for i, action in enumerate(Acciones):
            assert Acciones.int_to_action(i) == action , f"Int{i} to action{action} is not correct"

    def test_action_to_int(self):
        for i, action in enumerate(Acciones):
            assert Acciones.action_to_int(action) == i, f"Action {action} to int{i} is not correct"

    def test_get_actions_list(self):
        def key(x): return x.value
        expected: list[Acciones] = sorted(
            [Acciones.PERMITIR, Acciones.DENEGAR], key=key)

        actions_list: list[Acciones] = Acciones._get_actions_list()
        actions_list.sort(key=lambda x: x.value)

        assert actions_list == expected, "Actions list is not correct"
