package gg.scala.aware.connection

import gg.scala.aware.Aware
import io.lettuce.core.pubsub.RedisPubSubListener

/**
 * @author GrowlyX
 * @since 3/7/2022
 */
class WrappedRedisPubSubListener(
    private val aware: Aware
) : RedisPubSubListener<String, String>
{
    override fun message(channel: String, message: String)
    {
        TODO("Not yet implemented")
    }

    override fun message(
        pattern: String, channel: String, message: String
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
