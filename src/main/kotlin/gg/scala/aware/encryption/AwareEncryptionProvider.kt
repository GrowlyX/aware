package gg.scala.aware.encryption

/**
 * Allows for the end-user to
 * specify their own encryption
 * method, if they even want
 * one in the first place.
 *
 * @author GrowlyX
 * @since 3/8/2022
 */
interface AwareEncryptionProvider
{
    fun encrypt(input: String): String
    fun decrypt(input: String): String
}
