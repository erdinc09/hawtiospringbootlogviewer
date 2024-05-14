package io.github.erdinc09.springboot.hawtio.logviewer;

import java.util.function.Supplier;

final class InternalLogger {

  static final boolean LOG_ENABLED = Boolean.getBoolean("io.github.erdinc09.springboot.hawtio.internal_log.enabled");

  void debug(Supplier<String> messageSupplier) {
    if (LOG_ENABLED) {
      final StackTraceElement callerStackTraceElement = Thread.currentThread().getStackTrace()[2];
      final String callersFileName = callerStackTraceElement.getFileName();
      final int callersLineNumber = callerStackTraceElement.getLineNumber();
      System.out.printf(
          "%s:%d = [%s]%n", callersFileName, callersLineNumber, messageSupplier.get());
    }
  }
}
