package gg.scala.aware.conversation.messages.response.responses

import gg.scala.aware.conversation.messages.ConversationMessageResponse
import java.util.UUID

/**
 * @author GrowlyX
 * @since 3/9/2022
 */
class EmptyResponse(
    uniqueId: UUID
) : ConversationMessageResponse(uniqueId)
