package gg.scala.aware.encryption.providers

import gg.scala.aware.encryption.AwareEncryptionProvider
import java.util.*

/**
 * This is PURELY for testing.
 *
 * @author GrowlyX
 * @since 3/8/2022
 */
object Base64EncryptionProvider : AwareEncryptionProvider
{
    override fun encrypt(input: String): String
    {
        return Base64.getEncoder()
            .encodeToString(input.toByteArray())
    }

    override fun decrypt(input: String): String
    {
        return String(
            Base64.getDecoder().decode(input)
        )
    }
}
