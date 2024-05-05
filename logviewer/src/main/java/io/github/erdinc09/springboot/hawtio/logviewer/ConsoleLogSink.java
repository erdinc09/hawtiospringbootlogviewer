package io.github.erdinc09.springboot.hawtio.logviewer;

import org.springframework.stereotype.Component;

// for test purposes, will be deleted
// TODO:should be used websocket controller, within dedicated thread
@Component
class ConsoleLogSink implements ILogSink {
  @Override
  public void log(String message) {
    System.out.println(message);
  }
}
