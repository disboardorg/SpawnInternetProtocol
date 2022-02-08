package org.disboard.minecraft

import org.bukkit.Location
import org.disboard.minecraft.SIPPlugin.Companion.instance

class SIPUtils {
    companion object {
        fun getCountryLocation(countryCode: String): Location? {
            val country = instance.config.getConfigurationSection("$countryCode.default")?.getValues(false)
            country.let {
                val x = it?.get("x").toString().toDoubleOrNull()
                val y = it?.get("y").toString().toDoubleOrNull()
                val z = it?.get("z").toString().toDoubleOrNull()

                if (x == null || y == null || z == null) {
                    instance.logger.warning("Error loading country location: $countryCode.default")
                    return null
                }

                return Location(instance.server.worlds[0], x, y, z)
            }
        }

        fun getRegionLocation(countryCode: String, regionName: String): Location? {
            val region = instance.config.getConfigurationSection("$countryCode.region.$regionName")?.getValues(false)
            region.let {
                val x = it?.get("x").toString().toDoubleOrNull()
                val y = it?.get("y").toString().toDoubleOrNull()
                val z = it?.get("z").toString().toDoubleOrNull()

                if (x == null || y == null || z == null) {
                    instance.logger.warning("Error loading region location: $countryCode.region.$regionName")
                    return null
                }

                return Location(instance.server.worlds[0], x, y, z)
            }
        }

        fun getCityLocation(countryCode: String, cityName: String): Location? {
            val region = instance.config.getConfigurationSection("$countryCode.city.$cityName")?.getValues(false)
            region.let {
                val x = it?.get("x").toString().toDoubleOrNull()
                val y = it?.get("y").toString().toDoubleOrNull()
                val z = it?.get("z").toString().toDoubleOrNull()

                if (x == null || y == null || z == null) {
                    instance.logger.warning("Error loading city location: $countryCode.city.$cityName")
                    return null
                }

                return Location(instance.server.worlds[0], x, y, z)
            }
        }
    }
}
