package org.disboard.minecraft

import io.ipinfo.api.IPinfo
import io.ipinfo.api.cache.SimpleCache
import io.ipinfo.api.errors.ErrorResponseException
import io.ipinfo.api.errors.RateLimitedException
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent
import org.spigotmc.event.player.PlayerSpawnLocationEvent
import java.time.Duration


internal class EventListener(private val instance: SIPPlugin) : Listener {
    private val config: FileConfiguration
    private val ipInfo: IPinfo

    init {
        instance.server.pluginManager.registerEvents(this, instance)
        config = instance.config
        ipInfo = IPinfo.Builder().setToken(config.getString("token")).setCache(SimpleCache(Duration.ofDays(30))).build()
    }

    @EventHandler
    fun onPreJoin(event: PlayerLoginEvent) {
        instance.logger.info("${event.player.name} is logging in...")

        // If player has joined before, we are not going to set player's spawn location.
        if (event.player.hasPlayedBefore()) {
            instance.logger.info("${event.player.name} has joined before. Not setting spawn point.")
            return
        }

        val playerAddress = event.address.hostAddress

        // If player is connecting from localhost, we are not going to check IP address.
        if (playerAddress == "localhost" || playerAddress == "127.0.0.1") {
            instance.logger.info("${event.player.name} is joining from localhost. Not setting spawn point.")
            return
        }

        // Set spawn point
        instance.logger.info("${event.player.name} has joined for the first time. Setting spawn point.")

        try {
            val res = ipInfo.lookupIP(playerAddress)
            if (res.city == "null" && res.region == "null" && res.countryCode == "null") {
                instance.logger.info("${event.player.name} has no location information. Not setting spawn point.")
                return
            }

            instance.logger.info("${event.player.name} is joining from ${res.countryCode} ${res.region} ${res.city}")

            // Check city
            val city = SIPUtils.getCityLocation(res.countryCode, res.city)
            if (city != null) {
                event.player.setBedSpawnLocation(city, true)
                return
            }

            // Check region
            val region = SIPUtils.getRegionLocation(res.countryCode, res.region)
            if (region != null) {
                event.player.setBedSpawnLocation(region, true)
                return
            }

            // Check default location for the country
            val country = SIPUtils.getCountryLocation(res.countryCode)
            if (country != null) {
                event.player.setBedSpawnLocation(country, true)
                return
            }

            // Failed to find location from config
            instance.logger.warning("Failed to find location from config. Not setting spawn point for ${event.player.name}.")
            instance.logger.warning("Please check the config file. CountryCode: ${res.countryCode}, Region: ${res.region}, City: ${res.city}")

        } catch (e: RateLimitedException) {
            instance.logger.warning("Rate limited by IPinfo.io. Cannot set spawn point for ${event.player.name}.")
        } catch (e: ErrorResponseException) {
            instance.logger.warning("Error response from IPinfo.io. Cannot set spawn point for ${event.player.name}.")
        }
    }

    @EventHandler
    fun onJoin(event: PlayerSpawnLocationEvent) {
        if (!event.player.hasPlayedBefore()) event.spawnLocation = event.player.potentialBedLocation ?: event.spawnLocation
    }
}
