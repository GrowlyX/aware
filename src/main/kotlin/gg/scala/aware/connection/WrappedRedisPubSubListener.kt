package gg.scala.aware.connection

import gg.scala.aware.Aware
import gg.scala.aware.codec.WrappedRedisCodec
import io.lettuce.core.pubsub.RedisPubSubListener

/**
 * @author GrowlyX
 * @since 3/7/2022
 */
class WrappedRedisPubSubListener<V : Any>(
    private val aware: Aware,
    chosenCodec: WrappedRedisCodec<V>
) : RedisPubSubListener<String, V>
{
    override fun message(channel: String, message: V)
    {
        TODO("Not yet implemented")
    }

    override fun message(
        pattern: String, channel: String, message: V
    )
    {
        TODO("Not yet implemented")
    }

    override fun subscribed(channel: String, count: Long)
    {
        TODO("Not yet implemented")
    }

    override fun psubscribed(pattern: String, count: Long)
    {
        TODO("Not yet implemented")
    }

    override fun unsubscribed(channel: String, count: Long)
    {
        TODO("Not yet implemented")
    }

    override fun punsubscribed(pattern: String, count: Long)
    {
        TODO("Not yet implemented")
    }
}
