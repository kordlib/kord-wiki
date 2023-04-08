## Application Commands
In discord's words:
Application commands are commands that an application can register to Discord. They provide users a first-class way of interacting directly with your application that feels deeply integrated into Discord.

Setting up an application command is of two stages *registering* the command and *listening* to it's events

### Chat Inputs (Slash Commands)

are a replacement for the text-based commands we used in bots pre-V8; However, the Chat Inputs have a reach interface and interaction system that makes it easier for the user to search for and use the registered commands.

![](https://imgur.com/cc2efaO.png)
![](https://imgur.com/aC6uazj.png)

#### Registering {id="register-chat-input"}

To register a commmand, you may use `createGuildChatInputCommand` which registers a command only on a given guild (good for premium features)
or `createGlobalChatInputCommand` to register a command on all guilds the bot have the `application.commands` scope on.

**Code**

```kotlin
  val kord = Kord(System.getenv("TOKEN"))
  kord.createGuildChatInputCommand(
      Snowflake(556525343595298817),
      "sum",
      "A slash command that sums two numbers"
  ) {
      int("first_number", "The first operand") {
          required = true
      }
      int("second_number", "The second operand") {
          required = true
      }
  }
```
You may also give choices for a given command using the `choice` function in the DSL Builder which is a set of white listed values for a given arguement.

![](https://imgur.com/FTwHNP3.png)

**Code**
```kotlin
        val kord = Kord(System.getenv("TOKEN"))
        kord.createGuildChatInputCommand(
            Snowflake(556525343595298817),
            "sum",
            "A slash command that sums two numbers"
        ) {
            int("first_number", "The first operand") {
                required = true
                for(i in 0L..9L) {
                    choice("$i", i)
                }
            }
            int("second_number", "The second operand") {
                required = true
                for(i in 0L..9L) {
                    choice("$i", i)
                }
            }
        }
```
if you have multiple commands that maybe categoried, make use of subCommand and group functions to create group and subCommands
### Listening  {id="Listening-chat-input"}

Now that we have our command setup; we should listen to it to give it some functionality.

using the `on` function in Kord, you may listen to various events that give you different contexts:

* `ChatInputCommandInteractionCreateEvent` - occurred in either a DM or a Guild
* `GuildChatInputCommandInteractionCreateEvent` - occurred in Guilds
* `GlobalChatInputCommandInteractionCreateEvent` - occurred in DMs (Direct Message)

since our command is registered only on our guild, it's better to listen to the Guild variant to have as much context as possible.
```kotlin
 kord.on<GuildChatInputCommandInteractionCreateEvent> {
            val response = interaction.deferPublicMessage()
            val command = interaction.command
            val first = command.integers["first_number"]!! // it's required so it's never null
            val second = command.integers["second_number"]!! 
            response.edit { content = "$first + $second = ${first + second}" }
        }
```
 please check [Acknowledging An Interaction](https://github.com/kordlib/kord/wiki/Interactions#acknowledging-an-interaction)
* Kord provides type-safe Maps that utilize the id as shown above in the example.


## User and Message Commands

User and Message commands are application commands that appear on the context menu (right click or tap) of users/messages. They're a great way to surface quick actions for your app that target users/messages. They don't take any arguments, and will return the user/message on whom you clicked or tapped in the interaction response.

![](https://imgur.com/jGzPZ7A.gif)

### Registering  {id="register-context-command"}

Registering a User/Message command is quite straight forward like Chat Input, it has guild and global scoped registration

```kotlin
kord.createGuildUserCommand(guildId, "show id")
kord.createGuildMessageCommand(guildId, "show id")
```       

### Listening {id="register-context-commands"}

Listening to a User/Message command is quite straight forward as well
```kotlin

kord.on<GuildUserCommandInteractionCreateEvent> {
            val response = interaction.deferPublicMessage()
            val user = interaction.target
            response.edit { content = "${user.id}" }
        }
kord.on<GuildMessageCommandInteractionCreateEvent> {
            val response = interaction.deferPublicMessage()
            val message = interaction.target
            response.edit { content = "${message.id}" }
        }
```
# Components
## Buttons
## Menus
# Modals
# Acknowledging an Interaction

```kotlin
 kord.on<InteractionCreateEvent> {
            val response = interaction.deferPublicMessage()
            //...

        }
```
before we do anything with our code, we must defer the interaction to tell Discord we are going to respond to this interaciton, else the interaction would fail (we have a window of 3 seconds at the time of writing these docs ).

there two types of defers:
- Public: Visible to everyone

![](https://imgur.com/j1wjPA2.gif)

- Ephemeral: Visible only to the command user.

![](https://imgur.com/SJkTsOR.gif)
