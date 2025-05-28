package im.infmngmt;

import im.infmngmt.ui.MainAppFrame;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

@SpringBootApplication
public class MedicalApp {
    public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        ApplicationContext context = new SpringApplicationBuilder(MedicalApp.class)
                .headless(false)
                .run(args);

        UIManager.setLookAndFeel(new NimbusLookAndFeel());
        context.getBean(MainAppFrame.class).setVisible(true);
    }
}
