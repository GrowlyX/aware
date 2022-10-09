package gg.scala.aware

import com.google.gson.Gson
import gg.scala.aware.thread.AwareThreadContext
import gg.scala.aware.uri.WrappedAwareUri
import io.lettuce.core.RedisClient
import io.lettuce.core.resource.ClientResources
import java.time.Duration
import java.util.concurrent.Executors
import java.util.logging.Level

/**
 * @author GrowlyX
 * @since 3/7/2022
 */
object AwareHub
{
    internal val scheduler = Executors
        .newSingleThreadScheduledExecutor()

    private lateinit var wrappedUri: WrappedAwareUri
    private var client: RedisClient? = null

    // TODO: 3/7/2022 allow for multiple
    //  serialization providers
    lateinit var gson: () -> Gson

    fun configure(
        wrappedUri: WrappedAwareUri,
        provider: () -> Gson
    )
    {
        this.wrappedUri = wrappedUri
        this.gson = provider
    }

    fun client(): RedisClient
    {
        if (client == null)
        {
            client = RedisClient.create(
                ClientResources.create(),
                this.wrappedUri.build()
            )
        }

        return client!!
    }

    fun close()
    {
        client?.shutdown()
    }

    @JvmStatic
    val DEF_TIMEOUT: Duration = Duration.ofSeconds(1L)

    fun <T : Any> publish(
        aware: Aware<T>,
        message: T,
        context: AwareThreadContext =
            AwareThreadContext.ASYNC,
        channel: String = aware.channel,
    )
    {
        if (
            context == AwareThreadContext.SYNC
        )
        {
            try
            {
                aware.publishConnection.sync()
                    .apply {
                        setTimeout(DEF_TIMEOUT)
                    }
                    .publish(channel, message)
            } catch (exception: Exception)
            {
                aware.logger.log(Level.WARNING, exception) {
                    "Something went wrong while trying to distribute a message."
                }
            }
            return
        }

        aware.publishConnection.async()
            .apply {
                setTimeout(DEF_TIMEOUT)
            }
            .publish(channel, message)
    }
}
