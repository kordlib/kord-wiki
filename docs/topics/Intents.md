# Intents
[Gateway Intents](https://discord.com/developers/docs/topics/gateway#gateway-intents) are Discord's way of allowing bots to communicate which events a bot wishes to receive through the gateway.

By default, Kord is set up to receive all non-privileged intents (`Intents.nonPrivileged`),
you can change this when creating a new instance:

```kotlin
Kord(token){
    intents = Intents(Intent.Guilds, Intent.DirectMessages, Intent.GuildMessages)
}
```

> Kord primarily caches data from the gateway, consider enabling intents not just based on the events you'll handle but also the data you'll need. 
>
> e.g.: the `Intent.Guilds` will help in keeping guild channels cached and up to date.

## Privileged Intents

[Privileged Intents](https://discord.com/developers/docs/topics/gateway#privileged-intents) are a special set of intents that need to be enabled through the developer's portal in addition to Kord's builder. 

Because of the extra steps (and restrictions) surrounding these intents, Kord requires you to opt into a special annotation when using those intents (or parts of the API that require them):
```kotlin
Kord(token) {
    @OptIn(PrivilegedIntent::class)
    intents = Intents.nonPrivileged + Intent.GuildPresences
}
```
