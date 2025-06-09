from flask import jsonify
from flask.wrappers import Response
from werkzeug.exceptions import HTTPException
import traceback


def generate_error_response(code: int, message: str, description: str) -> tuple[Response, int]:
    """Generates an error response for the API.

    Args:
        code (int): The HTTP status code.
        message (str): A short description of the error.
        description (str): A detailed description of the error.

    Returns:
        tuple[Response, int]: The error response and the HTTP status code.
    """    
    print(f"Error {code}: {message} - {description}")
    return jsonify({
        "success": False,
        "error": {
            "code": code,
            "name": message,
            "description": description
        }
    }), code


def handle_http_exception(e: HTTPException) -> tuple[Response, int]:
    """Handles HTTP exceptions for the API.

    Args:
        e (HTTPException): The HTTP exception to handle.

    Returns:
        tuple[Response, int]: The error response and the HTTP status code.
    """    
    return generate_error_response(e.code, e.name, e.description)


def handle_generic_exception(e: Exception) -> tuple[Response, int]:
    """Handles generic exceptions for the API.

    Args:
        e (Exception): The generic exception to handle.

    Returns:
        tuple[Response, int]: The error response and the HTTP status code.
    """    
    print(f"Traceback: {traceback.format_exc()}")
    return generate_error_response(500, "Internal Server Error", str(e))
