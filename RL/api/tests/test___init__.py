import os
import sys
import pytest

def test_sys_path_modified():
    test_path = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", ".."))
    assert test_path in sys.path