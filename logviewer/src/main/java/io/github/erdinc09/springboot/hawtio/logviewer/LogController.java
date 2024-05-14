package io.github.erdinc09.springboot.hawtio.logviewer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@Qualifier("logController")
final class LogController implements ILogSink {
  private final InternalLogger internalLogger = new InternalLogger();

  private final SimpMessagingTemplate template;

  @Autowired
  public LogController(SimpMessagingTemplate template) {
    this.template = template;
  }

  @Override
  public void log(String message) {
    internalLogger.debug(() -> String.format("'%s' is sent", message));
    template.convertAndSend("/topic/new_logs", new LogMessage(message));
  }
}
