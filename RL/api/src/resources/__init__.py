from flask_restful import Resource
from src.resources.files import Progress, Monitor,  TrainedModel


resources: list[tuple[type[Resource], str]] = [
    (Monitor, '/getMonitor/<int:modelid>'),
    (Progress, '/getProgress/<int:modelid>'),
    (TrainedModel, '/getTrainedModel/<int:userid>')
]
