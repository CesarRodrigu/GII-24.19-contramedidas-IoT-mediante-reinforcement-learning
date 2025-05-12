package es.cesar.backend.repository;

import es.cesar.backend.model.TrainedModel;
import es.cesar.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainedModelRepository extends JpaRepository<TrainedModel, Long> {
    List<TrainedModel> findByUser(User user);

    void deleteAllByUser(User user);

    boolean existsTrainedModelByUserAndName(User user, String name);
}