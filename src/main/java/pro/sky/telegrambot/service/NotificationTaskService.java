package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.entity.User;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

@Service
public class NotificationTaskService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationTaskService.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private final TelegramBotService telegramBotService;
    private final NotificationTaskRepository notificationTaskRepository;
    private final UserService userService;

    public NotificationTaskService(TelegramBotService telegramBotService, NotificationTaskRepository notificationTaskRepository, UserService userService) {
        this.telegramBotService = telegramBotService;
        this.notificationTaskRepository = notificationTaskRepository;
        this.userService = userService;
    }

    public void saveNotification(Message message, Matcher matcher) {
        LocalDateTime localDateTime= LocalDateTime.parse(matcher.group(1), DATE_TIME_FORMATTER);

        if (localDateTime.isAfter(LocalDateTime.now())) {
            String notification = matcher.group(2);
            Long userId = message.chat().id();
            User user = userService.getUser(userId);

            NotificationTask notificationTask = new NotificationTask();
            notificationTask.setUser(user);
            notificationTask.setText(notification);
            notificationTask.setDateTime(localDateTime);
            notificationTaskRepository.save(notificationTask);
            userService.setState(userId, "FREE_SWIMMING");
            //userService.incrementTaskCount(userId);

            telegramBotService.sendMessage(message.chat().id(), "Напоминание сохранено");
        } else {
            telegramBotService.sendMessage(message.chat().id(), "Не могу поставить напоминание в прошлое");
        }
    }

    public void showNotifications(Long userId, int page) {
        PageRequest pageRequest = PageRequest.of(page, 5);
        List<String> notifications = notificationTaskRepository.findByUserId(userId, pageRequest).stream()
                .map(NotificationTask::getText)
                .collect(Collectors.toList());
        StringBuilder text = new StringBuilder();

        for (int i = 0; i < notifications.size(); i++) {
            text.append(String.format("%d. %s\n", i + 1, notifications.get(i)));
        }

        telegramBotService.sendMessage(userId, text.toString());
        userService.setTaskCount(userId, notifications.size());
        userService.setCurrentPage(userId, page);
        userService.setState(userId, "");// TODO: придумать состояние
    }
}
