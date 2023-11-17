package pro.sky.telegrambot.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    private Long id;

    @Column(name = "state", nullable = false)
    private String state;

    @Column(name = "task_count", nullable = false)
    private int taskCount;

    @Column(name = "current_page")
    private int currentPage;

    public User() {
    }

    public User(Long id, String state, int taskCount, int currentPage) {
        this.id = id;
        this.state = state;
        this.taskCount = taskCount;
        this.currentPage = currentPage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(int taskCount) {
        this.taskCount = taskCount;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
}
