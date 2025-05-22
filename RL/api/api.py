from config import Config
from flask import Flask
from src import AppFactory



if __name__ == '__main__':
    app: Flask = AppFactory.create_app()

    app.run(
        host='0.0.0.0',
        port=Config.PORT,
        load_dotenv=False,
        )
