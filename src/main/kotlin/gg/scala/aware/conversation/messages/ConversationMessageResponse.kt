package gg.scala.aware.conversation.messages

import java.util.*

/**
 * Similar to the origin message,
 * but used for replies.
 *
 * @author GrowlyX
 * @since 3/9/2022
 */
abstract class ConversationMessageResponse(
    val uniqueId: UUID
)
{
    internal lateinit var callContext: UUID
}
