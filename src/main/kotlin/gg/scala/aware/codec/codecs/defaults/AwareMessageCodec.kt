package gg.scala.aware.codec.codecs.defaults

import gg.scala.aware.codec.codecs.JsonRedisCodec
import gg.scala.aware.message.AwareMessage

/**
 * A default implementation for [JsonRedisCodec]
 * providing an [AwareMessage] value type.
 *
 * @author GrowlyX
 * @since 3/7/2022
 */
object AwareMessageCodec : JsonRedisCodec<AwareMessage>()
