from collections import deque
from typing import Literal
import pytest
from gymnasium import make
from gymnasium.utils.env_checker import check_env
from custom_env.router_env import RouterEnv
from custom_env.actions import Acciones
from custom_env.states import BaseState


class TestRouterEnv:
    def setup_method(self):
        env_id = "RouterEnv-v0"
        seed = None

        self.env: RouterEnv = make(env_id, seed=seed)

    def test_routerenv(self):

        check_env(self.env)

        with pytest.raises(ValueError, match="max_len must be greater than 0"):
            RouterEnv(max_len=0)
        with pytest.raises(ValueError, match="max_len must be greater than 0"):
            RouterEnv(max_len=0-1)

    def test_get_reward(self):
        allow_0: float = self.env.get_reward(0, Acciones.PERMITIR)
        deny_0: float = self.env.get_reward(0, Acciones.DENEGAR)

        allow_1: float = self.env.get_reward(1, Acciones.PERMITIR)
        deny_1: float = self.env.get_reward(1, Acciones.DENEGAR)

        allow_100: float = self.env.get_reward(100, Acciones.PERMITIR)
        deny_100: float = self.env.get_reward(100, Acciones.DENEGAR)

        rewards: list[float] = [allow_0, deny_0,
                                allow_1, deny_1,
                                allow_100, deny_100]

        assert max(
            rewards) == allow_0, "Max reward should be when 0 discarded packets and action is allow"
        assert min(
            rewards) == allow_100, "Min reward should be when 100 discarded packets and action is allow"

        assert allow_0 >= deny_0, "Allow action should have higher or equal reward than deny action when 0 discarded packets"
        assert allow_1 < deny_1, "Allow action should have lower reward than deny action when 1 discarded packets"
        assert allow_100 < deny_100, "Allow action should have lower reward than deny action when 100 discarded packets"

    def test_registro_estados(self):
        estados: list[BaseState] = self.env.registro_Estados()

        assert isinstance(
            estados, list), "registro_Estados should return a list"
        assert all(isinstance(estado, BaseState)
                   for estado in estados), "All items in the list should be instances of BaseState"

    def test_procesar_por_tamaño(self):
        assert len(self.env.queue) == 0, "Initial queue should be empty"
        self.env.reset()
        assert len(self.env.queue) == 0, "Initial queue should be empty"
        self.env.procesar_por_tamaño()
        assert len(self.env.queue) == 0, "Queue should still be empty"

        assert self.env.mb_restantes == -1.0, "Initial Remaining MB should be -1.0"

        tam: float = self.env.get_tam_ocu()
        assert tam >= 0, "Tam should be non-negative"

    def test_get_tam_ocu(self):
        def compare_floats(a: float, b: float, epsilon: float = 1e-6) -> bool:
            return abs(a - b) < epsilon

        assert compare_floats(self.env.get_tam_ocu(
        ), 0.0), "Total size should be 0.0 when queue is empty"
        self.env.reset()
        assert compare_floats(self.env.get_tam_ocu(
        ), 0.0), "Total size should be 0.0 when queue is empty"

        packets: list[dict[str, float]] = [
            {"SIZE": 100.0}, {"SIZE": 200.0}, {"SIZE": 300.0}]
        self.env.packet_input(packets)

        sizes: list[float] = [packet["SIZE"] for packet in packets]
        expected_size: float | Literal[0] = sum(sizes)

        assert compare_floats(self.env.get_tam_ocu(
        ), expected_size), f"Total size should be {expected_size} when queue has {len(packets)} packets of sizes {sizes}"
        assert self.env.rate > 0, "Rate should be greater than 0"

        self.env.procesar_por_tamaño()

        assert self.env.get_tam_ocu(
        ) < expected_size, "Total size should lower than before after processing"
