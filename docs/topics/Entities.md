# Entities

Entities are Kord's representation of Discord objects like `Guild`, `Channel`, `Message`, `User`. 

Conceptually, an `Entity` consists of a state and behavior.

### State

Most entities you'll encounter will have a `data` property (you can find the implementations under `com.gitlab.kordlib.core.cache.data`), which represents the state of the entity at the time of creation. This data is **immutable** and **read-only**, so it's best to think of them as a 'snapshot' of the actual entity. Being immutable, your entity will eventually become outdated and stop reflecting reality. As such, it's not advised to keep a reference to entities for a long time.

You can find out more about how this data is provided to you in [Caching](Caching.md).

## Behavior

Aside from having a state, most entities are also able to do things. It's Kord philosphy to provide one place to do one type of action, finding that place can be summed up in the following rules.

* **Create** entitities from the **parent**. A `Guild` creates channels, a `Channel` creates messages, etc. If there's no logical parent (like creating a guild), the action will be available in the `Kord` class.
  
* **Edit** entities from the entity **itself**. A `Guild` can edit itself, a `Channel` can edit itself, etc.

* **Delete** entities from the entity **itself**. A `Guild` can delete itself, a `Channel` can delete itself, etc. Exceptions to this rule are bulk removals. A `Guild` can prune members, a `TextChannel` can bulk delete messages.


* **Get** entities from their **parent**. A `Guild` can give you its channels, a `Channel` can give you its messages. If there's no logical parent (like getting a guild or user), the action will be available in the `Kord` class.

## Behaviors

Seeing as there is only one place to do one thing, you might find yourself getting entities *just* to do something with them. This means that you're hitting cache (or worse, REST!) without actually needing the data. For this reason, Kord provides a 'Behavior-only' version of entities, which follows a `[Entity]Data` naming pattern.

A frequent example would be the `MessageChannelBehavior`, which provides the actions of a message channel without the state.
Imagine you're listening to a `!ping` message:
```kotlin
kord.on<MessageCreateEvent> {
    if(message.content == "!ping") return@on

    TODO("respond")
}
```
You want to respond with `pong`, but you can only create messages from a `MessageChannel`. In a sad world this means you would have to do the following:

```kotlin
kord.on<MessageCreatEvent> {
    if(message.content == "!ping") return@on

    //get a channel just to create a message, sad!
    message.getChannel().createMessage("pong!") 
}
```

`getChannel` Will fetch you the entire message channel, and you better hope it's cached because otherwise you'll be spending some of your precious ratelimit tokens on trying to get that channel just to send a message.

Luckily, `Message` knows the id of its parent channel and it can be sure that the parent channel is a message channel (how else did a message end up there). So Kord can provide you with a stateless version of that channel without risking any IO operations. These behaviors are provided as a property that match the naming of the full `get[Entity]` function. In this case, `message.getChannel()` also has a `message.channel`.


```kotlin
kord.on<MessageCreateEvent> {
    if(message.content == "!ping") return@on

    //get the behavior of a channel for free, happy!
    message.channel.createMessage("pong!")
}
```

> Behavior properties assume the entity exists. While it's highly unlikely the channel of a message has been deleted, it remains a possibility.
>
> Another thing to watch out for is that behaviors don't hide information about their type.
> `message.channel` will always return a `MessageChannelBehavior` and nothing more. While the actual channel might be a `TextChannel` or a `DMChannel` that won't be reflected in the behavior. As such, you should **not** downcast behaviors or try to match them in `when` expressions.

## Live Entities

> Live entities are still an experimental feature. There's no certainty that we'll keep them as they are, but we do want to offer the functionality they have right now in some shape or form.
> 
{style="warning"}

There's times where you want to listen to events that only happen to a certain entity. A popular example would be listening to reactions on a specific message. While you could just put a couple of filters on Kord's event flow, you would also have to match the lifecycle of that flow with the entity, lest you run into memory leaks.

We decided this use-case is sufficiently complex and common to implement it as a feature. Calling `live()` on an entity (that has events) will turn it into a `Live[Entity]` which comes with an `events` flow that's filtered for the entity.

```kotlin
val liveMessage = message.live()

liveMessage.on<ReactionAddEvent> {
    println("this will only report reactions added to the message")
}
```

### Which events are filtered for my entity

* Any event that's about the entity itself. These are **Delete**, **Create** and **Modify** events of the entity. In practice you won't get a create event of an entity that already exists, but you can never be too sure with Discord.

* Any event about its direct 'children'. For a text channel this would be its messages, for a message its reactions. etc. But you wouldn't be able to listen to filtered reaction events on a channel.  


### Automatic lifetime handling

If a life entity detects its own deletion (either directly or indirectly) it will stop emitting events and, if left unreferenced, become available for GC together with all attached event listeners.

### Mini cache

Since these entities listen to all related events, they are also able to keep an updated reference to the entities. This means you could also think of live entities as small, updating caches.
