package me.detj.timetravel;

import com.google.common.primitives.Bytes;
import me.detj.timetravel.coders.crypto.Crypter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

@SpringBootApplication
public class TimeTravel {

    public static void main(String[] args) {
        SpringApplication.run(TimeTravel.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            System.out.println("Spring Boot Beans:");

            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            for (String beanName : beanNames) {
                System.out.println(beanName);
            }
        };
    }

    @Bean
    @Autowired
    public SecretKeySpec secretKeySpec(@Value("${crypto.password}") String password) throws IOException {
        byte[] key = Arrays.copyOf(password.getBytes(), 16);
        return new SecretKeySpec(key, Crypter.KEY_ALGORITHM);
    }
}
