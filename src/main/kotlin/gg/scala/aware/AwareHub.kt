package gg.scala.aware

import gg.scala.aware.builder.WrappedAwareUri
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.codec.RedisCodec

/**
 * @author GrowlyX
 * @since 3/7/2022
 */
object AwareHub
{
    lateinit var wrappedUri: WrappedAwareUri

    fun configure(
        wrappedUri: WrappedAwareUri
    )
    {
        RedisClient.create(wrappedUri.build())
            .connectPubSub()

    }
}
