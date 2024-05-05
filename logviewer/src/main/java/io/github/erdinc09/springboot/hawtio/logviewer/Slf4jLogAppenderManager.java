package io.github.erdinc09.springboot.hawtio.logviewer;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggerContextListener;
import com.google.common.base.Preconditions;
import java.util.Optional;
import javax.annotation.concurrent.GuardedBy;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
final class Slf4jLogAppenderManager
    implements LoggerContextListener, IAppenderCallBack, IAppenderCallBackSetter {
  @GuardedBy("this")
  private boolean initialized = false;

  @Override
  public boolean isResetResistant() {
    return true;
  }

  @Override
  public void onStart(LoggerContext context) {
    System.out.println("Slf4jLogAppenderManager start");
  }

  @Override
  public void onReset(LoggerContext context) {
    // this is the point where we should set the interface into appender
    System.out.println("Slf4jLogAppenderManager reset");
    SpringBootLogViewerLogbackAppender.setAppenderCallBackSetter(Optional.of(this));
  }

  @Override
  public void onStop(LoggerContext context) {
    System.out.println("Slf4jLogAppenderManager stop");
  }

  @Override
  public void onLevelChange(Logger logger, Level level) {}

  synchronized void initialize() {
    Preconditions.checkState(!initialized, "already initialized");
    final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    loggerContext.addListener(this);
    SpringBootLogViewerLogbackAppender.setCallBacks(Optional.of(this));
    initialized = true;
  }

  @Override
  public void log(String message) {
    // TODO:should go to websocket controller, within dedicated thread
    System.out.println(message);
  }

  @Override
  public void setAppenderCallback(
      SpringBootLogViewerLogbackAppender springBootLogViewerLogbackAppender) {
    springBootLogViewerLogbackAppender.setCallBack(Optional.of(this));
  }
}
