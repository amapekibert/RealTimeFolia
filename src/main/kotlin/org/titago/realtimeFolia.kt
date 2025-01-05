package org.titago

import org.bukkit.plugin.java.JavaPlugin
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.function.Consumer
import io.papermc.paper.threadedregions.scheduler.ScheduledTask

class realtimeFolia : JavaPlugin() {

    override fun onEnable() {

        saveDefaultConfig()


        val gmt = config.getString("timezone", "GMT-1")
        val zoneId = try {
            ZoneId.of(gmt)
        } catch (e: Exception) {
            logger.warning("Incorrect time zone format, using default GMT-1")
            ZoneId.of("GMT-1")
        }

        server.globalRegionScheduler.runAtFixedRate(
            this,
            Consumer<ScheduledTask> {
                val currentTime = ZonedDateTime.now(zoneId)
                val hours = currentTime.hour
                val minutes = currentTime.minute
                val minecraftTime = when (hours) {
                    12 -> 6000 + (minutes * 1000) / 60
                    13 -> 7000 + (minutes * 1000) / 60
                    14 -> 8000 + (minutes * 1000) / 60
                    15 -> 9000 + (minutes * 1000) / 60
                    16 -> 10000 + (minutes * 1000) / 60
                    17 -> 11000 + (minutes * 1000) / 60
                    18 -> 12000 + (minutes * 1000) / 60
                    19 -> 13000 + (minutes * 1000) / 60
                    20 -> 14000 + (minutes * 1000) / 60
                    21 -> 15000 + (minutes * 1000) / 60
                    22 -> 16000 + (minutes * 1000) / 60
                    23 -> 17000 + (minutes * 1000) / 60
                    0 -> 18000 + (minutes * 1000) / 60
                    1 -> 19000 + (minutes * 1000) / 60
                    2 -> 20000 + (minutes * 1000) / 60
                    3 -> 21000 + (minutes * 1000) / 60
                    4 -> 22000 + (minutes * 1000) / 60
                    5 -> 23000 + (minutes * 1000) / 60
                    6 -> 24000 + (minutes * 1000) / 60
                    7 -> (1000 + (minutes * 1000) / 60)
                    8 -> (2000 + (minutes * 1000) / 60)
                    9 -> (3000 + (minutes * 1000) / 60)
                    10 -> (4000 + (minutes * 1000) / 60)
                    11 -> (5000 + (minutes * 1000) / 60)
                    else -> 0
                }

                server.worlds.forEach {
                    it.setTime(minecraftTime.toLong())
                }
                logger.info("Real time: ${currentTime.hour}:${currentTime.minute}, Minecraft time: $minecraftTime")
            },
            1L,
            20L * 60L
        )
    }

    override fun onDisable() {
    }
}
