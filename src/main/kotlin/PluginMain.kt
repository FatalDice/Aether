package uk.akane.aether

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.events.BotOnlineEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.utils.info
import uk.akane.aether.BuildConstants

object PluginMain : KotlinPlugin(
    JvmPluginDescription(
        id = "uk.akane.aether",
        name = "Aether",
        version = BuildConstants.MAJOR_VERSION
    ) {
        author("AkaneTan")
        info(
            """
            AetherNode
        """.trimIndent()
        )
    }
) {
    private var bot: Bot? = null
    override fun onEnable() {
        logger.info { "Plugin loaded" }
        WebhookServer.startWebhookServer { str ->
            CoroutineScope(Dispatchers.IO).launch {
                bot?.groups?.forEach {
                    it.sendMessage(str)
                }
            }
        }
        globalEventChannel().subscribeAlways<BotOnlineEvent> {
            PluginMain.bot = bot
        }
        globalEventChannel().subscribeAlways<GroupMessageEvent> {
            if (message.contentToString().substring(1).startsWith("bot") &&
                message.contentToString().length <= 4) {
                group.sendMessage(getVersionString())
            }
        }
    }

    private fun getVersionString(): String =
        "${VersionUtils.getPluginVersion()}\n\n" +
            "${VersionUtils.getCompilationTime()}\n" +
            "${VersionUtils.getJdkVersion()} (${VersionUtils.getOSName()})"
}
