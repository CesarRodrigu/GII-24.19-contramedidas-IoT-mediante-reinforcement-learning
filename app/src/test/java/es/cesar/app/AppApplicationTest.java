package es.cesar.app;

import es.cesar.app.controller.BaseController;
import es.cesar.app.controller.MainController;
import es.cesar.app.controller.SigninController;
import es.cesar.app.controller.SignupController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("coverage")
class AppApplicationTest {
    @Autowired
    private MainController mainController;

    @Autowired
    private SigninController signinController;

    @Autowired
    private SignupController signupController;

    @Test
    void smokeTest() {
        // @formatter:off
        final List<BaseController> controllers = List.of(
                mainController,
                signinController,
                signupController
        );
        // @formatter:on
        for (BaseController controller : controllers) {
            assertNotNull(controller, "Controller " + controller.getClass().getSimpleName() + " should be loaded");
        }
    }
}