package gg.scala.aware.connection

import gg.scala.aware.Aware
import gg.scala.aware.annotation.Subscribe
import gg.scala.aware.codec.WrappedRedisCodec
import io.lettuce.core.pubsub.RedisPubSubListener
import java.util.logging.Level

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
            aware.logger.warning("[redis] A null message was sent on $channel!")
            return
        }

        val packetIdentifier = chosenCodec
            .interpretPacketId(message)
            .lowercase()

        val matches = aware.subscriptions
            .let {
                if (!aware.ignorePacketId)
                {
                    it.filter { ctx ->
                        ctx.subscription.value.lowercase() == packetIdentifier
                    }
                } else it
            }

        for (context in matches)
        {
            kotlin
                .runCatching {
                    context.contextType
                        .launchCasted(context, message)
                }
                .onFailure {
                    aware.logger.log(Level.WARNING, it) {
                        "[redis] An exception was thrown on channel ${aware.channel}"
                    }
                }
        }
    }

    override fun subscribed(channel: String, count: Long)
    {
        aware.logger.info("[redis] Subscribed to pub/sub on channel: ${aware.channel}")
    }

    override fun unsubscribed(channel: String, count: Long)
    {
        aware.logger.info("[redis] Unsubscribed from pub/sub on channel: ${aware.channel}.")
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
