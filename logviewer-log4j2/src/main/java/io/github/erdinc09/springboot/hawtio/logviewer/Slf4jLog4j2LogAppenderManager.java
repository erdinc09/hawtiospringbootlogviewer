package io.github.erdinc09.springboot.hawtio.logviewer;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
final class Slf4jLog4j2LogAppenderManager
    implements ILogAppenderManager, ILogSinkSetter {

  @Nonnull
  private final ILogSink logSink;

  @GuardedBy("this")
  private boolean initialized = false;

  @Autowired
  public Slf4jLog4j2LogAppenderManager(@Qualifier("logController") ILogSink logSink) {
    this.logSink = Preconditions.checkNotNull(logSink);
  }

  @Override
  public synchronized void initialize() {
    Preconditions.checkState(!initialized, "already initialized");
    SpringBootLogViewerLog4j2Appender.setLogSinkStatic(logSink);
    SpringBootLogViewerLog4j2Appender.setLogSinkSetter(this);
    initialized = true;
  }

  @Override
  public void setLogSink(@Nonnull ILogSinkConsumer logSinkConsumer) {
    logSinkConsumer.setLogSink(logSink);
  }
}
