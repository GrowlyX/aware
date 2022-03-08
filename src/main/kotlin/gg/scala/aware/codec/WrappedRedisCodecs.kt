package gg.scala.aware.codec

import kotlin.reflect.KClass

/**
 * @author GrowlyX
 * @since 3/7/2022
 */
object WrappedRedisCodecs
{
    @JvmStatic
    val CODECS = mutableListOf<KClass<out WrappedRedisCodec<*>>>()
}
