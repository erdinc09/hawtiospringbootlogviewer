package io.github.erdinc09.springboot.hawtio.logviewertester.package1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public final class Component1 {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Scheduled(fixedRate = 3_000)
  private void log() {
    logger.debug("test debug log from slf4j/logback");
  }
}
