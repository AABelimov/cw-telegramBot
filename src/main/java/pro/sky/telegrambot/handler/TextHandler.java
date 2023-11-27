package pro.sky.telegrambot.handler;

import com.pengrad.telegrambot.model.Message;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.service.NotificationTaskService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TextHandler {

    private static final Pattern PATTERN = Pattern.compile("([0-9.:\\s]{16})\\s([\\W\\w+]+)");
    private final NotificationTaskService notificationTaskService;

    public TextHandler(NotificationTaskService notificationTaskService) {
        this.notificationTaskService = notificationTaskService;
    }

    public void handleText(Message message) {
        Matcher matcher = PATTERN.matcher(message.text());

        if (message.text().charAt(0) == '/') {
            handleCommands(message);
        }

        if (matcher.matches()) {
            notificationTaskService.saveNotification(message, matcher);
        }
    }

    private void handleCommands(Message message) {
        switch (message.text()) {
            case "/start":
                notificationTaskService.start(message);
        }
    }
}
