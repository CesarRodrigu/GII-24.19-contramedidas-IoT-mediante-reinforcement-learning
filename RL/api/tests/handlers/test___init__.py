import pytest
from src.handlers import handlers
from src.handlers.error_handlers import (handle_generic_exception,
                                         handle_http_exception)
from werkzeug.exceptions import HTTPException


class TestErrorHandlers:
    def test_handlers_length(self):
        assert len(handlers) == 2, "Handlers list length is incorrect"

    def test_handlers_contains_http_exception_handler(self):

        assert any(isinstance(exception, type) and exception is HTTPException and handler == handle_http_exception
                   for exception, handler in handlers), "HTTPException handler is missing or incorrect"

    def test_handlers_contains_generic_exception_handler(self):

        assert any(isinstance(exception, type) and exception is Exception and handler == handle_generic_exception
                   for exception, handler in handlers), "Generic Exception handler is missing or incorrect"
