package gg.scala.aware.test

import com.google.gson.GsonBuilder
import com.google.gson.LongSerializationPolicy
import gg.scala.aware.Aware
import gg.scala.aware.AwareBuilder
import gg.scala.aware.AwareHub
import gg.scala.aware.annotation.ExpiresIn
import gg.scala.aware.annotation.Subscribe
import gg.scala.aware.uri.WrappedAwareUri
import gg.scala.aware.codec.codecs.JsonRedisCodec
import gg.scala.aware.codec.codecs.interpretation.AwareMessageCodec
import gg.scala.aware.thread.AwareThreadContext
import gg.scala.aware.encryption.AwareEncryptionCodec
import gg.scala.aware.encryption.providers.Base64EncryptionProvider
import gg.scala.aware.message.AwareMessage
import org.junit.jupiter.api.Test
import java.lang.Thread.sleep
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.random.Random

/**
 * @author GrowlyX
 * @since 3/7/2022
 */
object AwareTest
{
    @Test
    fun test()
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

        // encryption ong
        val encryption = AwareEncryptionCodec.of(
            AwareMessageCodec, Base64EncryptionProvider
        )

        val aware = AwareBuilder
            .of<AwareMessage>("twitter.com/growlygg")
            // You can do this:
            .codec(AwareMessageCodec)
            // Or you can do this:
            .codec(JsonRedisCodec.of { it.packet })
            // encryption? BASE64
            .codec(encryption)
            .build()

        aware.listen(this)

        aware.listen(
            "test",
            ExpiresIn(30L, TimeUnit.SECONDS)
        ) {
            val horse = retrieve<String>("horse")

            println("Lets go, hasn't been 30s? $horse")
        }

        aware.connect().thenRun {
            launchInfinitePublisher(aware)
        }

        thread {
            while (true)
            {
                sleep(Long.MAX_VALUE)
            }
        }
    }

    fun launchInfinitePublisher(
        aware: Aware<AwareMessage>
    )
    {
        thread {
            while (true)
            {
                AwareMessage.of(
                    "test", aware,
                    "horse" to "heyy-${Random.nextFloat()}"
                ).publish(
                    // supplying our own thread context
                    AwareThreadContext.SYNC
                )

                sleep(1000L)
            }
        }
    }

    @Subscribe("test")
    @ExpiresIn(30L, TimeUnit.SECONDS)
    fun onTestExpiresIn30Seconds(
        message: AwareMessage
    )
    {
        val horse = message.retrieve<String>("horse")

        println("Hey! It's not been 30 seconds. :) ($horse)")
    }
}
