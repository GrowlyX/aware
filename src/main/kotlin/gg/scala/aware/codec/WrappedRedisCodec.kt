package gg.scala.aware.codec

import io.lettuce.core.codec.RedisCodec
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import kotlin.reflect.KClass

/**
 * Allows users to easily
 * implement new [RedisCodec]s.
 *
 * @author GrowlyX
 * @since 3/7/2022
 */
@Suppress("UNCHECKED_CAST")
abstract class WrappedRedisCodec<V : Any>(
    private val codecType: KClass<V>
) : RedisCodec<String, V>
{
    private val utf8Charset = StandardCharsets.UTF_8
    private val emptyByteArray = byteArrayOf()

    override fun decodeKey(
        bytes: ByteBuffer
    ): String?
    {
        return utf8Charset
            .decode(bytes)
            .toString()
    }

    override fun decodeValue(bytes: ByteBuffer): V?
    {
        val stringForm = utf8Charset
            .decode(bytes)
            .toString()

        return decodeFromString(stringForm, codecType) as V
    }

    override fun encodeKey(key: String?): ByteBuffer
    {
        return ByteBuffer.wrap(
            key?.encodeToByteArray() ?: emptyByteArray
        )
    }

    override fun encodeValue(value: V?): ByteBuffer
    {
        return ByteBuffer.wrap(
            if (value == null)
            {
                emptyByteArray
            } else
            {
                encodeToString(value)
                    .encodeToByteArray()
            }
        )
    }

    abstract fun interpretPacketId(v: V): String

    abstract fun encodeToString(v: V): String
    abstract fun decodeFromString(string: String, codec: KClass<V>): Any
}
