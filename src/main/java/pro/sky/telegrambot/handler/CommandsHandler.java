package pro.sky.telegrambot.handler;

import com.pengrad.telegrambot.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.enums.CommandsEnum;
import pro.sky.telegrambot.enums.StatesEnum;
import pro.sky.telegrambot.service.NotificationTaskService;
import pro.sky.telegrambot.service.TelegramBotService;
import pro.sky.telegrambot.service.UserService;

import static pro.sky.telegrambot.enums.CommandsEnum.EXIT;
import static pro.sky.telegrambot.enums.CommandsEnum.START;

@Component
public class CommandsHandler {

    private static final Logger LOG = LoggerFactory.getLogger(CommandsHandler.class);
    private final TelegramBotService telegramBotService;
    private final NotificationTaskService notificationTaskService;
    private final UserService userService;

    public CommandsHandler(TelegramBotService telegramBotService, NotificationTaskService notificationTaskService, UserService userService) {
        this.telegramBotService = telegramBotService;
        this.notificationTaskService = notificationTaskService;
        this.userService = userService;
    }

    public void handleCommands(Message message) {
        Long chatId = message.chat().id();
        CommandsEnum command = START;
        try {
            command = CommandsEnum.valueOf(message.text().substring(1).toUpperCase());
        } catch (IllegalArgumentException e) {
            LOG.error(e.getMessage());
        }
        StatesEnum state = StatesEnum.valueOf(userService.getState(chatId));

        switch (state) {
            case NEW_USER:
            case FREE_SWIMMING:
                handleBasicCommands(chatId, command);
                break;
            case NAVIGATION:
            case EDIT:
            case DELETE:
                handleNavigationCommands(chatId, command);
                break;
        }
    }

    private void handleBasicCommands(Long chatId, CommandsEnum command) {
        switch (command) {
            case START:
                telegramBotService.start(chatId);
                break;
            case INFO:
                telegramBotService.info(chatId);
                break;
            case HELP:
                telegramBotService.help(chatId);
                break;
            case SHOW_NOTIFICATIONS:
                notificationTaskService.showNotifications(chatId, 0);
                break;
            default:
                telegramBotService.sendMessage(chatId, "Такой команды не существует");
        }
    }

    private void handleNavigationCommands(Long chatId, CommandsEnum command) {
        switch (command) {
            case NEXT:
                notificationTaskService.nextPage(chatId);
                break;
            case PREV:
                notificationTaskService.prevPage(chatId);
                break;
            case EXIT:
                notificationTaskService.endShowNotifications(chatId);
                break;
            case EDIT:
                userService.setState(chatId, StatesEnum.EDIT.name());
                telegramBotService.sendMessage(chatId, "Внесите изменение одним из форматов:\n" +
                        "№. новая дата текст нового уведомления\n" +
                        "№. новая дата\n" +
                        "№. текст нового уведомления");
                break;
            case DELETE:
                userService.setState(chatId, StatesEnum.DELETE.name());
                telegramBotService.sendMessage(chatId, "Какое уведомление необходимо удалить? Введите его номер");
                break;
            default:
                telegramBotService.sendMessage(chatId, "Введите команду " + EXIT +", чтобы выйти из режима просмотра уведомлений");
        }
    }
}
