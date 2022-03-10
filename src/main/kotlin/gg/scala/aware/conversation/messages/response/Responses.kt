package gg.scala.aware.conversation.messages.response

import gg.scala.aware.conversation.messages.response.responses.EmptyResponse
import java.util.UUID

/**
 * Proxy methods and/or
 * common responses.
 *
 * @author GrowlyX
 * @since 3/9/2022
 */
object Responses
{
    fun empty(uniqueId: UUID) = EmptyResponse(uniqueId)
}
