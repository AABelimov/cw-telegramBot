package pro.sky.telegrambot.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.telegrambot.entity.NotificationTask;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationTaskRepository extends JpaRepository<NotificationTask, Long> {

    List<NotificationTask> findByDateTime(LocalDateTime localDateTime);

    List<NotificationTask> findByUserIdAndDeliveredOrderByDateTime(Long userId, boolean delivered, PageRequest pageRequest);

    int countByDeliveredAndUserId(boolean delivered, Long userId);
}
