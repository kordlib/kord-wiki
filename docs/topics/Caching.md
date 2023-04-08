# Caching

Kord comes with a cache that allows your bot to store `Entity` data to avoid using REST when retrieving entities. The implementation is based on our (originally named) [Cache](https://github.com/kordlib/cache) project. The default implementation uses a ConcurrentHashMap on the JVM to store data.

The classes that are stored in cache can be found in `com.gitlab.kordlib.core.cache.data.*`. They are serializable with kotlinx.serialization if you want to use a serialized cache like Redis instead.

The following entities are available for caching:

* messages (disabled by default)
* roles
* channels
* guilds
* members
* users
* emojis (guild emojis only)
* webhooks
* presences
* voicestates

> Messages generally have an infinite lifetime, that is to say they are mostly created and never deleted. This combined with them being the fastest growing pool of entities you'll encounter made us decide that **messages will not be cached by default**. Unless configured otherwise, retrieving a message through suspended functions will never use cached data.
>
{style="warning"}

## Configuring the cache

You can configure the cache by calling `cache { ... }` in the `KordBuilder` when creating your Kord instance:

```kotlin
val kord = Kord("token"){
    cache {

    }
}
```

Kord keeps a separate cache per entity so that you can configure the policy for each one individually:

```kotlin
val kord = Kord("token"){
    cache {
        users { cache, description -> 
            MapEntryCache(cache, description, MapLikeCollection.concurrentHashMap()) 
        }
        
        messages { cache, description -> 
            MapEntryCache(cache, description, MapLikeCollection.lruLinkedHashMap(maxSize = 100)) 
        }
        
        members { cache, description -> 
            MapEntryCache(cache, description, MapLikeCollection.none()) 
        }
    }
}
```

> You can register non-Kord entities using `forDescription`, but their lifetime will not be managed by Kord.
> 
{style="note"}

## When are entities cached

Kord only caches entities from `Gateway` event, event entities will be inserted into, updated or removed from cache before any core events fire.

### Why not cache from REST

Our biggest fear when caching is holding on to data long after it should have been expired. Users can retrieve entities through REST that may not share the same shard, meaning that the gateway will not be informed of updates to or removals of the entity.

Additionally, most REST actions will trigger a `Gateway` event with similar information. 

As such, we have decided to only use the Gateway for our cache. 
This makes reasoning about the lifetime of a entity rather simple: What is created by the `Gateway` will, eventually, be removed by the `Gateway`.
