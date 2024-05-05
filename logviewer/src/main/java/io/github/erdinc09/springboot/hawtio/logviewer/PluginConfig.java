package io.github.erdinc09.springboot.hawtio.logviewer;

import io.hawt.springboot.HawtioPlugin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class PluginConfig {
  @Bean
  public HawtioPlugin provideSpringBootLogViewerPlugin(ILogAppenderManager logAppenderManager) {
    logAppenderManager.initialize();
    return new HawtioPlugin("spring_boot_log_viewer", "./plugin");
  }
}
