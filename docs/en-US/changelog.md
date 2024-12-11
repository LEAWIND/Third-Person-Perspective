# 🕗Changelog

:::info Next version may contains following contents.

<!--
## v (Preview)
### Changed
### Fixed
### Compatibility
### Other
-->

## (Preview)

### Changed

### Fixed

### Compatibility

### Other

:::


## v2.2.0-mc1.18.2

## v2.2.0-mc1.19-1.19.2

## v2.2.0-mc1.19.3

## v2.2.0-mc1.19.4

### Changed

* Port from v2.2.0-mc1.20-1.20.1

## v2.2.0-mc1.20-1.20.1

### Changed

* Add rotate mode: *Turn to Moving Direction*
* Reduce camera offset when looking up and down
* Update adjustable range of some config items

## v2.2.0-beta.4-mc1.20-1.20.1

### Fixed

* Issue url incorrect
* Errors in floating-point calculation (#173)

## v2.2.0-beta.3-mc1.20-1.20.1

### Added

-   Enum config option to specify how player entity normally rotate: `normal_rotate_mode`

    enum values:

    -   Interest point
    -   Camera crosshair
    -   Parallel with camera
    -   None

-   Allow disabling view bobbing in third person
    -   config option: `disable_third_person_bob_view`

### Removed

-   Replace multiple config options with one enum type option
    -   `player_rotate_to_interest_point`
    -   `player_rotate_with_camera_when_not_aiming`
    -   `rotate_to_moving_direction`

### Fixed

-   Fov mixin incorrect
-   Player entity rotation incorrect when enter third person

### Compatibility

-   Basically compatible with _Valkyrien Skies_ if you set option _Normal rotate mode_ to _Camera crosshair_

## v2.2.0-beta.2-mc1.20-1.20.1

### Added

-   Change fov when aiming (Dolly zoom)
    -   Config option: `aiming_fov_divisor`
-   Auto enter first person in narrow space
    -   Config option: `temp_first_person_in_narrow_space`
-   Adjustable vertical offset of rotate center
    -   Config option: `rotate_center_height_offset`

### Changed

-   Update default value of config options
-   Rename config category name: "Aiming Check" -> "Item Predicates"
-   Change some default values of config options

## v2.2.0-beta.1-mc1.20-1.20.1

### Added

-   Add config: `camera_distance_mode`
-   Allow item tag in item pattern
-   Make it always use post effect of attached entity in spectator mode
-   Disable double-tap sprinting in third person by default #153 #155
    -   config: `allow_double_tap_sprint`

### Changed

-   Support both 1.20 and 1.20.1
-   Config `use_camera_pick_in_creative`:
    -   Disabled by default
    -   Moved to category _Other_

### Removed

-   Remove config `sprint_impulse_threshold`

### Fixd

-   Camera slightly shakes when hitting wall

### Compatibility

### Other

-   Change package name to `com.github.leawind.thirdperson`
-   Record stack trace when infinite value detected. Once player's rotation become NaN or infinity, it will log some information for debugging

## v2.1.0-mc1.20.1

### Changed

-   Update link https://afdian.com/a/Leawind

### Fixed

-   Transition from third person to first person not smooth

## v2.1.0-beta.3-mc1.20.1

### Changed

-   Update default values and adjustable ranges for config items
-   Update translations
-   Improve riding experience
-   Calculate camera-to-player distance based on the total size of the player and vehicles #142
-   Make "camera distance" starts from certain distance to eye

### Removed

-   Unnecessary config item
-   Unnecessary camera distance limitation in normal mode

### Fixed

-   Sometimes player's head disappear #138

## v2.1.0-beta.2-mc1.20.1

### Fixed

-   Sometimes player's head disappeared #138

## v2.1.0-beta.1-mc1.20.1

### Added

-   Skip vanilla second person camera
    -   Add config: `skip_vanilla_second_person_camera`

### Fixed

-   No enough information output when player rotation become NaN
-   Crosshair is still rendered when mod is disabled

## v2.0.9-mc1.20.1

### Added

-   Add config: `hide_crosshair_when_flying`
-   Let player rotate to interest point, keep body not move
    -   Add config: `player_rotate_to_intrest_point`
-   Add config: `sprint_impulse_threshold`

### Changed

-   Update translation of config option
-   Remove YACL dependency declaration in forge `mods.toml`. But still support YACL 3.2.2 or below

### Fixed

-   Sneak speed insanely slow in first person #133
-   Player blinking when switch to first-person while moving
-   Can't rotate camera with _Controllable_ #34
-   Can't toggle perspective with _Controllable_ #34
-   _AutoThirdPerson_ can't toggle perspective
-   Unexpected sprinting when walking
-   Mouse lag when adjusting camera offset

## v2.0.9-beta.1-mc1.20.1

### Added

-   Add config: `gaze_opacity`

### Changed

-   Update fabric loader version: `0.15.7` --> `0.15.11`
-   Update fabric api version: `0.92.0` --> `0.92.2`
-   Update forge version: `47.2.20` --> `47.3.0`
-   Change config screen categories

### Fixed

-   Crash when config file is broken #128
-   Player head rotate unexpectedly with mouse #106
-   Camera toggle to another side when leaving center position #120

### Other

-   Use Forge API to set camera position and rotation
-   Use architectury api to register config screen
-   Add optional dependency YACL to mods.toml (forge). Supported version is `(,3.2.2+1.20]`
-   Use architectury api to check if mod exist
-   Ignore dir `.vs/`

## v2.0.8-mc1.18.2

-   Port from `v2.0.8-mc1.20.1`
-   Remove dependency YetAnotherConfigLib

## v2.0.8-mc1.20.1

### Added

-   Add config: `player_invisible_threshold`

### Changed

-   Player fade out is disabled by default

### Fixed

-   Unexpected rotate when using spyglass in first person #78
-   Crash when glowing #80
-   Splash potions don't activate aiming camera #105
-   Unexpected rotate when entering third person view #96
-   Game freeze when entering world (forge)

## v2.0.8-beta.5-mc1.20.4

-   Port from v2.0.8-beta.5-mc1.20.1

Thanks to [ArctynFox](https://github.com/ArctynFox) for [doing this port](https://github.com/Leawind/Third-Person/pull/88)!

## v2.0.8-beta.5-mc1.20.1

### Added

-   Do not rotate when eating
    -   add config: `do_not_rotate_when_eating`
-   Determine aiming mode by animation (bow or spear) #69
    -   Add config: `determine_aim_mode_by_animation`
-   Add category camera_offset in YACL config screen

### Changed

-   Render in vanilla way when camera entity opacity is close to 1.0
-   When switching to third person, camera start from eye position
-   No transition when enter temporary first person

### Removed

-   Remove config `turn_with_camera_when_enter_first_person`

### Fixed

-   Numbers are not allowed in item patterns #67
-   The default namespace of item patterns is always `minecraft` #68

## v2.0.8-beta.4-mc1.20.1

### Fixed

-   Mod is shown as resource pack in forge version. And the resource pack is disabled by default #56 #52
-   Strange behavior in Spectator Mode #60
-   Cannot pick in survival mod #64
-   Partial tick isn't updated in preRender

### Other

-   Update mod source URL in `fabric.mod.json`

## v2.0.8-beta.3-mc1.20.1

### Fixed

-   Compatible with _Better Combat_ and _First Person Model_ #50
-   Wrong translation key in YACL config screen
-   Camera forcing first person in tall grass #54
-   In third person, water is placed where player is looking at, rather than crosshair
-   Unexpected rotating when starting to use elytra in first person

### Other

-   Update translations
-   build script: add yacl dependency in fabric publishing script

## v2.0.8-beta.2-mc1.20.1

### Added

-   add config: `t2f_transition_halflife`

### Fixed

-   Mouse sensitivity doubled in first person #49
-   MixinExtras not inited
-   Picking error in forge version

## v2.0.8-beta.1-mc1.20.1

Port from 2.0.7-mc1.19.4

### Removed

-   Remove YACL support for forge version due to some problems with YACL

### Compatibility

-   With _Do a Barrel Roll_

### Fixed

-   Key `force_aiming`, `toggle_aiming` not working

## v2.0.7-mc1.19.4

Port from 2.0.7-mc1.19.3

## v2.0.7-mc1.19.3

Port from 2.0.7-mc1.19.2

## v2.0.7-mc1.19.2

### Added

-   Shoot enemies like in first person
    -   It can predict the target entity you want to shoot, even if the target is far away. Just aim at the target as if you were in first person
    -   Added config option: `enable_target_entity_predict`
-   Smooth transition when switching from third person to first person
-   Cloth Config API is now an optional dependency

### Changed

-   The smooth factors in the configuration has been changed to smoothing half-life for better intuition
-   When updating smooth eye position, multiply by the distance between it and the camera. This ensures a more consistent smoothing effect at different camera distances
-   Temporarily switch to first person when the camera entity is inside a wall

### Compatibility

-   Compatible with mod _do_a_barrel_roll_ (only Fabric version)

## v2.0.6-mc1.19.2

### Added

-   When the camera approaches the player, make player semi-transparent
-   Save config when client stopping
-   Enable third person mode by default
-   Save third person mode in config
    -   Add config: `is_third_person_mode`
-   Temporarily switch to first person when using specific items (spyglass for vanilla)
    -   Add config: `use_to_first_person_pattern_expressions`

### Changed

-   Change resource pack structure
    -   `assets/<namespace>/item_patterns/hold_to_aim/<anyname>.json` Enable aim mode when holding these items
    -   `assets/<namespace>/item_patterns/use_to_aim/<anyname>.json` Enable aim mode when using these items
    -   `assets/<namespace>/item_patterns/use_to_first_person/<anyname>.json` Temporarily switch to first person when using these items

### Fixed

-   Camera through glasses
-   Game crashing #44

## v2.0.5-mc1.19.2

### Added

-   Load ItemPatterns from resource pack
-   In Creative mode, you can directly pick blocks from the crosshair, even if player entity's sight is obstructed
    -   Add config: `use_camera_pick_in_creative`

### Changed

-   Rename config options
    -   `holding_item_aiming_rules` to `hold_to_aim_item_pattern_expressions`
    -   `using_item_aiming_rules` to `use_to_aim_item_pattern_expressions`

### Removed

-   Remove config: `enable_buildin_item_aiming_rules`

### Fixed

-   Unable quickly turn when sprinting
-   Unable to join world in forge version #38

## v2.0.4-mc1.19.2

### Added

-   Configurable aiming mode checking
    -   add boolean config: Enable Build-in match rules
    -   add string list config: Hand Item Match Rules
    -   add string list config: Using Item Matching Rules

### Fixed

-   Player's unsmooth rotation
-   In config screen, flying_smooth_factor option is in wrong tab

### Other

-   Unable to join world in forge version

## v2.0.3-mc1.19.2

### Added

-   Add Cloth-Config-API support for Forge version
-   Auto rotate body when draw a bow
-   Add config: auto_turn_body_drawing_a_bow
-   Add key: toggle_pitch_lock

### Fixed

-   Can't pick entity in certain case
-   Config is saved on dedicated server
-   Config option lost in screen: render_crosshair_when_aiming

## v2.0.2-mc1.19.2

Port to 1.19.2

### Added

-   Add config: normal/aiming_is_centered
-   Now use Cloth Config API to build config screen for fabric version

### Other

-   No config screen for forge version by now, but you can modify config file manually. When trying to open config screen, config file will be reloaded

## v2.0.1 (1.19.4)

### Added

-   Add config: Centered when flying
-   Auto switch to aiming mode when holding some items
    -   ender_pearl
    -   snowball
    -   egg
    -   splash_potion
    -   lingering_potion
    -   experience_bottle

### Fixed

-   Wrong smooth factor when adjusting camera
-   Can only swim forward
-   Flashes when camera is close to player

## v2.0.1-beta.4-mc1.19.4

### Added

-   Add config: Enable auto rotate when interecting #4
-   Add config: How to rotate when interecting #26

### Fixed

-   Camera is still moving when game paused

## v2.0.1-beta.3-mc1.19.4

### Added

-   Add key: toggle mod enable
-   Add config: Turn player with to camera when enter FP
-   Add config: smooth factors about adjusting camera
-   Now you can set smooth factor in range of [0, 1]

### Fixed

-   Camera choppiness when moving (again)
-   swimming to directions other than forward

## v2.0.1-beta.2-mc1.19.4

### Added

-   add config: lock pitch angle of camera
-   Let player invible when camera close enough to player

### Fixed

-   camera choppiness when moving

## v2.0.0-mc1.19.4

### Added

-   Use [Architectury](https://github.com/architectury/architectury-api)
-   Use [YACL](https://github.com/isXander/YetAnotherConfigLib) to generate config screen
-   Add [Modmenu](https://github.com/TerraformersMC/ModMenu) support for fabric version
-   Add key binding to open config screen
-   Ignore tall grass similar blocks when aiming
-   Use dedicated smooth factor when flying with elytra
-   Add config: `rotate_to_moving_direction` #22
-   Add config: `render_crosshair_when_aiming`
-   Add config: `render_crosshair_when_not_aiming`

### Fixed

-   Can only run forward #20
-   Camera go through wall
