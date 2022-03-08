package gg.scala.aware.codec.codecs

import com.google.gson.Gson
import gg.scala.aware.AwareHub
import gg.scala.aware.codec.WrappedRedisCodec
import gg.scala.aware.codec.codecs.interpretation.AwareMessageCodec
import java.lang.reflect.Field
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
open class JsonRedisCodec<V : Any> : WrappedRedisCodec<V>()
{
    /**
     * Implementations can provide their own
     * packet field of the provided type [V].
     *
     * In [AwareMessageCodec]'s case, they do
     * not have to as the default packet field is "packet".
     */
    open val defaultPacketField: Field =
        codecType.getField("packet")

    override fun encodeToString(v: V): String =
        useGson { toJson(v) }

    override fun decodeFromString(
        string: String, codec: KClass<V>
    ): Any =
        useGson { fromJson(string, codec.java) }

    private fun <R> useGson(lambda: Gson.() -> R) =
        AwareHub.gson.invoke().lambda()

    override fun interpretPacketId(v: V): String
    {
        return defaultPacketField
            .get(v).toString()
    }
}
