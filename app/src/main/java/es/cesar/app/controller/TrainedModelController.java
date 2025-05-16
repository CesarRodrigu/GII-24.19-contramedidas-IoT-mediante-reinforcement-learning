package es.cesar.app.controller;

import es.cesar.app.dto.NameDto;
import es.cesar.app.dto.TrainedModelDto;
import es.cesar.app.mappers.TrainedModelMapper;
import es.cesar.app.model.TrainedModel;
import es.cesar.app.model.User;
import es.cesar.app.service.LocaleFormattingService;
import es.cesar.app.service.TrainedModelService;
import es.cesar.app.service.UserService;
import es.cesar.app.util.MessageHelper;
import es.cesar.app.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Collection;

import static es.cesar.app.util.AlertType.*;

@Controller
public class TrainedModelController extends BaseController {
    public static final String MANAGE_MODELS_VIEW_NAME = "trained_models/table";
    public static final String MANAGE_MODELS_VIEW_URL = "manageTrainedModels";
    private static final String REDIRECT = "redirect:/";
    private final LocaleFormattingService formattingService;
    private final TrainedModelService trainedModelService;
    private final UserService userService;
    private final TrainedModelMapper trainedModelMapper;

    @Autowired
    public TrainedModelController(TrainedModelService trainedModelService, UserService userService, TrainedModelMapper trainedModelMapper, LocaleFormattingService formattingService) {
        this.trainedModelService = trainedModelService;
        this.userService = userService;
        this.trainedModelMapper = trainedModelMapper;
        this.formattingService = formattingService;
        super.module = "manage_models";
    }

    @GetMapping("/manageTrainedModels")
    public String manageTrainedModels(ModelMap interfazConPantalla) {
        String username = SecurityUtils.getCurrentUsername();
        User user = userService.getUserByUsername(username);
        Collection<TrainedModel> trainedModelList = trainedModelService.getTrainedModelsByUser(user);
        Collection<TrainedModelDto> trainedModelListDto = trainedModelMapper.toDtos(trainedModelList);
        interfazConPantalla.addAttribute("trainedModelList", trainedModelListDto);
        interfazConPantalla.addAttribute("nameDto", new NameDto());
        setPage(interfazConPantalla);
        return MANAGE_MODELS_VIEW_NAME;
    }


    @PostMapping("/requestModel")
    public String requestNewModel(@ModelAttribute @Valid NameDto nameDto, Errors errors, RedirectAttributes redirectAttributes) {
        if (errors.hasErrors()) {
            return MANAGE_MODELS_VIEW_NAME;
        }
        String username = SecurityUtils.getCurrentUsername();
        User user = userService.getUserByUsername(username);
        if (trainedModelService.isTrainedModelExists(user, nameDto.getName())) {
            errors.rejectValue("name", "error.name", formattingService.getMessage("model.exists"));
            return MANAGE_MODELS_VIEW_NAME;
        }
        try {
            if (user != null) {
                trainedModelService.requestTrainedModelToFlask(user, nameDto.getName());
                MessageHelper.addFlashMessage(redirectAttributes, INFO, formattingService.getMessage("manageusers.request.success"));
                return REDIRECT + MANAGE_MODELS_VIEW_URL;
            }
        } catch (IOException e) {
            MessageHelper.addFlashMessage(redirectAttributes, DANGER, formattingService.getMessage("model.request.error"));
        }
        return REDIRECT + MANAGE_MODELS_VIEW_URL;

    }

    @RequestMapping(value = "/deleteTrainedModel", method = {RequestMethod.POST, RequestMethod.DELETE})
    public String deleteTrainedModel(@RequestParam Long modelId, RedirectAttributes redirectAttributes) {
        trainedModelService.deleteTrainedModelById(modelId);
        MessageHelper.addFlashMessage(redirectAttributes, SUCCESS, formattingService.getMessage("model.delete.success"));
        return REDIRECT + MANAGE_MODELS_VIEW_URL;
    }

    @GetMapping("/editModel")
    public String editUser(@RequestParam Long modelId, ModelMap interfazConPantalla, RedirectAttributes redirectAttributes) {
        TrainedModel trainedModel = trainedModelService.getTrainedModelById(modelId);
        if (trainedModel == null) {
            MessageHelper.addFlashMessage(redirectAttributes, DANGER, formattingService.getMessage("model.notfound"));
            return REDIRECT + MANAGE_MODELS_VIEW_URL;
        }
        TrainedModelDto dto = trainedModelMapper.toDto(trainedModel);

        interfazConPantalla.addAttribute("trainedModelDto", dto);
        setPage(interfazConPantalla);

        return "trained_models/edit";
    }

    @RequestMapping(value = "/updateModel", method = {RequestMethod.POST, RequestMethod.PUT})
    public String updateModel(@ModelAttribute TrainedModelDto trainedModelDto, RedirectAttributes redirectAttributes) {
        TrainedModel trainedModel = trainedModelService.getTrainedModelById(trainedModelDto.getModelId());
        if (!trainedModelMapper.isDifferent(trainedModelDto, trainedModel)) {
            MessageHelper.addFlashMessage(redirectAttributes, WARNING, formattingService.getMessage("model.update.nochange"));
            return REDIRECT + MANAGE_MODELS_VIEW_URL;
        }
        trainedModelMapper.updateEntity(trainedModelDto, trainedModel);
        trainedModelService.updateTrainedModel(trainedModel);
        MessageHelper.addFlashMessage(redirectAttributes, SUCCESS, formattingService.getMessage("model.update.success"));
        return REDIRECT + MANAGE_MODELS_VIEW_URL;
    }
}
