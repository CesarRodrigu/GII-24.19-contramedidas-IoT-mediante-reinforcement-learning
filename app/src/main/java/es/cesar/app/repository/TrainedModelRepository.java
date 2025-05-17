package es.cesar.app.repository;

import es.cesar.app.model.TrainedModel;
import es.cesar.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainedModelRepository extends JpaRepository<TrainedModel, Long> {
    List<TrainedModel> findByUser(User user);

    void deleteAllByUser(User user);

    boolean existsTrainedModelByUserAndName(User user, String name);
}