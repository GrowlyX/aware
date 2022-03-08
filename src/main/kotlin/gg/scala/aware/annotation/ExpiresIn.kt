package gg.scala.aware.annotation

import java.util.concurrent.TimeUnit

/**
 * @author GrowlyX
 * @since 3/7/2022
 */
@Target(AnnotationTarget.FUNCTION)
annotation class ExpiresIn(
    val duration: Long,
    val timeUnit: TimeUnit
)
