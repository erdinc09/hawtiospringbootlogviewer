package io.github.erdinc09.springboot.hawtio.logviewer;

import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Optional;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.appender.AbstractOutputStreamAppender;
import org.apache.logging.log4j.core.appender.ManagerFactory;
import org.apache.logging.log4j.core.appender.OutputStreamManager;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.*;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;

@Plugin(
    name = "SpringBootLogViewerLog4j2Appender",
    category = Core.CATEGORY_NAME,
    elementType = Appender.ELEMENT_TYPE)
public final class SpringBootLogViewerLog4j2Appender
    extends AbstractOutputStreamAppender<OutputStreamManager> implements ILogSinkConsumer {

  @GuardedBy("SpringBootLogViewerLog4j2Appender.class")
  @Nonnull
  private static Optional<ILogSinkSetter> logSinkSetter = Optional.empty();

  private static final StringBuilderAppenderOutputStream OUTPUT_STREAM_SINGLETON =
      new StringBuilderAppenderOutputStream();

  private static ConsoleManagerFactory factory = new ConsoleManagerFactory();

  private SpringBootLogViewerLog4j2Appender(
      String name,
      Layout<? extends Serializable> layout,
      Filter filter,
      Property[] properties,
      boolean ignoreExceptions,
      OutputStreamManager manager) {
    super(name, layout, filter, ignoreExceptions, true, properties, manager);

    logSinkSetter.ifPresent(setter->{
      getStatusLogger().log(Level.INFO,"newly created SpringBootLogViewerLog4j2Appender is set logSink");
      setter.setLogSink(this);
    });
  }

  @PluginBuilderFactory
  public static <B extends SpringBootLogViewerLog4j2Appender.Builder<B>> B newBuilder() {
    return new Builder<B>().asBuilder();
  }

  @Override
  public  void setLogSink(ILogSink logSink) {
    OUTPUT_STREAM_SINGLETON.setLogSink(logSink);
  }

  public static void setLogSinkStatic(ILogSink logSink) {
    OUTPUT_STREAM_SINGLETON.setLogSink(logSink);
  }

  public static void setLogSinkSetter(ILogSinkSetter logSinkSetter) {
    synchronized (SpringBootLogViewerLog4j2Appender.class){
      SpringBootLogViewerLog4j2Appender.logSinkSetter = Optional.of(logSinkSetter);
    }
  }

  public static class Builder<B extends SpringBootLogViewerLog4j2Appender.Builder<B>>
      extends AbstractOutputStreamAppender.Builder<B>
      implements org.apache.logging.log4j.core.util.Builder<SpringBootLogViewerLog4j2Appender> {
    @Override
    public SpringBootLogViewerLog4j2Appender build() {
      if (!isValid()) {
        return null;
      }
      final Layout<? extends Serializable> layout = getOrCreateLayout(Charset.defaultCharset());

      return new SpringBootLogViewerLog4j2Appender(
          getName(),
          layout,
          getFilter(),
          getPropertyArray(),
          isIgnoreExceptions(),
          getManager(layout));
    }
  }

  private static OutputStreamManager getManager(final Layout<? extends Serializable> layout) {

    final String managerName = "SpringBootLogViewerLog4j2Appender.OutputStreamManager";
    return OutputStreamManager.getManager(
        managerName, new FactoryData(OUTPUT_STREAM_SINGLETON, managerName, layout), factory);
  }

  private static class ConsoleManagerFactory
      implements ManagerFactory<
          OutputStreamManager, SpringBootLogViewerLog4j2Appender.FactoryData> {

    @Override
    public OutputStreamManager createManager(
        final String name, final SpringBootLogViewerLog4j2Appender.FactoryData data) {
      return new OutputStreamManagerJustToConstruct(data.os, data.name, data.layout, true);
    }
  }

  private static class FactoryData {
    private final OutputStream os;
    private final String name;
    private final Layout<? extends Serializable> layout;

    public FactoryData(
        final OutputStream os, final String type, final Layout<? extends Serializable> layout) {
      this.os = os;
      this.name = type;
      this.layout = layout;
    }
  }

  static class OutputStreamManagerJustToConstruct extends OutputStreamManager {

    protected OutputStreamManagerJustToConstruct(
        OutputStream os, String streamName, Layout<?> layout, boolean writeHeader) {
      super(os, streamName, layout, writeHeader);
    }
  }

  private static class StringBuilderAppenderOutputStream extends OutputStream {
    private volatile Optional<ILogSink> logSink =
        Optional.of(
            message -> {
              // no op
            });
    // no need for synchronization, already locked by OutputStreamManager
    private final StringBuilder sb = new StringBuilder();

    @Override
    public void write(int b) {
      // we depend on OutputStreamManager implementation
      throw new UnsupportedOperationException();
    }

    @Override
    public void write(byte b[]) {
      // we depend on OutputStreamManager implementation
      throw new UnsupportedOperationException();
    }

    @Override
    public void write(byte b[], int off, int len) {
      // we depend on OutputStreamManager implementation
      sb.append(new String(b, off, len)); // TODO: we are using default charset. Is it good choice?
    }

    @Override
    public void flush() {
      logSink.get().log(sb.toString().trim());
      sb.setLength(0);
    }

    public void setLogSink(ILogSink logSink) {
      this.logSink = Optional.of(logSink);
    }
  }
}
