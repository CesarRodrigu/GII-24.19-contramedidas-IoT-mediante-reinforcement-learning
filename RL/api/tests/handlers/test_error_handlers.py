import pytest
from flask import Flask
from werkzeug.exceptions import NotFound
from src.handlers.error_handlers import (generate_error_response,
                                         handle_generic_exception, handle_http_exception)
from tests.conf import client, app



class TestErrorHandlers:
    def test_generate_error_response(self, app: Flask):
        with app.test_request_context():
            code = 404
            message = "Not Found"
            description = "The requested resource was not found."

            response, status_code = generate_error_response(code, message, description)

            assert status_code == code
            assert response.json == {
                "success": False,
                "error": {
                    "code": code,
                    "name": message,
                    "description": description
                }
            }
    def test_handle_generic_exception(self, app: Flask):
        with app.test_request_context():

            class CustomException(Exception):
                pass

            exception_message = "This is a test exception"
            test_exception = CustomException(exception_message)

            response, status_code = handle_generic_exception(test_exception)

            assert status_code == 500
            assert response.json == {
                "success": False,
                "error": {
                    "code": 500,
                    "name": "Internal Server Error",
                    "description": exception_message
                }
            }
            
    def test_handle_http_exception(self, app: Flask):

        with app.test_request_context():
            exception = NotFound()

            response, status_code = handle_http_exception(exception)

            assert response, status_code == generate_error_response(exception.code, exception.name, exception.description)

            exception = NotFound("Test")

            response, status_code = handle_http_exception(exception)

            assert response, status_code == generate_error_response(exception.code, exception.name, exception.description)
            


