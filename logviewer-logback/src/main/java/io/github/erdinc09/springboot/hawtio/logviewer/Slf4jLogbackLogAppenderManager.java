package io.github.erdinc09.springboot.hawtio.logviewer;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggerContextListener;
import com.google.common.base.Preconditions;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
final class Slf4jLogbackLogAppenderManager
    implements ILogAppenderManager, LoggerContextListener, ILogSinkSetter {

  @Nonnull private final ILogSink logSink;

  @GuardedBy("this")
  private boolean initialized = false;

  @Autowired
  public Slf4jLogbackLogAppenderManager(ILogSink logSink) {
    this.logSink = Preconditions.checkNotNull(logSink);
  }

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

  @Override
  public synchronized void initialize() {
    Preconditions.checkState(!initialized, "already initialized");
    final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    loggerContext.addListener(this);
    SpringBootLogViewerLogbackAppender.setCallBacks(logSink);
    initialized = true;
  }

  @Override
  public void setLogSink(ILogSinkConsumer logSinkConsumer) {
    logSinkConsumer.setLogSink(logSink);
  }
}
