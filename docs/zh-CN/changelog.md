# 🕗更新日志

:::info 下一个版本可能包含以下内容

<!--
## v （预览）
### 变化
### 修复
### 兼容性
### 其他
-->

## （预览）

### 变化

### 修复

### 兼容性

### 其他

:::

## v2.2.0-mc1.18.2

## v2.2.0-mc1.19-1.19.2

## v2.2.0-mc1.19.3

## v2.2.0-mc1.19.4

### 更改内容

* 移植自 v2.2.0-mc1.20-1.20.1

## v2.2.0-mc1.20-1.20.1

### 变化

* 添加旋转模式：*转向移动方向*
* 抬头和低头时减小相机偏移量
* 更新了一些配置项的可调范围

## v2.2.0-beta.4-mc1.20-1.20.1

### 修复

* 问题 URL 不正确
* 浮点计算中的错误 (#173)

## v2.2.0-beta.3-mc1.20-1.20.1

### 新增

-   枚举类型的配置选项，指定玩家实体通常如何旋转：`normal_rotate_mode`

    枚举值：

    -   兴趣点
    -   相机准星
    -   与相机平行
    -   无

-   允许在第三人称中禁用视角摇晃
    -   配置选项：`disable_third_person_bob_view`

### 移除

-   以一个枚举类型选项替换了多个选项
    -   `player_rotate_to_interest_point`
    -   `player_rotate_with_camera_when_not_aiming`
    -   `rotate_to_moving_direction`

### 修复

-   视野 mixin 错误
-   玩家实体在进入第三人称时旋转错误

### 兼容性

-   勉强兼容 _Valkyrien Skies_，只要将选项“普通旋转模式”设置为“相机准星”

## v2.2.0-beta.2-mc1.20-1.20.1

### 新增

-   在瞄准时改变视野（滑动变焦）
    -   配置选项：`aiming_fov_divisor`
-   在狭窄空间内自动进入第一人称
    -   配置选项：`temp_first_person_in_narrow_space`
-   可调节的旋转中心垂直偏移量
    -   配置选项：`rotate_center_height_offset`

### 修改

-   更新配置选项的默认值
-   重命名配置类别名称：“瞄准检查” -> “物品谓词”
-   更改某些配置选项的默认值

## v2.2.0-beta.1-mc1.20-1.20.1

### 新增

-   添加配置：`camera_distance_mode`
-   物品模式中支持物品标签
-   旁观者模式下总是使用被附着实体的后处理效果
-   第三人称下禁用双击疾跑 #153 #155
    -   添加配置 `allow_double_tap_sprint`

### 变化

-   支持 1.20 和 1.20.1
-   配置 `use_camera_pick_in_creative`：
    -   默认禁用
    -   移动到“其他”类别

### 移除

-   配置 `sprint_impulse_threshold`

### 修复

-   相机触墙时轻微抖动

### 兼容性

### 其他

-   修改包名为 `com.github.leawind.thirdperson`
-   当探测到非有限值时记录调用栈。一旦玩家朝向变成 NaN 或无限，将会在日志中记录一些信息以便调试

## v2.1.0-mc1.20.1

### 变化

-   更新爱发电链接 https://afdian.com/a/Leawind

### 修复

-   从第三人称到第一人称过渡不平滑

## v2.1.0-beta.3-mc1.20.1

### 变化

-   更新配置项的默认值和调节范围
-   更新翻译
-   提升骑行体验
-   根据玩家与载具的总体积计算相机到玩家的距离 #142
-   让“相机距离”从到眼睛的特定距离开始计算

### 移除

-   不必要的配置项
-   普通模式下不必要的相机距离限制

### 修复

-   玩家头部有时会消失 #138

## v2.1.0-beta.2-mc1.20.1

### 修复

-   玩家头部有时会消失 #138

## v2.1.0-beta.1-mc1.20.1

### 新增

-   跳过原版第二人称视角
    -   添加配置：`skip_vanilla_second_person_camera`

### 修复

-   玩家角度为 NaN 时没有输出足够信息
-   模组禁用时仍然显示准星

## v2.0.9-mc1.20.1

### 新增

-   添加配置项：`hide_crosshair_when_flying`
-   让玩家转向兴趣点，保持身体不动
    -   添加配置项：`player_rotate_to_intrest_point`
-   添加配置项：`sprint_impulse_threshold`

### 变化

-   更新了配置项的翻译
-   移除了 forge `mods.toml` 中 YACL 的依赖声明，但仍然支持 YACL 的 3.2.2 或以下版本

### 修复

-   第一人称潜行惊人的慢 #133
-   移动时切换到第一人称，玩家会闪烁
-   使用 _Controllable_ 时，无法用手柄转动相机 #34
-   使用 _Controllable_ 时，无法切换视角 #34
-   _AutoThirdPerson_ 无法自动切换视角
-   行走时意外开始疾行
-   调整相机偏移量时鼠标卡顿

## v2.0.9-beta.1-mc1.20.1

### 新增

-   添加配置：`gaze_opacity`

### 变化

-   更新 fabric 加载器版本： `0.15.7` --> `0.15.11`
-   更新 fabric api 版本： `0.92.0` --> `0.92.2`
-   更新 forge 版本： `47.2.20` --> `47.3.0`
-   修改配置屏幕类别

### 修复

-   当配置文件损坏时崩溃 #128
-   玩家头像在鼠标移动时异常旋转 #106
-   离开居中模式时相机切换到另一边 #120

### 其他

-   使用 Forge API 来设置相机位置和朝向
-   使用 architectury api 来注册配置屏幕
-   添加可选依赖 YACL 到 mods.toml (forge)。支持的版本是 `(,3.2.2+1.20]`
-   使用 architectury api 来检查 mod 是否存在
-   忽略目录 `.vs/`

## v2.0.8-mc1.18.2

-   移植自 `v2.0.8-mc1.20.1`
-   移除依赖：YetAnotherConfigLib

## v2.0.8-mc1.20.1

### 新增

-   添加配置项：`player_invisible_threshold`

### 变化

-   默认禁用玩家虚化效果

### 修复

-   第一人称下使用望远镜时玩家异常转动 #78
-   发光效果时崩溃 #80
-   喷溅药水不会自动触发瞄准模式 #105
-   进入第三人称时玩家异常旋转 #96
-   进入世界时游戏卡死（forge）

## v2.0.8-beta.5-mc1.20.4

-   移植自 v2.0.8-beta.5-mc1.20.1

感谢 [ArctynFox](https://github.com/ArctynFox) [将它移植到1.20.4](https://github.com/Leawind/Third-Person/pull/88)！

## v2.0.8-beta.5-mc1.20.1

### 新增

-   进食时不旋转
    -   添加配置项：`do_not_rotate_when_eating`
-   根据动画（弓箭或长矛）确定瞄准模式 #69
    -   添加配置项：`determine_aim_mode_by_animation`
-   在 YACL 配置屏幕中添加了一个类别：相机偏移

### 变化

-   当相机实体的不透明度接近 1.0 时，以原版方式渲染
-   切换到第三人称时，相机从眼睛位置开始过渡
-   暂时进入第一人称时不进行过渡

### 移除

-   移除配置项 `turn_with_camera_when_enter_first_person`

### 修复

-   物品模式中无法使用数字 #67
-   物品模式的默认命名空间始终为 `minecraft` #68

## v2.0.8-beta.4-mc1.20.1

### 修复

-   Forge版本中，此模组内置资源包会单独显示，而且默认不启用 #56 #52
-   观察者模式行为异常 #60
-   生存模式下无法选取 #64
-   在渲染刻开始时，没有更新 partial tick

### 其他

-   更新 `fabric.mod.json` 中的源码链接

## v2.0.8-beta.3-mc1.20.1

### 修复

-   兼容 _Better Combat_ 和 _First Person Model_ #50
-   YACL 配置屏幕中的本地化键错误
-   在高草丛中强制第一人称 #54
-   在第三人称中，水被放置在玩家所看的位置，而不是准星的位置
-   第一人称使用鞘翅时意外旋转的问题

### 其他

-   更新翻译
-   构建脚本：在 fabric 发布脚本中添加 YACL 依赖

## v2.0.8-beta.2-mc1.20.1

### 新增

-   添加配置：`t2f_transition_halflife`

### 修复

-   第一人称中有双倍鼠标灵敏度 #49
-   MixinExtras 没有初始化
-   Forge 版本中选取方块错误

## v2.0.8-beta.1-mc1.20.1

移植自 2.0.7-mc1.19.4

### 移除

-   由于YACL存在的一些问题，删除了Forge版本对YACL的支持

### 兼容

-   兼容 _Do a Barrel Roll_

### 修复

-   按键 `force_aiming`，`toggle_aiming` 失效

## v2.0.7-mc1.19.4

移植自 `v2.0.7-mc1.19.3`

## v2.0.7-mc1.19.3

移植自 `v2.0.7-mc1.19.2`

## v2.0.7-mc1.19.2

### 新增

-   像第一人称一样射击敌人
    -   模组可以预测你想要射击的目标实体，即使目标距离很远，你也只需要像第一人称一样将准星放在目标上方即可
    -   添加配置项：`enable_target_entity_predict`，启用目标实体预测
-   从第三人称切换到第一人称时的平滑过渡
-   当相机实体在墙里时暂时切换到第一人称

### 变化

-   配置中的平滑系数改成了平滑半衰期，这样更符合直觉
-   更新平滑眼睛坐标时，乘上其与相机的距离。这样在不同相机距离下的平滑效果更加一致
-   现在 Cloth Config API 是可选依赖

### 兼容

-   兼容模组滚筒飞行do_a_barrel_roll（仅fabric版本兼容）

## v2.0.6-mc1.19.2

### 新增

-   当相机靠近玩家时，玩家变得半透明
-   关闭客户端时自动保存配置文件
-   默认启用第三人称视角
-   将是否启用第三人称视角保存到配置文件中
    -   添加配置项：`is_third_person_mode`
-   使用特定物品时，暂时进入第一人称视角（原版的望远镜）
    -   添加配置项：`use_to_first_person_pattern_expressions`

### 变化

-   修改资源包结构
    -   `assets/<命名空间>/item_patterns/hold_to_aim/<任意名称>.json` 手持时进入瞄准状态
    -   `assets/<命名空间>/item_patterns/use_to_aim/<任意名称>.json` 使用时进入瞄准状态
    -   `assets/<命名空间>/item_patterns/use_to_first_person/<任意名称>.json` 使用时暂时切换成第一人称视角

### 修复

-   相机穿过玻璃
-   游戏崩溃 #44

## v2.0.5-mc1.19.2

### 新增

-   从资源包中加载物品模式
-   创造模式下，即便玩家视线被遮挡，也可以直接从相机选取方块，
    -   添加配置项： `use_camera_pick_in_creative`

### 变化

-   重命名配置项
    -   `holding_item_aiming_rules` -> `hold_to_aim_item_pattern_expressions`
    -   `using_item_aiming_rules` -> `use_to_aim_item_pattern_expressions`

### 移除

-   移除配置项： `enable_buildin_item_aiming_rules`

### 修复

-   奔跑时无法快速转身
-   Forge 版本无法加入世界 #38

## v2.0.4-mc1.19.2

### 新增

-   可配置的瞄准规则
    -   添加布尔类型配置：启用内置匹配规则
    -   添加字符串列表类型配置：手持物品匹配规则
    -   添加字符串列表类型配置：使用物品匹配规则

### 修复

-   玩家的不平滑旋转
-   配置屏幕中，flying_smooth_factor选项位于错误的标签页

### 其他

-   Forge 版本中无法进入世界

## v2.0.3-mc1.19.2

### 新增

-   为 Forge 版本添加 Cloth-Config-API 支持
-   拉弓时自动侧身
-   添加配置：拉弓时自动旋转身体
-   添加按键：切换俯仰角锁定

### 修复

-   某些情况下无法pick实体
-   在专用服务器上配置文件仍会被保存
-   配置屏幕里缺少了一项：瞄准时渲染准星

## v2.0.2-mc1.19.2

移植到 1.19.2

### 新增

-   添加配置: 正常/瞄准模式下居中
-   Fabric 版本使用 Cloth Config API 构建配置屏幕

### 其他

-   forge版本暂时没有配置屏幕，但你仍可以手动修改配置文件。当尝试打开配置屏幕时，会自动重新加载配置文件

## v2.0.1 (1.19.4)

### 新增

-   添加配置: 飞行时自动居中
-   手持以下物品时自动进入瞄准模式
    -   末影珍珠
    -   雪球
    -   鸡蛋
    -   喷溅药水
    -   滞留药水
    -   附魔之瓶

### 修复

-   调整相机时平滑系数错误
-   只能向前游泳
-   相机靠近玩家时画面闪烁

## v2.0.1-beta.4-mc1.19.4

### 新增

-   添加配置：交互时自动转身 #4
-   添加配置：交互时转身方式 #26

### 修复

-   游戏暂停时相机仍在移动

## v2.0.1-beta.3-mc1.19.4

### 新增

-   添加按键：切换模组是否启用
-   添加配置: 进入第一人称时玩家是否转向相机朝向
-   添加配置: 调节相机时的平滑系数
-   现在可以在 [0,1] 范围内调整平滑系数了

### 修复

-   相机颤动（再次修复）
-   游泳时仅朝前方

## v2.0.1-beta.2-mc1.19.4

### 新增

-   添加配置: 锁定相机俯仰角
-   当相机离玩家足够近时，让玩家变得不可见

### 修复

-   玩家移动时相机颤动

## v2.0.0-mc1.19.4

### 新增

-   使用 [Architectury](https://github.com/architectury/architectury-api)
-   使用 [YACL](https://github.com/isXander/YetAnotherConfigLib) to generate config screen
-   Fabric 版本添加 [ModMenu](https://github.com/TerraformersMC/ModMenu) 支持
-   添加打开配置屏幕的按键绑定
-   瞄准时忽略类似高草丛的方块
-   使用鞘翅飞行时使用专门的平滑系数
-   添加配置项：`rotate_to_moving_direction` #22
-   添加配置项：`render_crosshair_when_aiming`
-   添加配置项：`render_crosshair_when_not_aiming`

### 修复

-   只能向前奔跑 #20
-   相机穿墙
