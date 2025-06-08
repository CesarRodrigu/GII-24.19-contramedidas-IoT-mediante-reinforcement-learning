package es.cesar.app.repository;

import es.cesar.app.model.TrainedModel;
import es.cesar.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * The interface TrainedModelRepository, that extends JpaRepository for TrainedModel entity.
 */
public interface TrainedModelRepository extends JpaRepository<TrainedModel, Long> {
    /**
     * Find by user list.
     *
     * @param user the user
     *
     * @return the list
     */
    List<TrainedModel> findByUser(User user);

    /**
     * Delete all by user.
     *
     * @param user the user
     */
    void deleteAllByUser(User user);

    /**
     * Exists trained model by user and name boolean.
     *
     * @param user the user
     * @param name the name
     *
     * @return the boolean
     */
    boolean existsTrainedModelByUserAndName(User user, String name);
}