package pro.sky.telegrambot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.entity.User;
import pro.sky.telegrambot.enums.CommandsEnum;
import pro.sky.telegrambot.enums.StatesEnum;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;

import static pro.sky.telegrambot.enums.CommandsEnum.*;

@Service
public class NotificationTaskService {

    private static final int PAGE_SIZE = 5;
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

    public void setDelivered(NotificationTask notificationTask, boolean delivered) {
        notificationTask.setDelivered(delivered);
        notificationTaskRepository.save(notificationTask);
    }

    public void saveNotification(Long userId, Matcher matcher) {
        LocalDateTime localDateTime = LocalDateTime.parse(matcher.group(1), DATE_TIME_FORMATTER);

        if (localDateTime.isAfter(LocalDateTime.now())) {
            String notification = matcher.group(2);
            User user = userService.getUser(userId);

            NotificationTask notificationTask = new NotificationTask();
            notificationTask.setUser(user);
            notificationTask.setText(notification);
            notificationTask.setDateTime(localDateTime);
            notificationTaskRepository.save(notificationTask);
            userService.setState(userId, StatesEnum.FREE_SWIMMING.name());

            telegramBotService.sendMessage(userId, "Напоминание сохранено");
        } else {
            telegramBotService.sendMessage(userId, "Не могу поставить напоминание в прошлое");
        }
    }

    public void showNotifications(Long userId, int page) {
        int actualAmountNotDeliveredNotifications = notificationTaskRepository.countByDeliveredAndUserId(false, userId);

        PageRequest pageRequest = PageRequest.of(page, PAGE_SIZE);
        List<NotificationTask> notifications = notificationTaskRepository.findByUserIdAndDeliveredOrderByDateTime(userId, false, pageRequest);
        StringBuilder text = new StringBuilder();

        if (notifications.size() > 0) {
            for (int i = 0; i < notifications.size(); i++) {
                text.append(String.format("%d. %s: %s\n\n", i + 1, notifications.get(i).getDateTime().format(DATE_TIME_FORMATTER), notifications.get(i).getText()));
            }
        } else {
            text.append("У вас нет уведомлений\n\n");
        }

        text.append(String.format("%s - следующая страница\n" +
                "%s - предыдущая страница\n" +
                "%s - закончить просмотр\n" +
                "%s - редактировать уведомление\n" +
                "%s - удалить уведомление", NEXT, PREV, EXIT, EDIT, DELETE));

        telegramBotService.sendMessage(userId, text.toString());
        userService.setTaskCount(userId, actualAmountNotDeliveredNotifications);
        userService.setCurrentPage(userId, page);
        userService.setState(userId, StatesEnum.NAVIGATION.name());
    }

    public void nextPage(Long userId) {
        int currentPage = userService.getCurrentPage(userId);
        int actualAmountNotDeliveredNotifications = notificationTaskRepository.countByDeliveredAndUserId(false, userId);
        int maxPage = (actualAmountNotDeliveredNotifications + PAGE_SIZE - 1) / PAGE_SIZE;

        if (currentPage < maxPage - 1) {
            showNotifications(userId, currentPage + 1);
        } else {
            showNotifications(userId, currentPage);
        }
    }

    public void prevPage(Long userId) {
        int currentPage = userService.getCurrentPage(userId);

        if (currentPage > 0) {
            showNotifications(userId, currentPage - 1);
        } else {
            showNotifications(userId, currentPage);
        }
    }

    public void sendNotification(NotificationTask notificationTask) {
        telegramBotService.sendMessage(notificationTask.getUser().getId(), notificationTask.getText());
    }

    public void endShowNotifications(Long userId) {
        userService.setState(userId, StatesEnum.FREE_SWIMMING.name());
        userService.setCurrentPage(userId, 0);
        telegramBotService.help(userId);
    }

    public void editNotification(Long userId, Matcher matcher) {
        int actualAmountNotDeliveredNotifications = notificationTaskRepository.countByDeliveredAndUserId(false, userId);
        int taskCount = userService.getTaskCount(userId);

        if (actualAmountNotDeliveredNotifications == taskCount) {
            int currentPage = userService.getCurrentPage(userId);
            PageRequest pageRequest = PageRequest.of(currentPage, PAGE_SIZE);
            List<NotificationTask> notifications = notificationTaskRepository.findByUserIdAndDeliveredOrderByDateTime(userId, false, pageRequest);

            if (matcher.group(1) != null) {
                NotificationTask notificationTask = notifications.get(Integer.parseInt(matcher.group(1)) - 1);

                if (matcher.group(2) != null) {
                    LocalDateTime localDateTime = LocalDateTime.parse(matcher.group(2), DATE_TIME_FORMATTER);
                    notificationTask.setDateTime(localDateTime);
                }
                if (matcher.group(3) != null) {
                    notificationTask.setText(matcher.group(3).trim());
                }
                notificationTaskRepository.save(notificationTask);
                showNotifications(userId, currentPage);
            }
        } else {
            telegramBotService.sendMessage(userId, "Ваш список уведомлений изменился, " +
                    "необходимо выполнить заново команду " + CommandsEnum.SHOW_NOTIFICATIONS);
        }
    }

    public void deleteNotification(Long userId, int numberNotification) {
        int actualAmountNotDeliveredNotifications = notificationTaskRepository.countByDeliveredAndUserId(false, userId);
        int taskCount = userService.getTaskCount(userId);

        if (actualAmountNotDeliveredNotifications == taskCount) {
            int currentPage = userService.getCurrentPage(userId);
            PageRequest pageRequest = PageRequest.of(currentPage, PAGE_SIZE);
            List<NotificationTask> notifications = notificationTaskRepository.findByUserIdAndDeliveredOrderByDateTime(userId, false, pageRequest);

            NotificationTask notificationTask = notifications.get(numberNotification - 1);
            notificationTaskRepository.delete(notificationTask);
            showNotifications(userId, currentPage);
        } else {
            telegramBotService.sendMessage(userId, "Ваш список уведомлений изменился, " +
                    "необходимо выполнить заново команду " + CommandsEnum.SHOW_NOTIFICATIONS);
        }
    }
}
