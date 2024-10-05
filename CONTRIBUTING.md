# Contributing

## Basic

* Use UTF-8 encoding.
* Use LF line ending.
* Use .editorconfig to format code.

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

1. Define config item in `AbstractConfig`
   `@Expose public double my_option = 0.5;`
2. Use the config item somewhere.
3. Find all screen builders in `ConfigScreenBuilder#builders`, and add the config item to the builder.
4. Add translation to `resource/assets/minecraft/lang/*.json`
