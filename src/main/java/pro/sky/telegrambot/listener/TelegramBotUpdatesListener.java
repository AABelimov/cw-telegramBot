package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.handler.CommandsHandler;
import pro.sky.telegrambot.handler.TextHandler;
import pro.sky.telegrambot.service.TelegramBotService;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final TelegramBot telegramBot;
    private final TextHandler textHandler;
    private final CommandsHandler commandsHandler;
    private final TelegramBotService telegramBotService;

    public TelegramBotUpdatesListener(TelegramBot telegramBot,
                                      TextHandler textHandler,
                                      CommandsHandler commandsHandler,
                                      TelegramBotService telegramBotService) {
        this.telegramBot = telegramBot;
        this.textHandler = textHandler;
        this.commandsHandler = commandsHandler;
        this.telegramBotService = telegramBotService;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            Message message = update.message();
            String text = message.text();

            if (text != null) {
                if (text.charAt(0) == '/') {
                    commandsHandler.handleCommands(message);
                } else {
                    textHandler.handleText(message);
                }
            } else {
                telegramBotService.sendMessage(message.chat().id(), "Неккорректные данные");
            }
        });

        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
