# Contributing

* Use google-java-format

## How to add a new config item

Define config item in class `AbstractConfig`

```java
public abstract class AbstractConfig {
  @Expose public double my_option = 0.5;
}

```

Use the config item somewhere.

Find all screen builders in `ConfigScreenBuilder#builders`, and add the config item to the builder.

Add translation to `resource/assets/minecraft/lang/*.json`
