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
    implements ILogAppenderManager, ILogSinkSetter {

  @Nonnull
  private final ILogSink logSink;

  @GuardedBy("this")
  private boolean initialized = false;

  @Autowired
  public Slf4jLogbackLogAppenderManager(ILogSink logSink) {
    this.logSink = Preconditions.checkNotNull(logSink);
  }

  @Override
  public synchronized void initialize() {
    Preconditions.checkState(!initialized, "already initialized");

    initialized = true;
  }

  @Override
  public void setLogSink(ILogSinkConsumer logSinkConsumer) {
    logSinkConsumer.setLogSink(logSink);
  }
}
