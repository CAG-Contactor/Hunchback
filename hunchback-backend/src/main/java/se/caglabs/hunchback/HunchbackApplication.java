package se.caglabs.hunchback;

import io.hawt.springboot.EnableHawtio;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Project:hunchback
 * User: fredrik
 * Date: 2017-05-23
 * Time: 19:07
 */
@SpringBootApplication
@EnableHawtio
public class HunchbackApplication {
    public static void main(String[] args) {
//        ApplicationContext context =
//        new ClassPathXmlApplicationContext("application-context.xml");
        SpringApplication.run(HunchbackApplication.class, args);
    }
}
