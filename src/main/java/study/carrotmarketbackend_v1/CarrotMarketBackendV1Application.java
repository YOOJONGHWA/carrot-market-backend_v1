package study.carrotmarketbackend_v1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class CarrotMarketBackendV1Application {

    public static void main(String[] args) {
        SpringApplication.run(CarrotMarketBackendV1Application.class, args);
    }

}
