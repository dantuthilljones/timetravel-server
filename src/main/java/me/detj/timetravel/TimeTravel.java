package me.detj.timetravel;

import com.google.common.collect.ImmutableList;
import me.detj.timetravel.coders.crypto.Crypter;
import me.detj.timetravel.coders.string.StringCoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class TimeTravel extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(TimeTravel.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            System.out.println("Spring Boot Beans:");

            Arrays.stream(ctx.getBeanDefinitionNames())
                    .sorted()
                    .map(name -> "-" + name)
                    .forEachOrdered(System.out::println);
        };
    }

    @Bean
    @Autowired
    public SecretKeySpec secretKeySpec(@Value("${crypto.password}") String password) {
        byte[] key = Arrays.copyOf(password.getBytes(), 16);
        return new SecretKeySpec(key, Crypter.KEY_ALGORITHM);
    }

    @Bean
    @Autowired
    public StringCoder stringCoder(@Value("${strings.path}") String path) throws IOException {
        String fileContents = new String(Files.readAllBytes(Paths.get(path)));
        String[] words = fileContents.split("\\r?\\n");
        List<String> dictionary = Arrays.stream(words).limit(StringCoder.DICTIONARY_SIZE).collect(ImmutableList.toImmutableList());
        return new StringCoder(dictionary);
    }
}
