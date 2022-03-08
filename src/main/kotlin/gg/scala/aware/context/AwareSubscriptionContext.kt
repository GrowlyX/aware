package gg.scala.aware.context

import java.lang.reflect.Method

/**
 * Provides context to the method
 * caller about the containing class.
 *
 * @author GrowlyX
 * @since 3/7/2022
 */
data class AwareSubscriptionContext(
    val instance: Any,
    val method: Method,
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
