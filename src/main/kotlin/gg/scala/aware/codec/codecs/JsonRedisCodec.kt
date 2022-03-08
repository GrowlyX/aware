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
class JsonRedisCodec<V : Any> : WrappedRedisCodec<V>()
{
    override fun encodeToString(v: V): String =
        useGson { toJson(v) }

    override fun decodeFromString(
        string: String, codec: KClass<V>
    ): Any =
        useGson { fromJson(string, codec.java) }

    private fun <R> useGson(lambda: Gson.() -> R) =
        AwareHub.gson.invoke().lambda()
}
