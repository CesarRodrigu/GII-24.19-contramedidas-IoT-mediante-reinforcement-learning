package es.cesar.app.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ui.ModelMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MainControllerTest {

    @Test
    void testHome() {
        MainController controller = new MainController();

        ModelMap model = Mockito.mock(ModelMap.class);

        String viewName = controller.home(model);

        assertEquals("index", viewName, "La vista devuelta debería ser 'index'");

        Mockito.verify(model, Mockito.times(1)).addAttribute(Mockito.anyString(), Mockito.any());
    }
}
