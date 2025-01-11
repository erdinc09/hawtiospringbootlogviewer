import { HawtioPlugin, hawtio, helpRegistry, preferencesRegistry } from '@hawtio/react'
import { LogViewer } from './LogViewer'
import { LogViewerPreferences } from './LogViewerPreferences'
import { log, pluginName, pluginPath, pluginTitle } from './globals'
import help from './help.md'

export const sprinBootLogViewer: HawtioPlugin = () => {
  log.info('Loading', pluginName)

  hawtio.addPlugin({
    id: pluginName,
    title: pluginTitle,
    path: pluginPath,
    component: LogViewer,
    isActive: async () => true,
  })

  helpRegistry.add(pluginName, pluginTitle, help, 101)
  preferencesRegistry.add(pluginName, pluginTitle, LogViewerPreferences, 101)
}
