package gg.scala.aware

import com.google.gson.Gson
import gg.scala.aware.uri.WrappedAwareUri
import gg.scala.aware.thread.AwareThreadContext
import io.lettuce.core.RedisClient
import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.ForkJoinPool
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

    fun newClient(): RedisClient
    {
        return RedisClient.create(wrappedUri.build())
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
        val runnable: (Boolean) -> Unit = ctx@{ timeout ->
            try
            {
                aware.publishConnection.sync()
                    .apply {
                        if (timeout)
                            setTimeout(DEF_TIMEOUT)
                    }
                    .publish(channel, message)
            } catch (exception: Exception)
            {
                aware.logger.log(Level.WARNING, exception) {
                    "Something went wrong while trying to distribute a message."
                }
            }
        }

        println(
            Thread.currentThread().name
        )

        if (context == AwareThreadContext.SYNC)
        {
            runnable.invoke(true)
            return
        }

        ForkJoinPool.commonPool()
            .run { runnable.invoke(false) }
    }
}
