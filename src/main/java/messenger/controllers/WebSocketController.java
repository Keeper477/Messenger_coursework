package messenger.controllers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import messenger.components.WSMessage;

@Controller
public class WebSocketController {
    @MessageMapping("/send")
    @SendTo("/topic/messages")
    public WSMessage greeting(WSMessage message) throws InterruptedException {
        Thread.sleep(200);
        return message;
    }
}
