package io.github.erdinc09.springboot.hawtio.logviewer;

import javax.annotation.Nonnull;

interface ILogSinkConsumer {
  void setLogSink(@Nonnull ILogSink logSink);
}
