package gg.scala.aware.subscription

import gg.scala.aware.annotation.Subscribe

/**
 * Provides context to the method
 * caller about the containing class.
 *
 * @author GrowlyX
 * @since 3/7/2022
 */
data class AwareSubscriptionContext<C>(
    val caller: Any,
    val context: C,
    val contextType: AwareSubscriptionContextType<C>,
    val subscription: Subscribe,
    val annotations: List<Annotation>
)
{
    /**
     * Retrieve annotations marked on
     * the method by its type.
     */
    inline fun <reified T> byType(): List<T>
    {
        return annotations
            .filter { it is T }
            .map { it as T }
    }
}
