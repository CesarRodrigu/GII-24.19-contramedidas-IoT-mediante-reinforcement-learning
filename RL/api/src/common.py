from flask import Response
from flask import jsonify


def send_data(type: str, name: str, data: dict | str) -> Response:
    """Send data as a JSON response.

    Args:
        type (str): The type of the data.
        name (str): The name of the data.
        data (dict | str): The data to send.

    Returns:
        Response: The JSON response containing the data.
    """
    return jsonify({
        "success": True,
        "data": {
            "type": type,
            "name": name,
            "content": data
        }
    })
