package com.github.leawind.thirdperson.screen;

import com.github.leawind.thirdperson.ThirdPerson;
import com.github.leawind.thirdperson.config.Config;
import com.github.leawind.thirdperson.config.ConfigManager;
import com.github.leawind.thirdperson.util.PossibleSupplier;
import com.github.leawind.thirdperson.util.annotation.VersionSensitive;
import dev.architectury.platform.Platform;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 配置屏幕构建器
 *
 * @see ConfigManager#getConfigScreen(Screen)
 */
@SuppressWarnings("all")
@VersionSensitive("YACL version check")
public abstract class ConfigScreenBuilder {
  /** 已经实现或将来可能实现的构建器们 */
  private static Map<String, PossibleSupplier<ConfigScreenBuilder>> builders = new HashMap<>();

  static {
    builders.put(
        "Cloth Config",
        PossibleSupplier.of(
            () -> new ClothConfigScreenBuilder(),
            () -> Platform.isModLoaded("cloth-config") || Platform.isModLoaded("cloth_config")));
    builders.put(
        "YACL",
        PossibleSupplier.of(
            () -> new YaclConfigScreenBuilder(),
            () ->
                Platform.isModLoaded("yet_another_config_lib_v3")
                    && !(Platform.isForge()
                        && !Platform.getMod("yet_another_config_lib_v3")
                            .getVersion()
                            .startsWith("3.2."))));

    var availables = ConfigScreenBuilder.getAvailableBuidlers().keySet();
    availables.forEach(
        name -> {
          ThirdPerson.LOGGER.debug("Found available config screen builder: {}", name);
        });
    if (availables.isEmpty()) {
      ThirdPerson.LOGGER.warn("No config screen API available.");
    }
  }

  /** 根据配置获取屏幕构建器 */
  public static @Nullable ConfigScreenBuilder getBuilder() {
    final var availables = getAvailableBuidlers();
    if (availables.isEmpty()) {
      return null;
    }
    return availables
        .getOrDefault(
            ThirdPerson.getConfig().config_screen_api, availables.values().iterator().next())
        .get();
  }

  /** 获取全部可用的构建器 */
  public static @NotNull Map<String, PossibleSupplier<ConfigScreenBuilder>> getAvailableBuidlers() {
    final Map<String, PossibleSupplier<ConfigScreenBuilder>> availableBuilders = new HashMap<>();
    builders.forEach(
        (name, builder) -> {
          if (builder.available()) {
            availableBuilders.put(name, builder);
          }
        });
    return availableBuilders;
  }

  /**
   * 构建配置屏幕
   *
   * @param config 配置实例
   * @param parent 父屏幕
   * @return 配置屏幕
   */
  @NotNull
  public abstract Screen build(@NotNull Config config, @Nullable Screen parent);
}
