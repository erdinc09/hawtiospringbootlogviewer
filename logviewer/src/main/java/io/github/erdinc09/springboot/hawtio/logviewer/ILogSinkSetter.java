package io.github.erdinc09.springboot.hawtio.logviewer;

import javax.annotation.Nonnull;

interface ILogSinkSetter {
  void setLogSink(@Nonnull ILogSinkConsumer logSinkConsumer);
}
