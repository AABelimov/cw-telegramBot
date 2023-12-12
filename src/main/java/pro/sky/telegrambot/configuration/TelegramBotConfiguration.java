package pro.sky.telegrambot.configuration;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.DeleteMyCommands;
import com.pengrad.telegrambot.request.SetMyCommands;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static pro.sky.telegrambot.enums.CommandsEnum.*;

@Configuration
public class TelegramBotConfiguration {

    @Value("${telegram.bot.token}")
    private String token;

    @Bean
    public TelegramBot telegramBot() {
        TelegramBot bot = new TelegramBot(token);
        bot.execute(new DeleteMyCommands());
        BotCommand start = new BotCommand(START.toString(), "Запуск бота, образец записи напоминания");
        BotCommand info = new BotCommand(INFO.toString(), "Информация о боте");
        BotCommand help = new BotCommand(HELP.toString(), "Список команд");
        BotCommand showNotifications = new BotCommand(SHOW_NOTIFICATIONS.toString(), "Посмотреть на свои напоминания");
        bot.execute(new SetMyCommands(start, info, help, showNotifications));
        return bot;
    }
}
