package es.cesar.app.controller;

import es.cesar.app.service.LocaleFormattingService;
import es.cesar.app.util.MessageHelper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

import static es.cesar.app.util.AlertType.DANGER;


@Controller
public class AboutController extends BaseController {
    private final LocaleFormattingService formattingService;

    public AboutController(LocaleFormattingService formattingService) {
        this.formattingService = formattingService;
        super.module = "nav_about";
    }

    @GetMapping("/about")
    public String showViewerPage(ModelMap interfazConPantalla) {
        final String folderPath = "static/pdfs/";
        final String[] pdfFiles = {"memoria", "anexos", "Workshop_César"};

        try {
            for (int i = 0; i < pdfFiles.length; i++) {
                String filePath = folderPath + pdfFiles[i] + ".pdf";
                ClassPathResource resource = new ClassPathResource(filePath);
                if (!resource.exists()) {
                    continue;
                }
                String base64 = encodePdfToBase64(filePath);
                interfazConPantalla.addAttribute("pdf" + (i + 1), base64);
            }
        } catch (Exception e) {
            MessageHelper.addMessage(interfazConPantalla, DANGER, formattingService.getMessage("error.pdf"));
            return "about";
        }
        setPage(interfazConPantalla);
        return "about";
    }

    private String encodePdfToBase64(String classpathLocation) throws IOException {
        ClassPathResource resource = new ClassPathResource(classpathLocation);

        try (InputStream inputStream = resource.getInputStream();
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

            byte[] data = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, bytesRead);
            }

            return Base64.getEncoder().encodeToString(buffer.toByteArray());
        }
    }

}
