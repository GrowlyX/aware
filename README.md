# aware
Extensive annotation-based [Redis](https://redis.com/) Pub-Sub wrapper for [lettuce](https://lettuce.io) written in [Kotlin](https://kotlinlang.org/).
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
Aware has a conversation feature where A can contact B and await for a reply (which could possibly be empty).

### Conversation models:
Conversations require you to create your own implementation of ConversationMessage & ConversationMessageResponse.
 - These models can contain anything which is serializable by the [Gson](https://github.com/google/gson) instance provided in your AwareHub configuration.
 - The response model must contain the `uniqueId` of the origin message to allow it to be processed back on the original application.
 
### Channel naming:
Conversation factories require a channel suffix, which will then used for both outgoing and incoming channels.
 - **Outgoing:**
   - `og-${suffix}`
 - **Incoming:**
   - `ic-${suffix}`

How to create a new ConversationFactory:
```kotlin
val conversationFactory = ConversationFactoryBuilder
    .of<ConversationMessageImpl, ConversationResponseImpl>()
    // your channel suffix
    .channel("big-monkey")
    // your timeout, this is not optional.
    .timeout(2L, TimeUnit.SECONDS) {
        println("Lmao no response dam")
    }
    // what will be handled on the response of a message
    // the origin ConversationMessage is supplied as a lambda parameter
    .response {
        ConversationResponseImpl(
            "on god", it.uniqueId
        )
    }
    // what will be handled when our backend receives a response to a message
    .receive { message, response ->
        println("Original msg: ${message.message}")
        println("Response: ${response.message}")

        return@receive ConversationContinuation.END
    }
    .build()
```

## Usage:
An example annotation-based pub-sub subscription for an AwareMessage codec:
```kotlin
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
```kotlin
aware.listen(
    "test",
    ExpiresIn(30L, TimeUnit.SECONDS)
) {
    val horse = retrieve<String>("horse")

    println("Hey! This is the \"horse\". $horse")
}
```

An example AwareMessage use case:
```kotlin
AwareMessage.of(
    "test", aware,
    "horse" to "heyy-${Random.nextFloat()}"
).publish(
    // supplying our own thread context
    AwareThreadContext.SYNC
)
```

An example AwareHub configuration:
```kotlin
AwareHub.configure(
    // the default credentials would be localhost:6379, no password.
    WrappedAwareUri() 
) {
    gson
}
```

An example AwareBuilder configuration:
```kotlin
val aware = AwareBuilder
    .of<AwareMessage>("twitter.com/growlygg")
    // You can do this:
    .codec(AwareMessageCodec)
    // Or you can do this:
    .codec(JsonRedisCodec.of { it.packet })
    .build()
```

## Future plans:
 - None yet! Message me on Discord (growly#4953) if you have any suggestions!
 
## Other information:
lettuce-core is automatically shaded into the final shadowJar. [kotlin-stdlib](https://kotlinlang.org/api/latest/jvm/stdlib/) & kotlin-reflect are NOT.
 - Although aware has not been tested in a production environment, it has run perfectly fine under multiple tests.
   - _All of aware's features have now been testing._

## Note:
If you're using this in a **closed-source** project, please add `GrowlyX` to the project's author section.
