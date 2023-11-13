package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.model.Message;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;

@Service
public class NotificationTaskService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private final TelegramBotService telegramBotService;
    private final NotificationTaskRepository notificationTaskRepository;

    public NotificationTaskService(TelegramBotService telegramBotService, NotificationTaskRepository notificationTaskRepository) {
        this.telegramBotService = telegramBotService;
        this.notificationTaskRepository = notificationTaskRepository;
    }

    public void start(Message message) {
        telegramBotService.sendMessage(message.chat().id(), "Введите сообщение в формате: 01.01.2022 20:00 Сделать домашнюю работу");
    }

    public void saveNotification(Message message, Matcher matcher) {
        LocalDateTime localDateTime= LocalDateTime.parse(matcher.group(1), DATE_TIME_FORMATTER);
        String notification = matcher.group(2);

        NotificationTask notificationTask = new NotificationTask();
        notificationTask.setChatId(message.chat().id());
        notificationTask.setText(notification);
        notificationTask.setDateTime(localDateTime);
        notificationTaskRepository.save(notificationTask);

        telegramBotService.sendMessage(message.chat().id(), "Напоминание сохранено");
    }
}
