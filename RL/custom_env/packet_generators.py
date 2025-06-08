import numpy as np
from gymnasium.spaces import Box, Dict
from gymnasium.utils import seeding
from numpy.random import Generator


class PacketGenerator():
    sizes: list[tuple[int, int]] = [
        (64, 128),
        (128, 256),
        (256, 1024),
        (1024, 1500)]
    probs: list[float] = [0.5,
                          0.2,
                          0.15,
                          0.15]

    def __init__(self, min_ip: int = 0, max_ip: int = 2000,
                 min_port: int = 0, max_port: int = 4000,
                 min_protocol: int = 0, max_protocol: int = 100,
                 min_rate: int = 4, max_rate: int = 24,  # Paquetes/segundo
                 step_dur: float = 1e-3,  # En segundos,
                 generator: np.random.Generator = None
                 ):

        assert np.sum(self.probs) == 1
        assert len(self.sizes) == len(self.probs)
        assert min_rate <= max_rate
        if generator is not None:
            self._np_random: Generator = generator
        else:
            self._np_random, _ = seeding.np_random()
        indice: int = self._np_random.choice(len(self.sizes), p=self.probs)

        min_size, max_size = self.sizes[indice]
        self.step_dur: float = step_dur

        self.packet = Dict({
            "IP":       Box(low=min_ip, high=max_ip, shape=(), dtype=int,
                            seed=self._np_random),
            "PORT":     Box(low=min_port, high=max_port, shape=(), dtype=int,
                            seed=self._np_random),
            "PROTOCOL": Box(low=min_protocol, high=max_protocol, shape=(), dtype=int,
                            seed=self._np_random),
            "SIZE":     Box(low=min_size, high=max_size, shape=(), dtype=int,
                            seed=self._np_random)  # En bytes
        })

        self.min_rate: float = min_rate*step_dur
        self.max_rate: float = max_rate*step_dur

        self.rate: float = self._np_random.uniform(
            self.min_rate, self.max_rate)

    def generate_packet(self) -> dict[str, int]:
        return self.packet.sample()

    def generate_packets(self) -> list[dict[str, int]]:
        # Como el rate sería por segundo habría que transformarlo a la unidad deseada
        if self.min_rate < 1:
            if self._np_random.random() <= self.rate:
                num_packets: int = 1
            else:
                return []
        else:
            num_packets: int = int(np.round(self.rate))

        return [self.generate_packet() for _ in range(num_packets)]


class DoSPacketGenerator(PacketGenerator):
    sizes: list[tuple[int, int]] = [(64, 128),
                                    (128, 256),
                                    (256, 1024),
                                    (1024, 1500)]
    probs: list[float] = [0.8,
                          0.15,
                          0.03,
                          0.02]

    def __init__(self,
                 min_ip=0, max_ip=2000,
                 min_port=0, max_port=4000,
                 min_protocol=0, max_protocol=100,
                 min_rate=10_000, max_rate=10_300,
                 step_dur=1e-3,  # En segundos,
                 generator: np.random.Generator = None
                 ):
        if generator is not None:
            self._np_random: Generator = generator
        else:
            self._np_random, _ = seeding.np_random()

        ip: int = self._np_random.integers(min_ip, max_ip)
        super().__init__(ip, ip,
                         min_port, max_port,
                         min_protocol, max_protocol,
                         min_rate, max_rate,
                         step_dur,
                         self._np_random
                         )
