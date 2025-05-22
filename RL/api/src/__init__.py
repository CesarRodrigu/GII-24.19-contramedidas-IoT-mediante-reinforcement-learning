import secrets

from src.handlers.error_handlers import handle_generic_exception
from config import Config
from flask import Flask, got_request_exception
from src.extensions import api
from src.handlers import handlers
from src.resources import resources

class AppFactory:
    _app_instance = None

    @staticmethod
    def create_app(config_class=Config) -> Flask:
        if AppFactory._app_instance is None:
            app = Flask(__name__)
            app.config.from_object(config_class)

            app.secret_key = secrets.token_hex()
            got_request_exception.connect(handle_generic_exception, app)

            for exception, handler in handlers:
                app.register_error_handler(exception, handler)

            for resource_cls, endpoint in resources:
                print(
                    f"Registering resource: {resource_cls.__name__} with endpoint: {endpoint}")
                api.add_resource(resource_cls, endpoint)

            api.init_app(app)

            AppFactory._app_instance: Flask = app

        return AppFactory._app_instance
