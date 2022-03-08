package gg.scala.aware

import com.google.gson.Gson
import gg.scala.aware.builder.WrappedAwareUri
import io.lettuce.core.RedisClient

/**
 * @author GrowlyX
 * @since 3/7/2022
 */
object AwareHub
{
    lateinit var wrappedUri: WrappedAwareUri

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
}
