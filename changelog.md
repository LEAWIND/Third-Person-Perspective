### Added

* Enum config option to specify how player entity normally rotate: `normal_rotate_mode`

  enum values:
	* Interest point
	* Camera crosshair
	* Parallel with camera
	* None
* Allow disabling view bobbing in third person
	* config option: `disable_third_person_bob_view`

### Changed

### Removed

* Replace multiple config options with one enum type option
	* `player_rotate_to_interest_point`
	* `player_rotate_with_camera_when_not_aiming`
	* `rotate_to_moving_direction`

### Fixed

* Fov mixin incorrect
* Player entity rotation incorrect when enter third person

### Compatibility

* Basically compatible with _Valkyrien Skies_ if you set option _Normal rotate mode_ to _Camera crosshair_

### Other
