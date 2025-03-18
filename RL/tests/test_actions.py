import pytest
from custom_env.actions import Acciones

def test_acciones():
    for action in Acciones:
        assert action is not None