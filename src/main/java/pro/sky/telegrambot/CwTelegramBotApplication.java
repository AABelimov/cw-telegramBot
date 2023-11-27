package pro.sky.telegrambot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CwTelegramBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(CwTelegramBotApplication.class, args);
	}

}
