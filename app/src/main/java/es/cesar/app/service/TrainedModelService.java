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

/**
 * The class TrainedModelService, that provides services related to trained models.
 */
@Service
public class TrainedModelService {
    private final TrainedModelRepository trainedModelRepository;
    private final RestTemplate restTemplate;

    /**
     * Instantiates a new Trained model service.
     *
     * @param trainedModelRepository the trained model repository
     */
    @Autowired
    public TrainedModelService(TrainedModelRepository trainedModelRepository) {
        this.trainedModelRepository = trainedModelRepository;
        this.restTemplate = new RestTemplate();
    }

    /**
     * Request trained model to flask.
     *
     * @param user      the user
     * @param modelName the model name
     */
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

    /**
     * Create model.
     *
     * @param user the user
     * @param name the name
     * @param file the file
     */
    @Transactional
    public void createModel(User user, String name, byte[] file) {
        TrainedModel model = new TrainedModel();
        model.initializeWithName(user, name);
        model.setFile(file);
        trainedModelRepository.save(model);
    }

    /**
     * Gets trained models by user.
     *
     * @param user the user
     *
     * @return the trained models by user
     */
    public Collection<TrainedModel> getTrainedModelsByUser(User user) {
        return trainedModelRepository.findByUser(user);
    }

    /**
     * Delete trained model by id.
     *
     * @param idTrainedModel the id trained model
     */
    public void deleteTrainedModelById(Long idTrainedModel) {
        trainedModelRepository.deleteById(idTrainedModel);
    }

    /**
     * Gets trained model by id.
     *
     * @param idTrainedModel the id trained model
     *
     * @return the trained model by id
     */
    public TrainedModel getTrainedModelById(Long idTrainedModel) {
        return trainedModelRepository.findById(idTrainedModel).orElse(null);
    }

    /**
     * Update trained model trained model.
     *
     * @param trainedModel the trained model
     *
     * @return the trained model
     */
    public TrainedModel updateTrainedModel(TrainedModel trainedModel) {
        return trainedModelRepository.save(trainedModel);
    }

    /**
     * Is trained model exists boolean.
     *
     * @param user the user
     * @param name the name
     *
     * @return the boolean
     */
    public boolean isTrainedModelExists(User user, String name) {
        return trainedModelRepository.existsTrainedModelByUserAndName(user, name);
    }
}
