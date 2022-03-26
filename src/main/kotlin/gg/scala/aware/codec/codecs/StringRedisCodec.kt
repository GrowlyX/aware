package gg.scala.aware.codec.codecs

import gg.scala.aware.codec.WrappedRedisCodec
import kotlin.reflect.KClass

/**
 * Returns the provided string and interprets
 * the packetId by using the first element split by a colon.
 *
 * @author GrowlyX
 * @since 3/7/2022
 */
class StringRedisCodec : WrappedRedisCodec<String>(String::class)
{
    override fun encodeToString(v: String): String = v

    override fun decodeFromString(
        string: String, codec: KClass<String>
    ) = string

    override fun interpretPacketId(v: String): String
    {
        // We're going to assume the default
        // formatting for a string received is:
        // "packetId:some content"
        return v.split(":")[0]
    }
}
