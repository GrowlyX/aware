package gg.scala.aware

import gg.scala.aware.codec.WrappedRedisCodec
import gg.scala.aware.codec.codecs.JsonRedisCodec
import java.util.logging.Logger
import kotlin.reflect.KClass

/**
 * Allows for an easy, builder-style method to
 * create a new [Aware] instance.
 *
 * @author GrowlyX
 * @since 3/7/2022
 */
class AwareBuilder<V : Any>(
    private val channel: String,
    private val codecType: KClass<V>
)
{
    companion object
    {
        @JvmStatic
        inline fun <reified T : Any> of(
            channel: String
        ): AwareBuilder<T>
        {
            return AwareBuilder(channel, T::class)
        }

        @JvmStatic
        fun <T : Any> of(
            channel: String, tType: KClass<T>
        ): AwareBuilder<T>
        {
            return AwareBuilder(channel, tType)
        }
    }

    private lateinit var codec: WrappedRedisCodec<V>

    private var ignorePacketId = false
    private var logger = Logger.getAnonymousLogger()

    fun codec(
        codec: WrappedRedisCodec<V>
    ): AwareBuilder<V>
    {
        this.codec = codec
        return this
    }

    fun ignorePacketId(
        ignore: Boolean
    ): AwareBuilder<V>
    {
        this.ignorePacketId = ignore
        return this
    }

    fun logger(
        logger: Logger
    ): AwareBuilder<V>
    {
        this.logger = logger
        return this
    }

    fun build(): Aware<V>
    {
        return Aware(
            this.logger, this.channel,
            this.ignorePacketId, this.codec, this.codecType
        )
    }
}
