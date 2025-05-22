from .. import api
import pytest

def test_api():
    assert api is not None, "API module is not imported correctly"