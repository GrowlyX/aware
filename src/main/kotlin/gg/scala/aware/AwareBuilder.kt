package gg.scala.aware

import gg.scala.aware.codec.WrappedRedisCodec
import java.util.logging.Logger
import kotlin.reflect.KClass

/**
 * Allows for an easy, builder-style method to
 * create a new [Aware] instance.
 *
 * @author GrowlyX
 * @since 3/7/2022
 */
internal class AwareBuilder<V : Any>(
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
    }

    private lateinit var codec: WrappedRedisCodec<V>

    private var logger = Logger.getAnonymousLogger()

    fun codec(
        codec: WrappedRedisCodec<V>
    ): AwareBuilder<V>
    {
        this.codec = codec
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
            this.logger, this.channel, this.codec, this.codecType
        )
    }
}
