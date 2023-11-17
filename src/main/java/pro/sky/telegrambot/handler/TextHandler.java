package pro.sky.telegrambot.handler;

import com.pengrad.telegrambot.model.Message;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.service.NotificationTaskService;
import pro.sky.telegrambot.service.TelegramBotService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TextHandler {

    private static final Pattern PATTERN = Pattern.compile("([0-9.:\\s]{16})\\s([\\W\\w+]+)");
    private final NotificationTaskService notificationTaskService;
    private final TelegramBotService telegramBotService;

    public TextHandler(NotificationTaskService notificationTaskService, TelegramBotService telegramBotService) {
        this.notificationTaskService = notificationTaskService;
        this.telegramBotService = telegramBotService;
    }

    public void handleText(Message message) {
        Matcher matcher = PATTERN.matcher(message.text());

        if (matcher.matches()) {
            notificationTaskService.saveNotification(message, matcher);
        } else {
            telegramBotService.sendMessage(message.chat().id(), "Неверный формат сообщения");
        }
    }
}
