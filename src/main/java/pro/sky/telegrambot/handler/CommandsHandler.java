package pro.sky.telegrambot.handler;

import com.pengrad.telegrambot.model.Message;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.service.NotificationTaskService;
import pro.sky.telegrambot.service.TelegramBotService;

@Component
public class CommandsHandler {

    private final TelegramBotService telegramBotService;
    private final NotificationTaskService notificationTaskService;

    public CommandsHandler(TelegramBotService telegramBotService, NotificationTaskService notificationTaskService) {
        this.telegramBotService = telegramBotService;
        this.notificationTaskService = notificationTaskService;
    }

    public void handleCommands(Message message) {
        Long chatId = message.chat().id();
        String text = message.text();

        switch (text) {
            case "/start":
                telegramBotService.start(chatId);
                break;
            case "/info":
                telegramBotService.info(chatId);
                break;
            case "/help":
                telegramBotService.help(chatId);
                break;
            case "/show_notifications":
                notificationTaskService.showNotifications(chatId, 0);
                break;
            default:
                telegramBotService.sendMessage(chatId, "Такой команды не существует");
        }
    }

    private void handleBasicCommands() {

    }

    private void handleOtherCommands() {

    }
}
