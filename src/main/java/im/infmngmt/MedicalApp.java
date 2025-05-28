package im.infmngmt;

import im.infmngmt.ui.MainAppFrame;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;

import javax.swing.*;

@SpringBootApplication
public class MedicalApp {
    public static void main(String[] args) {
        ApplicationContext context = new SpringApplicationBuilder(MedicalApp.class)
                .headless(false)
                .run(args);

        context.getBean(MainAppFrame.class).setVisible(true);
    }
}
