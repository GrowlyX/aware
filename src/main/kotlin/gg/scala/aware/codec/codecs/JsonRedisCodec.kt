package gg.scala.aware.codec.codecs

import com.google.gson.Gson
import gg.scala.aware.AwareHub
import gg.scala.aware.codec.WrappedRedisCodec
import kotlin.reflect.KClass

/**
 * Encodes/decodes an object from/to
 * a valid Json formatted string.
 *
 * @see Gson
 *
 * @author GrowlyX
 * @since 3/7/2022
 */
abstract class JsonRedisCodec<V : Any>(
    codec: KClass<V>
) : WrappedRedisCodec<V>(codec)
{
    companion object
    {
        @JvmStatic
        inline fun <reified V : Any> of(
            noinline packet: (V) -> String
        ) : JsonRedisCodec<V>
        {
            return object : JsonRedisCodec<V>(V::class)
            {
                override fun getPacketId(v: V) = packet.invoke(v)
            }
        }
    }

    override fun encodeToString(v: V): String =
        useGson { toJson(v) }

    override fun decodeFromString(
        string: String, codec: KClass<V>
    ): Any =
        useGson { fromJson(string, codec.java) }

    private fun <R> useGson(lambda: Gson.() -> R) =
        AwareHub.gson.invoke().lambda()

    abstract fun getPacketId(v: V): String

    override fun interpretPacketId(v: V): String
    {
        return getPacketId(v)
    }
}
