from enum import Enum


class Color(Enum):
    """Enum for representing colors.

    Args:
        Enum (_type_): Base class for enumerations.
    """
    BLUE = 'tab:blue'
    RED = 'tab:red'


class Location(Enum):
    """Enum for representing locations.

    Args:
        Enum (_type_): Base class for enumerations.
    """
    UPPER_LEFT = 'upper left'
    UPPER_RIGHT = 'upper right'
