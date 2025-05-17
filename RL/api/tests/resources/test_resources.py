import pytest
from flask.testing import FlaskClient
from api.tests.conf import app, client
from werkzeug.test import TestResponse
from werkzeug.exceptions import NotFound, MethodNotAllowed


def validate_response(response: TestResponse) -> TestResponse:
    if response:
        assert response.is_json, "Response is not JSON"
        response_json = response.get_json()

        assert isinstance(
            response_json, dict), "Response is not a valid JSON object"
        assert "success" in response_json, "Response does not contain 'success' key"
        assert isinstance(
            response_json["success"], bool), "The 'success' key is not a boolean value"

        if response_json["success"]:
            data = response_json["data"]
            assert isinstance(
                data, dict), "Data is not a valid JSON object"
            assert "type" in data, "Data does not contain 'type' key"
            assert isinstance(
                data["type"], str), "The 'type' key is not a string value"
            assert "name" in data, "Data does not contain 'name' key"
            assert isinstance(
                data["name"], str), "The 'name' key is not a string value"
            assert "content" in data, "Data does not contain 'content' key"
            assert isinstance(
                data["content"], (dict, str)), "The 'content' key is not a valid type"
        else:
            error = response_json["error"]
            assert isinstance(
                error, dict), "Error is not a valid JSON object"
            assert "code" in error, "Error does not contain 'code' key"
            assert isinstance(
                error["code"], int), "The 'code' key is not an integer value"
            assert "name" in error, "Error does not contain 'name' key"
            assert isinstance(
                error["name"], str), "The 'name' key is not a string value"
            assert "description" in error, "Error does not contain 'description' key"
            assert isinstance(
                error["description"], str), "The 'description' key is not a string value"
    else:
        assert response is not None, "Response is None"


class TestEndpoints:
    def test_none_response(self, client: FlaskClient) -> TestResponse:
        with pytest.raises(AssertionError, match="Response is None"):
            validate_response(None)

    def test_request_example(self, client: FlaskClient) -> TestResponse:
        response: TestResponse = client.get("/posts")

        validate_response(response)

    def test_get_not_existing_endpoint(self, client: FlaskClient) -> TestResponse:
        response: TestResponse = client.get("/getNotExistingEndpoint")
        assert response.status_code == 404
        validate_response(response)
        assert response.get_json(
        )["error"]["description"] == NotFound.description

   

    def test_post_get_monitor(self, client: FlaskClient) -> TestResponse:
        response: TestResponse = client.post("/getMonitor/1")
        assert response.status_code == 405
        validate_response(response)
        assert response.get_json(
        )["error"]["description"] == MethodNotAllowed.description
    
    def test_get_monitor(self, client: FlaskClient) -> TestResponse:
        response: TestResponse = client.get("/getMonitor/1")
        assert response.status_code == 200
        validate_response(response)
        assert response.get_json(
        )["data"]["type"] == "modelStats"
        assert response.get_json(
        )["data"]["name"] == "monitor.csv"

    def test_get_progress(self, client: FlaskClient) -> TestResponse:
        response: TestResponse = client.get("/getProgress/1")
        assert response.status_code == 200
        validate_response(response)
        assert response.get_json(
        )["data"]["type"] == "trainingStats"
        assert response.get_json(
        )["data"]["name"] == "progress.csv"

    def test_get_TrainedModel(self, client: FlaskClient) -> TestResponse:
        response: TestResponse = client.get("/getTrainedModel/1")
        assert response.status_code == 200
        validate_response(response)
        assert response.get_json(
        )["data"]["type"] == "trained_model"
        assert response.get_json(
        )["data"]["name"] == "Example.zip"

