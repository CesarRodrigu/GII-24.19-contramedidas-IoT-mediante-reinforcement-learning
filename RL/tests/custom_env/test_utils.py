import pytest
from custom_env.utils import Color, Location


class TestColor:
    def test_color_enum(self):

        assert len(Color) > 0

        assert all(isinstance(color.value, str) for color in Color)

class TestLocation:
    def test_location_enum(self):
        assert len(Location) > 0

        assert all(isinstance(location.value, str) for location in Location)
