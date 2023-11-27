package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.handler.TextHandler;
import pro.sky.telegrambot.service.TelegramBotService;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final TelegramBot telegramBot;
    private final TextHandler textHandler;
    private final TelegramBotService telegramBotService;

    public TelegramBotUpdatesListener(TelegramBot telegramBot, TextHandler textHandler, TelegramBotService telegramBotService) {
        this.telegramBot = telegramBot;
        this.textHandler = textHandler;
        this.telegramBotService = telegramBotService;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            if (update.message() != null && update.message().text() != null) {
                textHandler.handleText(update.message());
            } else {
                telegramBotService.sendMessage(update.message().chat().id(), "Неккорректное сообщение");
            }
        });

        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
