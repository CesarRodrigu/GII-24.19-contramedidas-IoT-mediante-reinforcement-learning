import os
from dotenv import load_dotenv

load_dotenv(verbose=True)

HOST: str = os.getenv('APP_HOST', '0.0.0.0')
PORT: int = int(os.getenv('APP_PORT', 8000))
DEBUG: bool = bool(os.getenv('APP_DEBUG', False))
