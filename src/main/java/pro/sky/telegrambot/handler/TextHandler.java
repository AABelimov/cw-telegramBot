package pro.sky.telegrambot.handler;

import com.pengrad.telegrambot.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.enums.StatesEnum;
import pro.sky.telegrambot.service.NotificationTaskService;
import pro.sky.telegrambot.service.TelegramBotService;
import pro.sky.telegrambot.service.UserService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TextHandler {

    private static final Logger LOG = LoggerFactory.getLogger(TextHandler.class);
    private static final Pattern NOTIFICATION_PATTERN = Pattern.compile("([0-9.:\\s]{16})\\s([\\W\\w+]+)");
    private static final Pattern EDIT_NOTIFICATION_PATTERN = Pattern.compile("([1-5])\\. ([0-9.:\\s]{16})?([\\W\\w+]+)?");
    private final NotificationTaskService notificationTaskService;
    private final TelegramBotService telegramBotService;
    private final UserService userService;

    public TextHandler(NotificationTaskService notificationTaskService, TelegramBotService telegramBotService, UserService userService) {
        this.notificationTaskService = notificationTaskService;
        this.telegramBotService = telegramBotService;
        this.userService = userService;
    }

    public void handleText(Message message) {
        Long chatId = message.chat().id();
        String text = message.text();
        StatesEnum state = StatesEnum.valueOf(userService.getState(chatId));

        switch (state) {
            case NEW_USER:
            case FREE_SWIMMING:
                handleNotification(chatId, text);
            case EDIT:
                handleEditNotification(chatId, text);
                break;
            case DELETE:
                handleDeleteNotification(chatId, text);
                break;
            case NAVIGATION:
                notificationTaskService.showNotifications(chatId, userService.getCurrentPage(chatId));
        }
    }

    private void handleNotification(Long chatId, String text) {
        Matcher matcher = NOTIFICATION_PATTERN.matcher(text);

        if (matcher.matches()) {
            notificationTaskService.saveNotification(chatId, matcher);
        } else {
            telegramBotService.sendMessage(chatId, "Неверный формат сообщения");
        }
    }

    private void handleEditNotification(Long chatId, String text) {
        Matcher matcher = EDIT_NOTIFICATION_PATTERN.matcher(text);
        if (matcher.matches()) {
            notificationTaskService.editNotification(chatId, matcher);
        } else {
            telegramBotService.sendMessage(chatId, "Неверный формат сообщения");
        }
    }

    private void handleDeleteNotification(Long chatId, String text) {
        String s = text.split("[^1-5]")[0];
        if (s != null) {
            int numberNotification = Integer.parseInt(s);
            notificationTaskService.deleteNotification(chatId, numberNotification);
        }
    }
}
