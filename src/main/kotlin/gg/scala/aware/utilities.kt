package gg.scala.aware

/**
 * @author devrawr
 */
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass

fun Any.getTypes(): List<Class<*>>
{
    return this::class.getTypes()
}

inline fun <reified T> Any.hasTypeOf(
    index: Int = -1
): Boolean
{
    return this::class.hasTypeOf<T>(index)
}

fun KClass<*>.getTypes(): List<Class<*>>
{
    return this.java.getTypes()
}

inline fun <reified T> KClass<*>.hasTypeOf(
    index: Int = -1
): Boolean
{
    return this.java.hasTypeOf<T>(index)
}

fun Class<*>.getTypes(): List<Class<*>>
{
    return (this.genericSuperclass as ParameterizedType).actualTypeArguments
        .map {
            it as Class<*>
        }
        .toList()
}