package io.github.erdinc09.springboot.hawtio.logviewer;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.OutputStreamAppender;
import com.google.common.base.Preconditions;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;

public final class SpringBootLogViewerLogbackAppender extends OutputStreamAppender<ILoggingEvent> {

  @GuardedBy("SpringBootLogViewerLogbackAppender.class")
  private static final Set<SpringBootLogViewerLogbackAppender> STARTED_INSTANCES =
      Collections.synchronizedSet(new HashSet<>());

  @GuardedBy("SpringBootLogViewerLogbackAppender.class")
  @Nonnull
  private static Optional<IAppenderCallBackSetter> appenderCallBackSetter = Optional.empty();

  // no need for synchronization, already locked by OutputStreamAppender.streamWriteLock
  private final StringBuilder sb = new StringBuilder();
  private volatile Optional<IAppenderCallBack> callBack =
      Optional.of(
          message -> {
            // no op
          });

  public SpringBootLogViewerLogbackAppender() {
    // lazy created, ex appender exist but no ref to it
    // new instance created if ref unbound and then rebound in scans...
    System.out.println("SpringBootLogViewerLogbackAppender created");
  }

  static void setAppenderCallBackSetter(Optional<IAppenderCallBackSetter> appenderCallBackSetter) {
    Preconditions.checkNotNull(appenderCallBackSetter);
    Preconditions.checkArgument(appenderCallBackSetter.isPresent());
    SpringBootLogViewerLogbackAppender.appenderCallBackSetter = appenderCallBackSetter;
  }

  static void setCallBacks(Optional<IAppenderCallBack> callBack) {
    Preconditions.checkNotNull(callBack);
    Preconditions.checkArgument(callBack.isPresent());
    synchronized (SpringBootLogViewerLogbackAppender.class) {
      for (SpringBootLogViewerLogbackAppender appender : STARTED_INSTANCES) {
        appender.setCallBack(callBack);
      }
    }
  }

  void setCallBack(Optional<IAppenderCallBack> callBack) {
    Preconditions.checkNotNull(callBack);
    Preconditions.checkArgument(callBack.isPresent());
    this.callBack = callBack;
  }

  @Override
  public void start() {
    super.setOutputStream(new StringBuilderAppenderOutputStream());
    super.start();
    synchronized (SpringBootLogViewerLogbackAppender.class) {
      // appenderCallBackSetter  is set, it means reset is detected, newly
      // SpringBootLogViewerLogbackAppender may be created
      // otherwise it is fresh start, the only way is saving the created instances in order to set
      // the callbacks
      appenderCallBackSetter.ifPresentOrElse(
          appenderCallBackSetter -> appenderCallBackSetter.setAppenderCallback(this),
          () -> STARTED_INSTANCES.add(this));
    }
    System.out.println("SpringBootLogViewerLogbackAppender started");
  }

  @Override
  public void stop() {
    super.stop();
    synchronized (SpringBootLogViewerLogbackAppender.class) {
      STARTED_INSTANCES.remove(this);
      appenderCallBackSetter = Optional.empty();
    }
    System.out.println("SpringBootLogViewerLogbackAppender stopped");
  }

  @Override
  public void setOutputStream(OutputStream outputStream) {
    throw new UnsupportedOperationException(
        "SpringBootLogViewerAppender does not allow setting output stream");
  }

  @Override
  public void setImmediateFlush(boolean immediateFlush) {
    throw new UnsupportedOperationException(
        "SpringBootLogViewerAppender does not allow changing immediate flush");
  }

  private class StringBuilderAppenderOutputStream extends OutputStream {
    @Override
    public void write(int b) {
      // we depend on super implementation
      throw new UnsupportedOperationException();
    }

    @Override
    public void write(byte b[]) {
      sb.append(new String(b)); // TODO: we are using default charset. Is it good choice?
    }

    @Override
    public void write(byte b[], int off, int len) {
      // we depend on super implementation
      throw new UnsupportedOperationException();
    }

    @Override
    public void flush() {
      callBack.get().log(sb.toString().trim());
      sb.setLength(0);
    }
  }
}
