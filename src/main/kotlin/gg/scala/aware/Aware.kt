package gg.scala.aware

import gg.scala.aware.codec.WrappedRedisCodec
import gg.scala.aware.connection.WrappedRedisPubSubListener
import gg.scala.aware.context.AwareSubscriptionContext
import java.util.logging.Logger
import kotlin.reflect.KClass

/**
 * The central processor for
 * an aware service.
 *
 * @author GrowlyX
 * @since 3/7/2022
 */
@Suppress("UNCHECKED_CAST")
class Aware<V : Any>(
    val logger: Logger,
    private val codec: WrappedRedisCodec<V>,
    val channel: String
)
{
    internal val codecType = getTypes()[0] as KClass<V>

    val subscriptions =
        mutableListOf<AwareSubscriptionContext>()

    private val client by lazy {
        AwareHub.newClient()
    }

    fun connect()
    {
        val connection = client
            .connectPubSub(codec)

        connection.addListener(
            WrappedRedisPubSubListener(this, codec)
        )
    }

}
