from flask import jsonify
from flask.wrappers import Response
from werkzeug.exceptions import HTTPException
import traceback


def generate_error_response(code: int, message: str, description: str) -> tuple[Response, int]:
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
    return generate_error_response(e.code, e.name, e.description)


def handle_generic_exception(e: Exception) -> tuple[Response, int]:
    print(f"Traceback: {traceback.format_exc()}")
    return generate_error_response(500, "Internal Server Error", str(e))
