package gg.scala.aware.codec.codecs

import com.google.gson.Gson
import gg.scala.aware.AwareHub
import gg.scala.aware.codec.WrappedRedisCodec
import gg.scala.aware.codec.codecs.interpretation.AwareMessageCodec
import java.lang.reflect.Field
import kotlin.reflect.KClass
import kotlin.reflect.jvm.kotlinProperty

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
