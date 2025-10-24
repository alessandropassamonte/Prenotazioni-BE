package it.company.deskbooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class DeskBookingApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeskBookingApplication.class, args);
    }
}
