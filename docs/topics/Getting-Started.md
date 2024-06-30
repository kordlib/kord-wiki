# Getting Started

That's it! You have created a bot and put it in your discord server.

## Starting your bot with Kord

Go to your bot's application page from the previous section of this guide. There's a `Token` section with a 'Copy'
button. This'll put your bot's token in your clipboard.

![how do you like my art?](https://user-images.githubusercontent.com/18498008/85200373-2a9fbf80-b2f7-11ea-93c0-ba275a032855.png)


> Note:
>
> Never ever ever ever ever, share this code with anyone. Don't put it on github, don't put it in Discord. This is the
> equivalent of your bot's password. People can do *very* bad things with it.

Right now we assume you have basic knowledge of gradle or maven, you can find out how to add Kord to your project in
the `installation` section of the [README](https://github.com/kordlib/kord#installation).

The minimal code to get your bot online is the following:

```kotlin
suspend fun main() {
    val kord = Kord("your bot token")

    kord.login()
}
```

Run this code, and your bot will appear online in your client's sidebar.
`login` Will keep your bot logged in until you tell it to log out, or stop the program through some other means.

## Making a simple ping-pong bot

Copy the code above:

```kotlin
suspend fun main() {
    val kord = Kord("your bot token")

    kord.login()
}
```

Kord allows you to listen to
[events](https://github.com/kordlib/kord/tree/0.8.x/core/src/main/kotlin/event) either through the `events` property, or
the `on` extension function.

Since our requirements are pretty simple, we'll use the latter. We'll want to listen to a newly created message,
aka `MessageCreateEvent`:

```kotlin
```
{src="src/commonMain/kotlin/PingBot.kt" include-symbol="main"}

That's it, you now have a bot that'll respond pongs to your pings. With that, you have the very basics covered.

> `login` will suspend until the bot logs out, this stops the program from reaching the end of the main function and
> ending. It also means that whatever code you write after `login` will only run **after** your bot was logged out. If
> you
> follow this pattern, you should **add your event listeners before calling login**.


