package pro.sky.telegrambot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entity.User;
import pro.sky.telegrambot.repository.UserRepository;

@Service
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User getUser(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            user = createUser(new User(userId, "NEW_USER", 0, 0));
        }
        return user;
    }

    public String getState(Long userId) {
        User user = getUser(userId);
        return user.getState();
    }

    public void setState(Long userId, String state) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setState(state);
        userRepository.save(user);
    }

    public void incrementTaskCount(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setTaskCount(user.getTaskCount() + 1);
        userRepository.save(user);
    }

    public void decrementTaskCount(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setTaskCount(user.getTaskCount() - 1);
        userRepository.save(user);
    }

    public void setTaskCount(Long userId, int count) {
        User user = userRepository.findById(userId).orElseThrow();

        user.setTaskCount(count);
        userRepository.save(user);
    }

    public void setCurrentPage(Long userId, int page) {
        User user = userRepository.findById(userId).orElseThrow();

        user.setCurrentPage(page);
        userRepository.save(user);
    }
}
