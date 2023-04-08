# Logging
Kord uses [SLF4J](http://www.slf4j.org/) as its logging framework, which requires an implementation supplied by the user to work. If you get the following warning in your console, you don't have an implementation in your runtime.

```
SLF4J: No SLF4J providers were found.
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See https://www.slf4j.org/codes.html#noProviders for further details.
```

## Adding an implementation

There are many implementations of SLF4J, in an effort to keep this straightforward we'll recommend `slf4j-simple` to get logging working:

[![slf4j-simple version](https://img.shields.io/maven-central/v/org.slf4j/slf4j-simple.svg?label=slf4j-simple&style=for-the-badge)](https://www.slf4j.org/download.html)

```kotlin
repository {
    mavenCentral()
}
dependencies {
    implementation("org.slf4j:slf4j-simple:${version}")
}
```

## Providing a configuration

Next, under your `main` directory, create a directory `resources` with the file `simplelogger.properties`. The following is a [template from slf4j-simple](https://github.com/qos-ch/slf4j/blob/master/slf4j-simple/src/test/resources/simplelogger.properties) with all supported properties commented, remove the `#` prefix to uncomment a line:

<!--TODO: specify language type once Writerside supports properties highlighting-->
```
# SLF4J's SimpleLogger configuration file
# Simple implementation of Logger that sends all enabled log messages, for all defined loggers, to System.err.

# Default logging detail level for all instances of SimpleLogger.
# Must be one of ("trace", "debug", "info", "warn", or "error").
# If not specified, defaults to "info".
#org.slf4j.simpleLogger.defaultLogLevel=info

# Logging detail level for a SimpleLogger instance named "xxxxx".
# Must be one of ("trace", "debug", "info", "warn", or "error").
# If not specified, the default logging detail level is used.
#org.slf4j.simpleLogger.log.xxxxx=

# Set to true if you want the current date and time to be included in output messages.
# Default is false, and will output the number of milliseconds elapsed since startup.
#org.slf4j.simpleLogger.showDateTime=false

# The date and time format to be used in the output messages.
# The pattern describing the date and time format is the same that is used in java.text.SimpleDateFormat.
# If the format is not specified or is invalid, the default format is used.
# The default format is yyyy-MM-dd HH:mm:ss:SSS Z.
#org.slf4j.simpleLogger.dateTimeFormat=yyyy-MM-dd HH:mm:ss:SSS Z

# Set to true if you want to output the current thread name.
# Defaults to true.
#org.slf4j.simpleLogger.showThreadName=true

# Set to true if you want the Logger instance name to be included in output messages.
# Defaults to true.
#org.slf4j.simpleLogger.showLogName=true

# Set to true if you want the last component of the name to be included in output messages.
# Defaults to false.
#org.slf4j.simpleLogger.showShortLogName=false
```

All values are optional, but we recommend at least setting `org.slf4j.simpleLogger.defaultLogLevel=error` to get stacktraces or `org.slf4j.simpleLogger.defaultLogLevel=trace` to get a *very* detailed log of what exactly is happening.
