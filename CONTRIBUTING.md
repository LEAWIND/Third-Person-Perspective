# Contributing

* Use UTF-8 encoding
* Use google-java-format

## Commit message format

These are some requirements of commit message:

* use lowercase
* no . at the end
* fit the format below

```
<type>: <title> [#issueId]

[description]

```

Example:

```
fix: too many downloads #999
```

### Type (required)

* feat: feature change
* fix: fix bug
* style: code format or comments change
* chore: build script change
* refactor: refactor code
* perf: improve performance or experience
* revert: revert commit

## How to add a new config item

Define config item in `AbstractConfig`

```java
public abstract class AbstractConfig {
  @Expose public double my_option = 0.5;
}

```

Use the config item somewhere.

Find all screen builders in `ConfigScreenBuilder#builders`, and add the config item to the builder.

Add translation to `resource/assets/minecraft/lang/*.json`
