package gg.scala.aware.codec.codecs

import gg.scala.aware.codec.WrappedRedisCodec
import kotlin.reflect.KClass

/**
 * Returns the provided string,
 * there's not really much to do here.
 *
 * @author GrowlyX
 * @since 3/7/2022
 */
class StringRedisCodec : WrappedRedisCodec<String>()
{
    override fun encodeToString(v: String): String = v

    override fun decodeFromString(
        string: String, codec: KClass<String>
    ) = string
}
