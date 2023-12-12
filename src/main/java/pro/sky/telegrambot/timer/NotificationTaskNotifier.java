package pro.sky.telegrambot.timer;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pro.sky.telegrambot.repository.NotificationTaskRepository;
import pro.sky.telegrambot.service.NotificationTaskService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Component
public class NotificationTaskNotifier {

    private final NotificationTaskRepository notificationTaskRepository;
    private final NotificationTaskService notificationTaskService;

    public NotificationTaskNotifier(NotificationTaskRepository notificationTaskRepository,
                                    NotificationTaskService notificationTaskService) {
        this.notificationTaskRepository = notificationTaskRepository;
        this.notificationTaskService = notificationTaskService;
    }

    @Scheduled(timeUnit = TimeUnit.MINUTES, fixedDelay = 1)
    @Transactional
    public void sendNotification() {
        notificationTaskRepository.findByDateTime(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                .forEach(e -> {
                    notificationTaskService.sendNotification(e);
                    notificationTaskService.setDelivered(e, true);
                });
    }
}
