# aware
Extensive annotation-based Redis Pub-Sub wrapper for [lettuce](https://lettuce.io) written in Kotlin.
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

## Conversations:
Aware has a W.I.P. conversation feature, where A can contact B and await for a reply (which could possibly be empty).

## Usage:
An example annotation-based pub-sub subscription for an AwareMessage codec:
```kt
@Subscribe("test")
@ExpiresIn(30L, TimeUnit.SECONDS)
fun onTestExpiresIn30Seconds(
    message: AwareMessage
)
{
    val horse = message.retrieve<String>("horse")

    println("Hey! This is the \"horse\". ($horse)")
}
```

And a lambda-based one:
```kt
aware.listen(
    "test",
    ExpiresIn(30L, TimeUnit.SECONDS)
) {
    val horse = retrieve<String>("horse")

    println("Hey! This is the \"horse\". $horse")
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
// our encryption codec
val encryption = AwareEncryptionCodec.of(
    AwareMessageCodec, Base64EncryptionProvider
)
    
val aware = AwareBuilder
    .of<AwareMessage>("twitter.com/growlygg")
    // You can do this:
    .codec(AwareMessageCodec)
    // Or you can do this:
    .codec(JsonRedisCodec.of { it.packet })
    // encryption? sure!
    .codec(encryption)
    .build()
```

## Future plans:
 - Allow for upstream message encryption, after the provided `WrappedRedisCodec<V>` does its thing.
 - Allow for conversation channels (A -> B, B -> A) (Both sides are notified of the opposite response)
 - Allow for traditional, lambda based subscriptions.
 
## Other information:
lettuce-core is automatically shaded into the final shadowJar. kotlin-stdlib & kotlin-reflect are NOT.
 - Although aware has not been tested in a production enviornment, it has ran perfectly fine under multiple tests.

## Note:
If you're using this in a **closed-source** project, please add `GrowlyX` to the project's author section.
