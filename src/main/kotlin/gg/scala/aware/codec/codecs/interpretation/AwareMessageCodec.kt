package gg.scala.aware.codec.codecs.interpretation

import gg.scala.aware.codec.codecs.JsonRedisCodec
import gg.scala.aware.message.AwareMessage
import java.lang.reflect.Field

/**
 * A default implementation for [JsonRedisCodec]
 * providing an [AwareMessage] value type.
 *
 * @author GrowlyX
 * @since 3/7/2022
 */
object AwareMessageCodec : JsonRedisCodec<AwareMessage>()
