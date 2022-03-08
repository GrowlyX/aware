package gg.scala.aware.annotation

/**
 * @author GrowlyX
 * @since 3/7/2022
 */
@Repeatable
@Target(AnnotationTarget.FUNCTION)
annotation class Subscribe(
    val value: String
)
