import os


class Config:
    """Set Flask configuration vars."""
    TESTING: bool = False
    DEBUG: bool = False
    PORT: int = os.getenv('API_DOCKER_PORT', 5001)
