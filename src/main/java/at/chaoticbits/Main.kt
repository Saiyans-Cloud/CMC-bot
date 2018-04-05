package at.chaoticbits

import at.chaoticbits.config.Bot
import at.chaoticbits.config.Config
import at.chaoticbits.updateshandlers.CryptoHandler
import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.TelegramBotsApi
import org.telegram.telegrambots.exceptions.TelegramApiException
import org.telegram.telegrambots.logging.BotLogger
import org.telegram.telegrambots.logging.BotsFileHandler
import org.yaml.snakeyaml.Yaml

import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.util.Objects
import java.util.logging.Level


/**
 * Main class to create bot
 */
object Main {

    private val LOG_TAG = "MAIN"

    var cryptoHandler: CryptoHandler? = null
        private set


    @JvmStatic
    fun main(args: Array<String>) {

        BotLogger.setLevel(Level.ALL)
        try {
            BotLogger.registerLogger(BotsFileHandler("./TelegramBots%g.%u.log"))
        } catch (e: IOException) {
            BotLogger.severe(LOG_TAG, e)
        }

        // exit if no telegram bot token is specified
        if (System.getenv("CMBOT_TELEGRAM_TOKEN") == null) {
            BotLogger.error(LOG_TAG, "No Telegram Bot Token specified! Please declare a System Environment Variable with your Telegram API Key. CMBOT_TELEGRAM_TOKEN={YOUR_API_KEY}")
            return
        }

        initialize()
    }


    /**
     * Initialize and register Telegram Bot
     */
    private fun initialize() {

        try {

            loadBotConfiguration()

            ApiContextInitializer.init()
            val telegramBotsApi = createLongPollingTelegramBotsApi()
            try {

                cryptoHandler = CryptoHandler()

                // Register long polling bots. They work regardless type of TelegramBotsApi
                telegramBotsApi.registerBot(cryptoHandler!!)

            } catch (e: TelegramApiException) {
                BotLogger.error(LOG_TAG, e)
            }

        } catch (e: Exception) {
            BotLogger.error(LOG_TAG, e)
        }

    }

    /**
     * Loads configuration properties from the provided config.yaml file
     * and populates the Config.class with values.
     */
    private fun loadBotConfiguration() {
        try {
            Bot.config = Yaml().loadAs(Main::class.java.classLoader.getResourceAsStream("config.yaml"), Config::class.java)
        } catch (e: NullPointerException) {
            BotLogger.error(LOG_TAG, "Error loading config.yaml! " + e.message)
        }

    }


    /**
     * Ceates a Telegram Bots Api to use Long Polling (getUpdates) bots.
     * @return TelegramBotsApi to register the bots.
     */
    private fun createLongPollingTelegramBotsApi(): TelegramBotsApi {
        return TelegramBotsApi()
    }

}
