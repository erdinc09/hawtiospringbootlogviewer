package io.github.erdinc09.springboot.hawtio.logviewer;

import io.hawt.springboot.HawtioPlugin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PluginConfig {
    @Bean
    public HawtioPlugin samplePlugin() {
        /*
         * These are the parameters required to load a remote Hawtio plugin (a.k.a. Module Federation remote module):
         *
         * - scope: The name of the container defined at Webpack ModuleFederationPlugin. See also: sample-plugin/craco.config.js
         * - module: The path exposed from Webpack ModuleFederationPlugin. See also: sample-plugin/craco.config.js
         * - url: The URL of the remote entry for the plugin, e.g. "http://localhost:8081". (optional)
         */
        HawtioPlugin plugin = new HawtioPlugin(
                "samplePlugin",
                "./plugin");

        /*
         * By default, Hawtio expects "plugin" as the name of the Hawtio plugin entry function.
         * If you want to use the name other than the default one, specify the name using HawtioPlugin#setPluginEntry()
         * as follows. See also: sample-plugin/src/sample-plugin/index.ts
         */
        //plugin.setPluginEntry("registerMyPlugin");

        return plugin;
    }
}
