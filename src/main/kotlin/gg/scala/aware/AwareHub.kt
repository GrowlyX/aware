package gg.scala.aware

import com.google.gson.Gson
import gg.scala.aware.uri.WrappedAwareUri
import gg.scala.aware.context.AwareThreadContext
import io.lettuce.core.RedisClient
import java.time.Duration
import java.util.concurrent.Executors

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
            AwareThreadContext.ASYNC
    )
    {
        if (context == AwareThreadContext.SYNC)
        {
            aware.publishConnection.sync()
                .apply {
                    setTimeout(DEF_TIMEOUT)
                }
                .publish(aware.channel, message)
        } else
        {
            aware.publishConnection.async()
                .apply {
                    setTimeout(DEF_TIMEOUT)
                }
                .publish(aware.channel, message)
        }
    }
}
