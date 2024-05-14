package io.github.erdinc09.springboot.hawtio.logviewertester;

import java.util.logging.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(
    scanBasePackages = {
      "io.github.erdinc09.springboot.hawtio.logviewertester",
      "io.github.erdinc09.springboot.hawtio.logviewer"
    })
public class LogViewerTesterApplication {

  private static final Logger LOGGER = Logger.getLogger(LogViewerTesterApplication.class.getName());

  public static void main(String[] args) {
    SpringApplication.run(LogViewerTesterApplication.class, args);
  }
}
