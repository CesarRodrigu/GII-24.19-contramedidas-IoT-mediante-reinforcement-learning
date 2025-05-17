import os


class Config:
    """Set Flask configuration vars."""
    TESTING: bool = False
    DEBUG: bool = True
    PORT: int = os.getenv('API_DOCKER_PORT', 5001)
