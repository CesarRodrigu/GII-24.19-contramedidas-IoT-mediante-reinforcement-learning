import os
import sys
from custom_env.router_env import RouterEnv, train, get_train_graph

name = "./models/Example"
logs_path = r"C:\Users\cesar\OneDrive - Universidad de Burgos\TFG\Repo\GII-24.19-contramedidas-IoT-mediante-reinforcement-learning\RL\logs\train\\"
models_path = "./models/"
env_id = "RouterEnv-v0"

print(os.getcwd())

get_train_graph(logs_path, save=True)