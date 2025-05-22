from werkzeug.exceptions import HTTPException
from typing import Callable
from src.handlers.error_handlers import handle_generic_exception, handle_http_exception


handlers: list[tuple[Exception, Callable]] = [
    (HTTPException, handle_http_exception),
    (Exception, handle_generic_exception),
]
