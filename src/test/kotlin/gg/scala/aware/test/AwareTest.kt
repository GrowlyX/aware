package gg.scala.aware.test

import com.google.gson.GsonBuilder
import com.google.gson.LongSerializationPolicy
import gg.scala.aware.AwareBuilder
import gg.scala.aware.AwareHub
import gg.scala.aware.builder.WrappedAwareUri
import gg.scala.aware.codec.codecs.interpretation.AwareMessageCodec
import gg.scala.aware.message.AwareMessage
import org.junit.jupiter.api.Test

/**
 * @author GrowlyX
 * @since 3/7/2022
 */
object AwareTest
{
    @Test
    @JvmStatic
    fun main(args: Array<String>)
    {
        val gson = GsonBuilder()
            .setLongSerializationPolicy(LongSerializationPolicy.STRING)
            .create()

        // this can be called on your platform initialization,
        // or any major "module", or in context of Minecraft, a primary plugin.
        AwareHub.configure(
            WrappedAwareUri()
        ) {
            gson
        }

        val aware = AwareBuilder
            .of<AwareMessage>("twitter.com/growlygg")
            .codec(AwareMessageCodec)
            .build()

        aware.connect()
    }
}
