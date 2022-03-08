package gg.scala.aware.message

import gg.scala.aware.AwareHub

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
            vararg pair: Pair<String, Any>
        ): AwareMessage
        {
            val message = AwareMessage(packet)
                .apply {
                    pair.forEach {
                        assign(it.first, it.second)
                    }
                }

            return message
        }
    }

    private val content =
        mutableMapOf<String, String>()

    inline fun <reified T> retrieve(key: String): T
    {
        return AwareHub.gson
            .invoke().fromJson(
                key, T::class.java
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
}
