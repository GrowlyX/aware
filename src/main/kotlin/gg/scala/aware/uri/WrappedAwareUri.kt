package gg.scala.aware.uri

/**
 * Takes in credentials in its raw
 * form, and creates a valid redis
 * connection URI out of it.
 *
 * @author GrowlyX
 * @since 3/7/2022
 */
data class WrappedAwareUri(
    val address: String = "127.0.0.1",
    val port: Int = 6379,
    val password: String? = null,
    val index: Int = 0
)
{
    fun build(): String
    {
        var builder = "redis://"

        if (password != null)
        {
            builder += "$password@"
        }

        builder += address
        builder += ":$port"

        builder += "/$index"

        return builder
    }
}
