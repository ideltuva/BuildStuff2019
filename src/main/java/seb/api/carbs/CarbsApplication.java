package seb.api.carbs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CarbsApplication {

	@Autowired
	KafkaReceiver kafkaReceiver;

	public static void main(String[] args) {
		SpringApplication.run(CarbsApplication.class, args);
	}

}
