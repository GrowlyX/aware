package gg.scala.aware

import com.google.gson.Gson
import gg.scala.aware.uri.WrappedAwareUri
import gg.scala.aware.thread.AwareThreadContext
import io.lettuce.core.RedisClient
import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.ForkJoinPool

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
        val runnable = ctx@{
            aware.publishConnection.sync()
                .apply {
                    setTimeout(DEF_TIMEOUT)
                }
                .publish(channel, message)
        }

        val forkJoinPool = Thread.currentThread()
            .name.contains("ForkJoinPool", true)

        // We're not using async commands as its sort of messing stuff up
        if (context == AwareThreadContext.SYNC || forkJoinPool)
        {
            runnable.invoke()
            return
        }

        ForkJoinPool.commonPool()
            .run { runnable.invoke() }
    }
}
