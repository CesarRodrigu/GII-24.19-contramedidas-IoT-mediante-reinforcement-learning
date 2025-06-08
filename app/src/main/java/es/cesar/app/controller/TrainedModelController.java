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
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Objects;

import static es.cesar.app.util.AlertType.*;

/**
 * The class TrainedModelController, that handles the trained model management.
 */
@Controller
public class TrainedModelController extends BaseController {
    /**
     * The constant MANAGE_MODELS_VIEW_NAME, that represents the view name for managing trained models.
     */
    public static final String MANAGE_MODELS_VIEW_NAME = "trained_models/table";
    /**
     * The constant MANAGE_MODELS_VIEW_URL, that represents the view URL for managing trained models.
     */
    public static final String MANAGE_MODELS_VIEW_URL = "manageTrainedModels";
    private static final String REDIRECT = "redirect:/";
    private final LocaleFormattingService formattingService;
    private final TrainedModelService trainedModelService;
    private final UserService userService;
    private final TrainedModelMapper trainedModelMapper;

    /**
     * Instantiates a new Trained model controller.
     *
     * @param trainedModelService the trained model service
     * @param userService         the user service
     * @param trainedModelMapper  the trained model mapper
     * @param formattingService   the formatting service
     */
    @Autowired
    public TrainedModelController(TrainedModelService trainedModelService, UserService userService, TrainedModelMapper trainedModelMapper, LocaleFormattingService formattingService) {
        this.trainedModelService = trainedModelService;
        this.userService = userService;
        this.trainedModelMapper = trainedModelMapper;
        this.formattingService = formattingService;
        super.module = "manage_models";
    }

    /**
     * Handle the request to manage trained models.
     *
     * @param modelMap the model map
     *
     * @return the path to the manage models view
     */
    @GetMapping("/manageTrainedModels")
    public String manageTrainedModels(ModelMap modelMap) {
        String username = SecurityUtils.getCurrentUsername();
        User user = userService.getUserByUsername(username);
        Collection<TrainedModel> trainedModelList = trainedModelService.getTrainedModelsByUser(user);
        Collection<TrainedModelDto> trainedModelListDto = trainedModelMapper.toDtos(trainedModelList);
        modelMap.addAttribute("trainedModelList", trainedModelListDto);
        modelMap.addAttribute("nameDto", new NameDto());
        setPage(modelMap);
        return MANAGE_MODELS_VIEW_NAME;
    }


    /**
     * Handle the request to create a new trained model.
     *
     * @param nameDto            the name dto
     * @param errors             the errors
     * @param redirectAttributes the redirect attributes
     *
     * @return the path to the manage models view or redirect URL
     */
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
        } catch (Exception e) {
            MessageHelper.addFlashMessage(redirectAttributes, DANGER, formattingService.getMessage("model.request.error"));
        }
        return REDIRECT + MANAGE_MODELS_VIEW_URL;

    }

    /**
     * Handle the request to delete a trained model.
     *
     * @param modelId            the model id
     * @param redirectAttributes the redirect attributes
     *
     * @return the path to the manage models view
     */
    @RequestMapping(value = "/deleteTrainedModel", method = {RequestMethod.POST, RequestMethod.DELETE})
    public String deleteTrainedModel(@RequestParam Long modelId, RedirectAttributes redirectAttributes) {
        trainedModelService.deleteTrainedModelById(modelId);
        MessageHelper.addFlashMessage(redirectAttributes, SUCCESS, formattingService.getMessage("model.delete.success"));
        return REDIRECT + MANAGE_MODELS_VIEW_URL;
    }

    /**
     * Handle the request to edit a trained model.
     *
     * @param modelId            the model id
     * @param modelMap           the model map
     * @param redirectAttributes the redirect attributes
     *
     * @return the path to the edit model view or redirect URL
     */
    @GetMapping("/editModel")
    public String editUser(@RequestParam Long modelId, ModelMap modelMap, RedirectAttributes redirectAttributes) {
        TrainedModel trainedModel = trainedModelService.getTrainedModelById(modelId);
        if (trainedModel == null) {
            MessageHelper.addFlashMessage(redirectAttributes, DANGER, formattingService.getMessage("model.notfound"));
            return REDIRECT + MANAGE_MODELS_VIEW_URL;
        }
        TrainedModelDto dto = trainedModelMapper.toDto(trainedModel);

        modelMap.addAttribute("trainedModelDto", dto);
        setPage(modelMap);

        return "trained_models/edit";
    }

    /**
     * Handle the request to update a trained model.
     *
     * @param trainedModelDto    the trained model dto
     * @param redirectAttributes the redirect attributes
     *
     * @return the path to the manage models view or redirect URL
     */
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

    /**
     * Handle the request to download a trained model as a zip file.
     *
     * @param id       the id of the trained model
     * @param response the HTTP response
     *
     * @throws IOException the io exception, that may occur during file download
     */
    @GetMapping("/models/{id}/download")
    public void downloadZipFile(@PathVariable Long id, HttpServletResponse response) throws IOException {
        TrainedModel trainedModel = trainedModelService.getTrainedModelById(id);
        if (trainedModel == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        byte[] zipBytes = trainedModel.getFile();
        String fileName = trainedModel.getFileName() != null ? trainedModel.getFileName() : "model.zip";

        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        response.setContentLength(zipBytes.length);

        try (OutputStream out = response.getOutputStream()) {
            out.write(zipBytes);
            out.flush();
        }
    }

    /**
     * Handle zip upload.
     *
     * @param file               the file
     * @param user               the user
     * @param redirectAttributes the redirect attributes
     *
     * @return the path to the manage models view or redirect URL
     */
    @PostMapping("/uploadZipModel")
    public String handleZipUpload(@RequestParam("file") MultipartFile file,
                                  @AuthenticationPrincipal UserDetails user,
                                  RedirectAttributes redirectAttributes) {
        if (file.isEmpty() || !Objects.requireNonNull(file.getOriginalFilename()).endsWith(".zip")) {
            MessageHelper.addFlashMessage(redirectAttributes, DANGER, formattingService.getMessage("model.upload.invalid"));
            return REDIRECT + MANAGE_MODELS_VIEW_URL;
        }
        try {
            byte[] fileBytes = file.getBytes();

            String originalFilename = file.getOriginalFilename();
            String modelName = originalFilename != null && originalFilename.endsWith(".zip")
                    ? originalFilename.substring(0, originalFilename.length() - 4)
                    : "unnamed-model";
            User user2 = userService.getUserByUsername(user.getUsername());
            trainedModelService.createModel(user2, modelName, fileBytes);
            MessageHelper.addFlashMessage(redirectAttributes, SUCCESS, formattingService.getMessage("model.upload.success"));
        } catch (IOException e) {
            MessageHelper.addFlashMessage(redirectAttributes, DANGER, formattingService.getMessage("model.upload.invalid"));
        }

        return REDIRECT + MANAGE_MODELS_VIEW_URL;
    }

}
