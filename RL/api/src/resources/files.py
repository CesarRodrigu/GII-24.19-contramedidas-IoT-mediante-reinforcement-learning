import base64
import os

from flask import Response
from flask_restful import Resource, reqparse
from src.common import send_data

data_directory: str = os.path.join(
    os.path.dirname(__file__), '..', '..', 'data')
data_directory = os.path.abspath(data_directory)


parser = reqparse.RequestParser()
parser.add_argument('name', required=True, type=str,
                    help="Name cannot be blank")

print("Data directory:", data_directory)


class TrainedModel(Resource):
    def get(self, userid: int) -> Response:
        name: str = 'Example.zip'
        print("User ID:", userid)
        namefile: str = os.path.join(data_directory, name)
        with open(namefile, "rb") as f:
            encoded: str = base64.b64encode(f.read()).decode('utf-8')
        return send_data("trained_model", name, encoded)


class File_Stats(Resource):
    NAME: str = 'file.csv'
    TYPE: str = "stats"

    def get(self, modelid: int) -> Response:
        print("Model ID:", modelid)
        namefile: str = os.path.join(data_directory, self.NAME)
        with open(namefile, "rb") as f:
            encoded: str = base64.b64encode(f.read()).decode('utf-8')
        return send_data(self.TYPE, self.NAME, encoded)


class Monitor(File_Stats):
    NAME: str = 'monitor.csv'
    TYPE: str = "modelStats"


class Progress(File_Stats):
    NAME: str = 'progress.csv'
    TYPE: str = "trainingStats"
