from enum import Enum
from functools import cache


class Action(Enum):
    """Enum for representing action in the environment.

    Args:
        Enum (_type_): Base class for enumerations.

    Returns:
        _type_: Enum class representing action.
    """
    ALLOW = 1
    DENY = -1

    @classmethod
    def int_to_action(cls, action: int) -> "Action":
        """Convert an integer to an action.

        Args:
            action (int): The integer representation of the action.

        Returns:
            Action: The corresponding action.
        """
        return cls._get_actions_list()[action]

    @classmethod
    @cache
    def _get_actions_list(cls) -> list["Action"]:
        """Get the list of all actions.

        Returns:
            list[Action]: The list of all actions.
        """
        return list(Action)

    @classmethod
    @cache
    def action_to_int(cls, action: "Action") -> int:
        """Convert an action to its integer representation.

        Args:
            action (Action): The action to convert.

        Returns:
            int: The integer representation of the action.
        """
        return cls._get_actions_list().index(action)
