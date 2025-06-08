from flask import Response
from flask import jsonify


def send_data(type: str, name: str, data: dict | str) -> Response:
    return jsonify({
        "success": True,
        "data": {
            "type": type,
            "name": name,
            "content": data
        }
    })
