package gg.scala.aware

import gg.scala.aware.annotation.ExpiresIn
import gg.scala.aware.annotation.Subscribe
import gg.scala.aware.codec.WrappedRedisCodec
import gg.scala.aware.connection.WrappedRedisPubSubListener
import gg.scala.aware.context.AwareSubscriptionContext
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection
import java.lang.reflect.Method
import java.util.concurrent.CompletionStage
import java.util.logging.Logger
import kotlin.reflect.KClass
import kotlin.reflect.jvm.kotlinFunction

/**
 * The central processor for
 * an aware service.
 *
 * @author GrowlyX
 * @since 3/7/2022
 */
class Aware<V : Any>(
    val logger: Logger,
    val channel: String,
    private val codec: WrappedRedisCodec<V>,
    private val codecType: KClass<V>
)
{
    val subscriptions =
        mutableListOf<AwareSubscriptionContext>()

    private lateinit var connection:
            StatefulRedisPubSubConnection<String, V>

    internal lateinit var publishConnection:
            StatefulRedisConnection<String, V>

    // we don't want them to be instantiated instantly.
    private val client by lazy {
        AwareHub.newClient()
    }

    fun register(any: Any)
    {
        for (method in any.javaClass.methods)
        {
            internalRegister(method, any)
        }
    }

    private fun internalRegister(
        method: Method, instance: Any
    )
    {
        if (method.parameters.isEmpty())
            return

        val firstParameter = method.parameters[0]

        // we don't want methods without our codec type
        if (firstParameter.type != codecType.java)
        {
            return
        }

        val context = AwareSubscriptionContext(
            instance, method, method.kotlinFunction
                ?.annotations?.toList() ?: method.annotations.toList()
        )

        val subscriptions = context
            .byType<Subscribe>()

        if (subscriptions.isEmpty())
            return

        this.subscriptions.add(context)
    }

    fun connect(): CompletionStage<Void>
    {
        connection = client
            .connectPubSub(codec)

        connection.addListener(
            WrappedRedisPubSubListener(this, codec)
        )

        publishConnection = client
            .connect(codec)

        return connection.async().subscribe(channel)
            .thenRun {
                for (subscription in subscriptions)
                {
                    scheduleRemoval(subscription)
                }
            }
    }

    private fun scheduleRemoval(context: AwareSubscriptionContext)
    {
        val expiresIn = context
            .byType<ExpiresIn>()

        if (expiresIn.isNotEmpty())
        {
            // ExpiresIn is not repeatable
            val first = expiresIn[0]

            // schedule the removal of this method
            AwareHub.scheduler.schedule(
                {
                    this.subscriptions.remove(context)
                },
                first.duration, first.timeUnit
            )
        }
    }
}
