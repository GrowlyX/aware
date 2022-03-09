package gg.scala.aware.connection

import gg.scala.aware.Aware
import gg.scala.aware.annotation.Subscribe
import gg.scala.aware.codec.WrappedRedisCodec
import io.lettuce.core.pubsub.RedisPubSubListener

/**
 * A wrapped form of [RedisPubSubListener] containing
 * all of our interpretation & distribution functionality.
 *
 * @author GrowlyX
 * @since 3/7/2022
 */
class WrappedRedisPubSubListener<V : Any>(
    private val aware: Aware<V>,
    private val chosenCodec: WrappedRedisCodec<V>
) : RedisPubSubListener<String, V>
{
    override fun message(channel: String, message: V?)
    {
        // making sure this message is from a
        // channel we're looking for
        if (channel != aware.channel)
            return

        if (message == null)
        {
            aware.logger.warning("[Aware] A null message was sent on $channel!")
            return
        }

        val packetIdentifier = chosenCodec
            .interpretPacketId(message)
            .lowercase()

        val matches = aware.subscriptions
            .filter {
                it.byType<Subscribe>().any { subscribe ->
                    subscribe.value.lowercase() == packetIdentifier
                }
            }

        for (context in matches)
        {
            kotlin.runCatching {
                context.contextType
                    .launchCasted(context, message)
            }
        }
    }

    override fun subscribed(channel: String, count: Long)
    {
        aware.logger.info("[Aware] Subscribed through aware on \"${aware.channel}\".")
    }

    override fun unsubscribed(channel: String, count: Long)
    {
        aware.logger.info("[Aware] Unsubscribed from aware on \"${aware.channel}\".")
    }

    /**
     * We don't use any of the following methods.
     */
    override fun psubscribed(pattern: String, count: Long) = Unit
    override fun punsubscribed(pattern: String, count: Long) = Unit

    override fun message(
        pattern: String, channel: String, message: V?
    ) = Unit
}
