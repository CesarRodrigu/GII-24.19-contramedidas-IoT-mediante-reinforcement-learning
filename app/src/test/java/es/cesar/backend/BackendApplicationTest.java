package es.cesar.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("coverage")
class BackendApplicationTest {
    @Test
    void contextLoads() {
        // TODO mirar si es test de humo
        assert true;
    }
}