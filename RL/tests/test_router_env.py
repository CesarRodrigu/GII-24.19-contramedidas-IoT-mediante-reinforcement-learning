import pytest
from gymnasium import make
from gymnasium.utils.env_checker import check_env


def test_router_env():
    env_id = "RouterEnv-v0"
    seed = None
    
    env = make(env_id, seed=seed)

    check_env(env)