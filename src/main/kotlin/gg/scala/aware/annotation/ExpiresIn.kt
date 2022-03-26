package gg.scala.aware.annotation

import java.util.concurrent.TimeUnit

/**
 * Provides context of expiration
 * time for a targeted subscription.
 *
 * Subscriptions expire [duration]
 * [timeUnit]s after it the client has connected.
 *
 * @author GrowlyX
 * @since 3/7/2022
 */
@Target(AnnotationTarget.FUNCTION)
annotation class ExpiresIn(
    val duration: Long,
    val timeUnit: TimeUnit
)
