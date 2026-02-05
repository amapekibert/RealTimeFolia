package org.titago

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import java.time.ZoneId
import java.time.ZonedDateTime
import io.papermc.paper.threadedregions.scheduler.ScheduledTask

class RealTimeFolia : JavaPlugin() {

    private var task: ScheduledTask? = null

    override fun onEnable() {
        if (!dataFolder.exists()) {
            dataFolder.mkdirs()
        }

        if (getResource("config.yml") == null) {
            logger.severe("config.yml not found in JAR resources.")
        } else {
            saveDefaultConfig()
        }

        startTimer()
        logger.info("RealTimeFolia enabled successfully.")
    }

    override fun onDisable() {
        task?.cancel()
        logger.info("RealTimeFolia has been disabled.")
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (command.name.equals("realtimefolia", ignoreCase = true)) {
            if (args.isNotEmpty() && args[0].equals("reload", ignoreCase = true)) {
                reloadConfig()
                startTimer()
                sender.sendMessage("Configuration reloaded!")
                return true
            }
        }
        return false
    }

    private fun startTimer() {
        task?.cancel()

        task = server.globalRegionScheduler.runAtFixedRate(this, { _ ->
            val worldsSection = config.getConfigurationSection("worlds") ?: return@runAtFixedRate

            for (worldName in worldsSection.getKeys(false)) {
                val world = Bukkit.getWorld(worldName) ?: continue
                val timezoneStr = worldsSection.getString("$worldName.timezone") ?: "UTC"

                val zoneId = try {
                    ZoneId.of(timezoneStr)
                } catch (e: Exception) {
                    ZoneId.of("UTC")
                }

                val now = ZonedDateTime.now(zoneId)
                val totalSeconds = now.toLocalTime().toSecondOfDay()

                var minecraftTime = ((totalSeconds.toLong() * 1000) / 3600) - 6000
                if (minecraftTime < 0) {
                    minecraftTime += 24000
                }

                world.time = minecraftTime
            }
        }, 1L, 100L)
    }
}