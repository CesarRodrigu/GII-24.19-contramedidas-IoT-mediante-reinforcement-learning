import pytest
from custom_env.actions import Action


class TestAction:
    def test_action(self):
        for action in Action:
            assert action is not None, "Action should not be None"

    def test_int_to_action(self):
        for i, action in enumerate(Action):
            assert Action.int_to_action(i) == action , f"Int{i} to action{action} is not correct"

    def test_action_to_int(self):
        for i, action in enumerate(Action):
            assert Action.action_to_int(action) == i, f"Action {action} to int{i} is not correct"

    def test_get_actions_list(self):
        def key(x): return x.value
        expected: list[Action] = sorted(
            [Action.ALLOW, Action.DENY], key=key)

        actions_list: list[Action] = Action._get_actions_list()
        actions_list.sort(key=lambda x: x.value)

        assert actions_list == expected, "Actions list is not correct"
