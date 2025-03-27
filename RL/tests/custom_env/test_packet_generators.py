import numpy as np
import pytest
from custom_env.packet_generators import DOS_Packet_Generator, Packet_Generator
from gymnasium.utils import seeding


class TestPacket_Generator:
    def setup_method(self):
        self.seed = 0

        self.generator, self.seed = seeding.np_random(self.seed)
        self.generator2, _ = seeding.np_random(self.seed)

        self.generators: list[Packet_Generator] = [
            Packet_Generator(generator=self.generator),
            DOS_Packet_Generator(generator=self.generator)
        ]

    def test_init(self):
        for generator in self.generators:
            assert generator._np_random == self.generator, "Generator should be the same"
            assert generator.packet, "Packet should not be None"

            assert isinstance(generator.__class__(
            )._np_random, np.random.Generator), "_np_random should be an instance of np.random.Generator"

    def test_generate_packet(self):
        for generator in self.generators:
            packet: dict[str, int] = generator.generate_packet()

            assert isinstance(generator, Packet_Generator)
            assert isinstance(packet, dict), "Packet should be a dictionary"

            assert "SIZE" in packet, "Packet should have SIZE key"

    def test_generate_packets(self):
        for generator in self.generators:
            packets: list[dict[str, int]] = generator.generate_packets()
            assert isinstance(packets, list), "Packets should be a list"
            assert all(isinstance(packet, dict)
                    for packet in packets), "All items in the list should be dictionaries"