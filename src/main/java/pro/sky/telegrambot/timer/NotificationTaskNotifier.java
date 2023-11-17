package pro.sky.telegrambot.timer;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pro.sky.telegrambot.repository.NotificationTaskRepository;
import pro.sky.telegrambot.service.TelegramBotService;
import pro.sky.telegrambot.service.UserService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Component
public class NotificationTaskNotifier {

    private final NotificationTaskRepository notificationTaskRepository;
    private final TelegramBotService telegramBotService;
    private final UserService userService;

    public NotificationTaskNotifier(NotificationTaskRepository notificationTaskRepository, TelegramBotService telegramBotService, UserService userService) {
        this.notificationTaskRepository = notificationTaskRepository;
        this.telegramBotService = telegramBotService;
        this.userService = userService;
    }

    @Scheduled(timeUnit = TimeUnit.MINUTES, fixedDelay = 1)
    @Transactional
    public void task() {
        notificationTaskRepository.findByDateTime(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                .forEach(e -> {
                    telegramBotService.sendMessage(e.getUser().getId(), e.getText());
                    //userService.decrementTaskCount(e.getUser().getId());
                });
    }
}
