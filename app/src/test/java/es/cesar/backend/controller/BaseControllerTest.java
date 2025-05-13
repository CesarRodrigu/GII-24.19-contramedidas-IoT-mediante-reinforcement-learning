package es.cesar.backend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class BaseControllerTest {

    @Test
    void testSetPageWithModelMap() {
        TestBaseController testBaseController = new TestBaseController();
        ModelMap modelMap = mock(ModelMap.class);
        testBaseController.setPage(modelMap);
        verify(modelMap).addAttribute("module", "home");
    }

    @Test
    void testSetPageWithModel() {
        TestBaseController testBaseController = new TestBaseController();
        Model model = mock(Model.class);
        testBaseController.setPage(model);
        verify(model).addAttribute("module", "home");
    }

    private static class TestBaseController extends BaseController {
    }
}
