package gg.scala.aware.message

import gg.scala.aware.Aware
import gg.scala.aware.AwareHub
import gg.scala.aware.thread.AwareThreadContext
import kotlin.reflect.KClass

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
            vararg pair: Pair<String, Any?>
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
        mutableMapOf<String, Any?>()

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> retrieve(
        key: String, kClass: KClass<T>
    ): T
    {
        val value = content[key]

        if (kClass == String::class)
        {
            return value as T
        }

        return AwareHub.gson
            .invoke().fromJson(
                value.toString(),
                kClass.java
            )
    }


    inline fun <reified T : Any> retrieve(key: String): T
    {
        return retrieve(key, T::class)
    }

    inline fun <reified T> retrieveNullable(key: String): T?
    {
        val value = content[key]
            ?: return null

        if (T::class == String::class)
        {
            return value as T
        }

        return AwareHub.gson
            .invoke().fromJson(
                value as String,
                T::class.java
            )
    }

    operator fun contains(key: String): Boolean
    {
        return content.containsKey(key)
    }

    fun assign(key: String, value: Any?)
    {
        content[key] = value
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
