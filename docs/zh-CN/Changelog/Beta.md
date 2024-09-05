# 测试

## v2.0.8-beta.5-mc1.20.4

-   移植自 1.20.1

感谢 [ArctynFox](https://github.com/ArctynFox) [将它移植到1.20.4](https://github.com/Leawind/Third-Person/pull/88)！

## v2.0.8-beta.5-mc1.20.1

### 特性

-   进食时不旋转。
    -   添加配置项：`do_not_rotate_when_eating`。
-   当相机实体的不透明度接近 1.0 时，以原版方式渲染。
-   根据动画（弓箭或长矛）确定瞄准模式 #69
    -   添加配置项：`determine_aim_mode_by_animation`。
-   在 YACL 配置屏幕中添加了一个类别：相机偏移。
-   移除配置项 `turn_with_camera_when_enter_first_person`。
-   切换到第三人称时，相机从眼睛位置开始过渡。
-   临时进入第一人称时不进行过渡。

### Bug修复

-   物品模式中无法使用数字。 #67
-   物品模式的默认命名空间始终为 `minecraft`。 #68

### 其他

-   在渲染刻前检查过渡状态。e45c556
-   重新格式化代码，消除一些警告。
-   更新 .editorconfig。
-   更新构建脚本。
-   当实体旋转为 NaN 时记录错误。这有助于调试。
-   优化物品模式匹配。

## v2.0.8-beta.4-mc1.20.1

### Bug修复

-   Forge版本中，此模组内置资源包会单独显示，而且默认不启用。 #56 #52
-   观察者模式行为异常 #60
-   生存模式下无法选取 #64
-   在渲染刻开始时，没有更新 partial tick

### 其他

-   添加问题模板
-   更新 CONTRIBUTING.md
-   更新 `fabric.mod.json` 中的源码链接
-   优化

## v2.0.8-beta.3-mc1.20.1

### 特性

-   使用 `ModifyExpressionValue` 替代 `Redirect`。这将有更好的兼容性。

### 问题修复

-   兼容 _Better Combat_ 和 _First Person Model_ #50
-   YACL 配置屏幕中的本地化键错误
-   在高草丛中强制第一人称 #54
-   在第三人称中，水被放置在玩家所看的位置，而不是准星的位置。

### 其他

-   更新翻译
-   构建脚本：在 fabric 发布脚本中添加 YACL 依赖。
-   修复：第一人称使用鞘翅时意外旋转的问题。
-   添加注释。
-   更新 README
-   优化代码

## v2.0.8-beta.2-mc1.20.1

### 特性

-   添加配置：`t2f_transition_halflife`

### Bug修复

-   修复：第一人称中有双倍鼠标灵敏度 #49
-   修复：MixinExtras 没有初始化
-   Forge 版本中选取方块错误

### 其他

-   优化代码

## v2.0.8-beta.1-mc1.20.1

移植自 2.0.7-mc1.19.4

### 特性

-   使用 MixinExtras。
    -   用`@WrapWithCondition`替换`@Redirect`，这应该能解决与*Do a Barrel Roll*的冲突。
-   由于YACL存在的一些问题，删除了Forge版本对YACL的支持。

### Bug修复

-   按键 `force_aiming`，`toggle_aiming` 失效

### 其他

-   将 `changelog_latest.txt` 更新为 `changelog_latest.md`
-   在YACL中更新了弃用的方法：`valueFormatter` -> `formatValue`
-   更新构建脚本
-   添加调试日志

## v2.0.1-beta.4-mc1.19.4

### 特性

-   添加配置：交互时自动转身 #4
-   添加配置：交互时转身方式 #26

### 修复bug

-   游戏暂停时相机仍在移动

## v2.0.1-beta.3-mc1.19.4

### 特性

-   添加按键：切换模组是否启用
-   添加配置: 进入第一人称时玩家是否转向相机朝向
-   添加配置: 调节相机时的平滑系数
-   现在可以在 [0,1] 范围内调整平滑系数了

### 修复bug

-   相机颤动（再次修复）

### 藏匿bug

-   游泳时仅朝前方

## v2.0.1-beta.2-mc1.19.4

### 特性

-   添加配置: 锁定相机俯仰角
-   当相机离玩家足够近时，让玩家变得不可见

### 修复bug

-   玩家移动时相机颤动
