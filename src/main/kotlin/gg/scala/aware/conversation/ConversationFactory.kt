package gg.scala.aware.conversation

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.RemovalCause
import gg.scala.aware.AwareBuilder
import gg.scala.aware.AwareHub
import gg.scala.aware.codec.codecs.JsonRedisCodec
import gg.scala.aware.conversation.messages.ConversationMessage
import gg.scala.aware.conversation.messages.ConversationMessageResponse
import java.io.Closeable
import java.util.*
import java.util.concurrent.CompletionStage
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

/**
 * The primary engine for
 * aware conversations.
 *
 * [T]: the origin message
 * [U]: the response
 *
 * @author GrowlyX
 * @since 3/9/2022
 */
class ConversationFactory<T : ConversationMessage, U : ConversationMessageResponse>(
    val messageType: KClass<T>, val responseType: KClass<U>, private val channel: String,
    timeoutDuration: Long, timeoutTimeUnit: TimeUnit,
    timeoutFunction: (T) -> Unit,
    private val processorFunction: (T) -> U,
    private val responseFunction: (T, U) -> ConversationContinuation
) : Closeable
{
    private val uniqueId = UUID.randomUUID()

    private val messages = Caffeine.newBuilder()
        .removalListener<UUID, T> { _, value, cause ->
            if (cause == RemovalCause.EXPIRED)
                timeoutFunction.invoke(value!!)
        }
        .expireAfterWrite(
            timeoutDuration, timeoutTimeUnit
        )
        .build<UUID, T>()

    private val message =
        object : JsonRedisCodec<T>(messageType)
        {
            override fun getPacketId(v: T) =
                v.uniqueId.toString()
        }

    private val response =
        object : JsonRedisCodec<U>(responseType)
        {
            override fun getPacketId(v: U) =
                v.uniqueId.toString()
        }

    private val outgoing by lazy {
        AwareBuilder
            .of("og-$channel", messageType)
            .codec(message)
            .ignorePacketId(true)
            .build()
    }

    private val incoming by lazy {
        AwareBuilder
            .of("ic-$channel", responseType)
            .codec(response)
            .ignorePacketId(true)
            .build()
    }

    fun configure(): CompletionStage<Void>
    {
        outgoing.listen("") {
            val processed =
                processorFunction.invoke(this)

            // update the call context to this
            processed.callContext = uniqueId

            // distribute the response back to the call site
            AwareHub.publish(
                incoming, processed
            )
        }

        incoming.listen("") {
            // check if the call context is
            // the same as our unique id
            if (callContext != uniqueId)
                return@listen

            // check if the message has
            // been invalidated or not
            val origin = messages
                .get(this.uniqueId) {
                    null
                }
                ?: return@listen

            val context = responseFunction
                .invoke(origin, this)

            // end the conversation if we
            // received an END context
            if (context == ConversationContinuation.END)
            {
                messages.invalidate(this.uniqueId)
            }
        }

        return incoming.connect()
            .thenCompose {
                outgoing.connect()
            }
    }

    fun distribute(message: T)
    {
        message.callContext = this.uniqueId

        AwareHub.publish(
            outgoing, message
        )

        messages.put(
            message.uniqueId, message
        )
    }


    override fun close()
    {
        outgoing.shutdown()
        incoming.shutdown()

        messages.cleanUp()
    }
}
