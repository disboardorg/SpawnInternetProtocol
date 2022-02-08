package org.disboard.minecraft

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class CommandCompletionHandler(private val instance: SIPPlugin): TabCompleter {
    init {
        instance.getCommand("sip")?.tabCompleter = this
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        return when (args.size) {
            1 -> listOf("reload", "tp").filter { it.startsWith(args[0]) }.toMutableList()
            2 -> {
                if (args[0] == "reload") return mutableListOf()
                val completions = mutableListOf("@a", "@e")
                if (sender is Player) completions.addAll(listOf("@p", "@s"))
                completions.addAll(instance.server.onlinePlayers.map { it.name })
                completions.filter { it.startsWith(args[1]) }.toMutableList()
            }
            3 -> instance.config.getKeys(false).filterNot { it == "token" }.filter { it.startsWith(args[2]) }.toMutableList()
            4 -> mutableListOf("city", "region").filter { it.startsWith(args[3]) }.toMutableList()
            5 -> instance.config.getConfigurationSection("JP.city")?.getValues(false)?.keys?.toMutableList()?.filter { it.startsWith(args[4]) }?.toMutableList() ?: mutableListOf()
            else -> mutableListOf()
        }
    }
}
