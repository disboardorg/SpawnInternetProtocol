package org.disboard.minecraft

import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandHandler(private val instance: SIPPlugin): CommandExecutor {
    init {
        instance.getCommand("sip")?.setExecutor(this)
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) return false

        when (args[0]) {
            "reload" -> {
                if(!sender.hasPermission("sip.reload")) {
                    sender.sendMessage("${ChatColor.RED}You don't have permission to use this command.")
                    return true
                }
                if (args.size != 1) return false
                instance.reloadConfig().run { sender.sendMessage("Config reloaded.") }
            }
            "tp" -> {
                if(!sender.hasPermission("sip.tp")) {
                    sender.sendMessage("${ChatColor.RED}You don't have permission to use this command.")
                    return true
                }
                if (!(args.size == 3 || args.size == 5)) return false

                val players = when (args[1]) {
                    "@a" -> {
                        instance.server.onlinePlayers.toList()
                    }
                    "@p" -> {
                        if (sender !is Player) {
                            sender.sendMessage("${ChatColor.RED}You must be a player to use this command.")
                            return true
                        }
                        // Don't know the default range for @p, so I'm just going to assume it's 5
                        listOf(sender.getNearbyEntities(5.0,5.0,5.0).filterIsInstance<Player>()[0])
                    }
                    "@s" -> {
                        if (sender !is Player) {
                            sender.sendMessage("${ChatColor.RED}You must be a player to use this command.")
                            return true
                        }
                        listOf(sender)
                    }
                    "@e" -> if (sender is Player) sender.world.entities else sender.server.worlds[0].entities
                    else -> instance.server.onlinePlayers.filter { it.name == args[1] }
                }

                if (players.isEmpty()) {
                    sender.sendMessage("${ChatColor.RED}No player/entity found.")
                    return true
                }

                val location = if (args.size == 3) {
                    SIPUtils.getCountryLocation(args[2])
                } else if (args[3] == "city") {
                    SIPUtils.getCityLocation(args[2], args[4])
                } else if (args[3] == "region") {
                    SIPUtils.getRegionLocation(args[2], args[4])
                } else {
                    return false
                }

                if (location == null) {
                    sender.sendMessage("${ChatColor.RED}No location found.")
                    return true
                }

                players.forEach {
                    it.teleport(location)
                    it.sendMessage("${ChatColor.GREEN}Welcome to ${if (args.size == 3) args[2] else "${args[4]}, ${args[2]}"}.")
                }
            }
            else -> return false
        }
        return true
    }
}
