package es.cesar.app.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;


/**
 * The class ChatController, that provides chatbot functionality.
 */
@RestController
@CrossOrigin
public class ChatController {

    private final ChatClient chatClient;

    /**
     * Instantiates a new Chat controller.
     *
     * @param builder the builder
     */
    @Autowired
    public ChatController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    /**
     * Provides the Chat with stream flux.
     *
     * @param message the message provided by the user
     *
     * @return the flux of chat responses
     */
    @GetMapping("/stream")
    public Flux<String> chatWithStream(@RequestParam("message") String message) {
        String context = """
                This website was developed by César Rodríguez as part of his Bachelor's Thesis (TFG) in computer science for University of Burgos. Its main purpose is to present the official project documentation and to visualize the performance of a reinforcement learning model based on the PPO (Proximal Policy Optimization) algorithm, designed to mitigate DoS and DDoS attacks in network environments.
                
                In the "About the Project" section, users can access the full thesis document and its annexes. The website also provides interactive visualizations of:
                
                - The reward function behavior based on different parameters
                - Training statistics of the model
                - Performance metrics during model operation or simulated testing
                
                The website is primarily intended for the thesis evaluation committee, but it may also be of interest to students, researchers, or professionals in cybersecurity and machine learning.
                
                The virtual assistant should be able to answer questions in both English and Spanish, using a formal and technical tone appropriate for an academic context.
                
                If the assistant does not have enough information to accurately answer a question, it should politely direct the user to the "About the Project" section for more detailed documentation.
                
                Este sitio web ha sido desarrollado por César Rodríguez como parte de su Trabajo de Fin de Grado (TFG). Su objetivo principal es presentar la documentación oficial del proyecto y visualizar el rendimiento de un modelo de aprendizaje por refuerzo (Reinforcement Learning) basado en el algoritmo PPO (Proximal Policy Optimization), diseñado para mitigar ataques DoS y DDoS en entornos de red.
                
                En la sección "Sobre el Proyecto", los usuarios pueden acceder a la memoria completa del TFG y a los anexos. Además, la web ofrece visualizaciones interactivas de:
                
                - El comportamiento de la función de recompensa según distintos parámetros
                - Estadísticas del proceso de entrenamiento del modelo
                - Métricas de rendimiento durante la operación del modelo o en pruebas simuladas
                
                El sitio está dirigido principalmente al tribunal evaluador del TFG, aunque también puede resultar útil para estudiantes, investigadores o profesionales del ámbito de la ciberseguridad y el aprendizaje automático.
                
                El asistente virtual debe responder en el idioma del usuario (español o inglés), utilizando un tono técnico y formal adecuado para un contexto académico.
                
                Si el asistente no dispone de suficiente información para responder con precisión, debe redirigir amablemente al usuario a la sección "Sobre el Proyecto" para consultar la documentación detallada.
                
                """;

        return chatClient.prompt()
                .system("You are a helpful assistant. Use the following context to answer the user's question, in the user's language.")
                .user("Context:\n" + context + "\n\nQuestion:\n" + message)
                .stream()
                .content();
    }

}
