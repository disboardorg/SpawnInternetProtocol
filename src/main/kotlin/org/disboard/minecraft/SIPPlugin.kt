package org.disboard.minecraft

import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class SIPPlugin : JavaPlugin() {
    companion object {
        lateinit var instance: SIPPlugin
    }

    override fun onEnable() {
        // Generate default config file if not exists
        if (!File(this.dataFolder,"config.yml").exists()) {
            this.saveDefaultConfig()
            this.logger.info("Config file generated. Please edit config.yml and restart the server.")
            this.pluginLoader.disablePlugin(this)
            return
        }

        this.config.getString("token")?.let {
            if (it.isEmpty() || it == "YOUR_TOKEN_HERE") {
                this.logger.warning("Please set your token in config.yml")
                this.logger.warning("Disabling plugin...")
                this.pluginLoader.disablePlugin(this)
                return
            }
        }

        // Register events and commands
        instance = this
        EventListener(this)
        CommandHandler(this)
        CommandCompletionHandler(this)
    }
}
