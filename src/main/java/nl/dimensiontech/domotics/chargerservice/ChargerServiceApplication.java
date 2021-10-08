package nl.dimensiontech.domotics.chargerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ChargerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChargerServiceApplication.class, args);
	}

}
