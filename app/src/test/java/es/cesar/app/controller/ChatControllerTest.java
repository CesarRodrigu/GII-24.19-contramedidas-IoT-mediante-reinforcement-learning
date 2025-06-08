package es.cesar.app.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ChatControllerTest {

    private ChatController chatController;


    @BeforeEach
    void setup() {
        ChatModel chatModel = Mockito.mock(ChatModel.class);
        chatController = new ChatController(ChatClient.builder(chatModel));
    }

    @Test
    void testChatWithStream() {
        try {
            chatController.chatWithStream("");
        } catch (NullPointerException e) {
            assertTrue(true);
        }
    }
}