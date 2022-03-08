package gg.scala.aware

import gg.scala.aware.annotation.ExpiresIn
import gg.scala.aware.annotation.Subscribe
import gg.scala.aware.codec.WrappedRedisCodec
import gg.scala.aware.connection.WrappedRedisPubSubListener
import gg.scala.aware.subscription.AwareSubscriptionContext
import gg.scala.aware.subscription.AwareSubscriptionContextTypes
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection
import java.lang.reflect.Method
import java.util.concurrent.CompletionStage
import java.util.logging.Logger
import kotlin.reflect.KClass
import kotlin.reflect.jvm.kotlinFunction
import kotlin.system.measureTimeMillis

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
        mutableListOf<AwareSubscriptionContext<*>>()

    private lateinit var connection:
            StatefulRedisPubSubConnection<String, V>

    internal lateinit var publishConnection:
            StatefulRedisConnection<String, V>

    // we don't want them to be instantiated instantly.
    private val client by lazy {
        AwareHub.newClient()
    }

    fun listen(
        packet: String,
        vararg additionalAnnotations: Annotation,
        lambda: V.() -> Unit
    )
    {
        val methodContext =
            AwareSubscriptionContextTypes
                .LAMBDA.asT<(V) -> Unit>()

        val subscriptions = additionalAnnotations
            .toMutableList().apply {
                add(Subscribe(packet))
            }

        val context = AwareSubscriptionContext(
            this, lambda,
            methodContext, subscriptions
        )

        this.subscriptions.add(context)
    }

    fun listen(any: Any)
    {
        for (method in any.javaClass.methods)
        {
            internalListen(method, any)
        }
    }

    private fun internalListen(
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

        val methodContext =
            AwareSubscriptionContextTypes.METHOD.asT<Method>()

        val methods = method.kotlinFunction
            ?.annotations?.toList() ?: method.annotations.toList()

        val context = AwareSubscriptionContext(
            instance, method, methodContext, methods
        )

        val subscriptions = context
            .byType<Subscribe>()

        if (subscriptions.isEmpty())
            return

        this.subscriptions.add(context)
    }

    fun ping(): Pair<String, Long>
    {
        val pingFunction = {
            publishConnection.sync().ping()
        }

        return Pair(
            pingFunction.invoke(),
            measureTimeMillis {
                pingFunction.invoke()
            },
        )
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

    fun shutdown()
    {
        client.shutdown()
    }

    private fun scheduleRemoval(
        context: AwareSubscriptionContext<*>
    )
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
