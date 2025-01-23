from flask import Flask

from config import DEBUG, HOST, PORT
from src import create_app

if __name__ == "__main__":
    app: Flask = create_app()
    app.run(host=HOST, port=PORT, debug=DEBUG)