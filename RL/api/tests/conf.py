import os
import sys
from typing import Any, Generator

import pytest
from config import Config
from flask import Flask
from flask.testing import FlaskClient, FlaskCliRunner
from src import AppFactory

sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))


BASE_DIRECTORY: str = os.path.abspath(os.path.dirname(__file__))


@pytest.fixture()
def app() -> Generator[Flask, Any, None]:
    config = Config()

    config.TESTING = True

    app: Flask = AppFactory.create_app(config)

    yield app


@pytest.fixture()
def client(app: Flask) -> FlaskClient:
    return app.test_client()