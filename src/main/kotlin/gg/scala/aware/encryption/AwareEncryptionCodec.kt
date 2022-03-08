package gg.scala.aware.encryption

import gg.scala.aware.codec.WrappedRedisCodec
import kotlin.reflect.KClass

/**
 * Uses the downstream codec to perform its serialization
 * and/or deserialization tasks, and then encrypts/decrypts accordingly.
 *
 * @author GrowlyX
 * @since 3/8/2022
 */
internal class AwareEncryptionCodec<V : Any>(
    codecType: KClass<V>,
    private val downstreamCodec: WrappedRedisCodec<V>,
    private val provider: AwareEncryptionProvider
) : WrappedRedisCodec<V>(codecType)
{
    companion object
    {
        @JvmStatic
        inline fun <reified V : Any> of(
            downstream: WrappedRedisCodec<V>,
            provider: AwareEncryptionProvider
        ) : AwareEncryptionCodec<V>
        {
            return AwareEncryptionCodec(
                V::class, downstream, provider
            )
        }
    }

    override fun interpretPacketId(v: V) =
        downstreamCodec.interpretPacketId(v)

    override fun encodeToString(v: V): String
    {
        val encodedDownstream =
            downstreamCodec.encodeToString(v)

        return provider
            .encrypt(encodedDownstream)
    }

    override fun decodeFromString(
        string: String, codec: KClass<V>
    ): Any
    {
        val decrypted = provider
            .decrypt(string)

        return downstreamCodec
            .decodeFromString(decrypted, codec)
    }
}
