package gg.scala.aware.conversation.messages

import java.util.UUID

/**
 * The message sent from
 * the origin application.
 *
 * @author GrowlyX
 * @since 3/9/2022
 */
abstract class ConversationMessage
{
    val uniqueId: UUID = UUID.randomUUID()

    internal lateinit var callContext: UUID
}
