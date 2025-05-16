package es.cesar.app.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ui.ModelMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParamControllerTest {

    @Test
    void testParams() {
        ParamController controller = new ParamController();

        ModelMap model = Mockito.mock(ModelMap.class);

        String viewName = controller.params(model);

        assertEquals("params/Params", viewName, "The returned view mismatch");

        Mockito.verify(model, Mockito.times(1)).addAttribute(Mockito.anyString(), Mockito.any());
    }
}