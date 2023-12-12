package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.enums.StatesEnum;

import static pro.sky.telegrambot.enums.CommandsEnum.*;

@Service
public class TelegramBotService {

    private final Logger LOG = LoggerFactory.getLogger(TelegramBotService.class);
    private final TelegramBot telegramBot;
    private final UserService userService;

    public TelegramBotService(TelegramBot telegramBot, UserService userService) {
        this.telegramBot = telegramBot;
        this.userService = userService;
    }

    public void sendMessage(long chatId, String message, ParseMode parseMode) {
        SendMessage sendMessage = new SendMessage(chatId, message);
        if (parseMode != null) {
            sendMessage.parseMode(parseMode);
        }
        SendResponse sendResponse = telegramBot.execute(sendMessage);
        if (!sendResponse.isOk()) {
            LOG.error("SendMessage was failed:" + sendResponse.description());
        }
    }

    public void sendMessage(long chatId, String message) {
        sendMessage(chatId, message, null);
    }

    public void start(Long userId) {
        if (userService.getState(userId).equals(StatesEnum.NEW_USER.name())) {
            info(userId);
        } else {
            sendMessage(userId, "Введите сообщение в формате: 01.01.2022 20:00 Сделать домашнюю работу");
        }
        userService.setState(userId, StatesEnum.FREE_SWIMMING.name());
    }

    public void info(long chatId) {
        sendMessage(chatId,
                "Это учебный бот для работы с напоминаниями.\n" +
                        "Чтобы посмотреть список всех доступных команд, введите команду" + HELP + "\n\n" +
                        "Для создания напоминания, введите сообщение в формате:\n01.01.2022 20:00 Сделать домашнюю работу");
    }

    public void help(Long chatId) {
        sendMessage(chatId, String.format("Список доступных команд:\n" +
                        "1. %s - начало работы\n" +
                        "2. %s - увидеть информацию о боте\n" +
                        "3. %s - посмотреть список всех доступных команд\n" +
                        "4. %s - посмотреть свои напоминания\n", START, INFO, HELP, SHOW_NOTIFICATIONS)
                );
    }
}
