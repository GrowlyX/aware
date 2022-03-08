package gg.scala.aware

import java.util.logging.Logger

/**
 * @author GrowlyX
 * @since 3/7/2022
 */
class Aware(
    val logger: Logger,
    val channel: String
)
{
    private val client by lazy {
        AwareHub.newClient()
    }

    fun
}
