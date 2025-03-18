import datetime
print(f"Cargado a las {datetime.datetime.now()}")

from gymnasium.envs.registration import register

register(
    id="RouterEnv-v0",
    entry_point="custom_env.router_env:RouterEnv",
    order_enforce=False,
    disable_env_checker=True,
)