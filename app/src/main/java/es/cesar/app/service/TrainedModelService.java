package es.cesar.app.service;

import es.cesar.app.dto.response.Response;
import es.cesar.app.model.TrainedModel;
import es.cesar.app.model.User;
import es.cesar.app.repository.TrainedModelRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Collection;
import java.util.Objects;

@Service
public class TrainedModelService {
    private final TrainedModelRepository trainedModelRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public TrainedModelService(TrainedModelRepository trainedModelRepository) {
        this.trainedModelRepository = trainedModelRepository;
        this.restTemplate = new RestTemplate();
    }

    @Transactional
    public void requestTrainedModelToFlask(User user, String modelName) {
        String url = System.getenv("API_URL");

        if (url == null) {
            url = "http://localhost:5000/";
        }
        url += "/getTrainedModel/" + user.getId();
        ResponseEntity<Response> response = restTemplate.getForEntity(url, Response.class);
        Response responseData = response.getBody();
        if (!Objects.requireNonNull(responseData).isSuccess()) {
            return;
        }
        if (!Objects.equals(responseData.getType(), "trained_model")) {
            return;
        }
        if (!response.getStatusCode().is2xxSuccessful()) {
            return;
        }
        String content = responseData.getContent();

        byte[] fileBytes = Base64.getDecoder().decode(content);

        createModel(user, modelName, fileBytes);
    }

    @Transactional
    public void createModel(User user, String name, byte[] file) {
        TrainedModel model = new TrainedModel();
        model.initializeWithName(user, name);
        model.setFile(file);
        trainedModelRepository.save(model);
    }

    public Collection<TrainedModel> getTrainedModelsByUser(User user) {
        return trainedModelRepository.findByUser(user);
    }

    public void deleteTrainedModelById(Long idTrainedModel) {
        trainedModelRepository.deleteById(idTrainedModel);
    }

    public TrainedModel getTrainedModelById(Long idTrainedModel) {
        return trainedModelRepository.findById(idTrainedModel).orElse(null);
    }

    public TrainedModel updateTrainedModel(TrainedModel trainedModel) {
        return trainedModelRepository.save(trainedModel);
    }

    public boolean isTrainedModelExists(User user, String name) {
        return trainedModelRepository.existsTrainedModelByUserAndName(user, name);
    }
}
