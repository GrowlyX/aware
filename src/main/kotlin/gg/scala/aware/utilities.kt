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

fun KClass<*>.getTypes(): List<Class<*>>
{
    return this.java.getTypes()
}

fun Class<*>.getTypes(): List<Class<*>>
{
    return (this.genericSuperclass as ParameterizedType).actualTypeArguments
        .map {
            it as Class<*>
        }
        .toList()
}
