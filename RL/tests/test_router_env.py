import pytest
from gymnasium import make
from gymnasium.utils.env_checker import check_env
from custom_env.router_env import RouterEnv

class TestRouterEnv:
    def test_RouterEnv(self):
        env_id = "RouterEnv-v0"
        seed = None
        
        env:RouterEnv = make(env_id, seed=seed)

        check_env(env)