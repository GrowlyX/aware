# aware
Extensive annotation-based Redis Pub-Sub wrapper for [lettuce-core](https://lettuce.io) written in Kotlin.
 - Aware was written to be a replacement for the very dated [Banana](https://github.com/growlyx/banana) library.
 - Aware allows for both asynchronous and synchronous contexts.
 - Aware contain wrappers for the [RedisCodec<K, V>](https://lettuce.io/core/release/api/io/lettuce/core/codec/RedisCodec.html).
   * These codec wrappers are only used for message values, therefore they only accept one type, V. (`WrappedRedisCodec<V>`)
   * We have multiple wrapper types available:
     - `StringRedisCodec`
     - `JsonRedisCodec<V>`
       * AwareMessageCodec *(A default implementation of JsonRedisCodec, for the AwareMessage)*
 - Aware uses a platform-level credential & serialization provider.
   * Aware currently ONLY supports [Gson](https://github.com/google/gson).
   * Aware credential & serialization providers can be configured by using `AwareHub#configure()`
 - Aware is an annotation-based.
   * More traditional solutions will be implemented in the future.

## Usage:
An example pub-sub subscription for an AwareMessage codec:
```kt
@Subscribe("test")
@ExpiresIn(30L, TimeUnit.SECONDS)
fun onTestExpiresIn30Seconds(
    message: AwareMessage
)
{
    val horse = message.retrieve<String>("horse")

    println("Hey! It's not been 30 seconds. :) ($horse)")
}
```

An example AwareMessage use case:
```kt
AwareMessage.of(
    "test", aware,
    "horse" to "heyy-${Random.nextFloat()}"
).publish(
    // supplying our own thread context
    AwareThreadContext.SYNC
)
```

An example AwareHub configuration:
```kt
AwareHub.configure(
    // the default credentials would be localhost:6379, no password.
    WrappedAwareUri() 
) {
    gson
}
```

An example AwareBuilder configuration:
```kt
val aware = AwareBuilder
    .of<AwareMessage>("channel")
    // You can do this:
    .codec(AwareMessageCodec)
    // Or you can do this:
    .codec(object : JsonRedisCodec<AwareMessage>(AwareMessage::class) {
        override fun getPacketId(v: AwareMessage) = v.packet
    })
    .build()
```
 
## Other information:
lettuce-core is automatically shaded into the final shadowJar. kotlin-stdlib & kotlin-reflect are NOT.

## Note:
If you're using this in a **closed-source** project, please add `GrowlyX` to the project's author section.
