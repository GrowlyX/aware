package gg.scala.aware.message

import gg.scala.aware.Aware
import gg.scala.aware.AwareHub
import gg.scala.aware.thread.AwareThreadContext

/**
 * A standardized "message" containing
 * the packetId & any other data.
 *
 * @author GrowlyX
 * @since 3/7/2022
 */
data class AwareMessage(
    val packet: String
)
{
    companion object
    {
        @JvmStatic
        fun of(
            packet: String,
            aware: Aware<AwareMessage>,
            vararg pair: Pair<String, Any>
        ): AwareMessage
        {
            val message = AwareMessage(packet)
                .apply {
                    pair.forEach {
                        assign(it.first, it.second)
                    }
                }
                .apply {
                    this.aware = aware
                }

            return message
        }
    }

    @Transient
    lateinit var aware: Aware<AwareMessage>

    val content =
        mutableMapOf<String, String>()

    inline fun <reified T> retrieve(key: String): T
    {
        return AwareHub.gson
            .invoke().fromJson(
                content[key], T::class.java
            )
    }

    inline fun <reified T> retrieveNullable(key: String): T?
    {
        return AwareHub.gson
            .invoke().fromJson(
                content[key], T::class.java
            )
    }

    operator fun contains(key: String): Boolean
    {
        return content.containsKey(key)
    }

    fun assign(key: String, value: Any)
    {
        content[key] = AwareHub.gson
            .invoke().toJson(value)
    }

    fun remove(key: String)
    {
        content.remove(key)
    }

    fun publish(
        context: AwareThreadContext =
            AwareThreadContext.ASYNC,
        channel: String = aware.channel
    )
    {
        AwareHub.publish(
            aware, this, context, channel
        )
    }
}
