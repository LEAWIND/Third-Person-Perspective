package com.github.leawind.thirdperson.config;

import com.github.leawind.thirdperson.ThirdPerson;
import com.github.leawind.thirdperson.ThirdPersonConstants;
import com.github.leawind.thirdperson.screen.ConfigScreenBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Timer;
import java.util.TimerTask;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConfigManager {
  private final Gson GSON =
      new GsonBuilder()
          .excludeFieldsWithoutExposeAnnotation()
          .setPrettyPrinting()
          .disableHtmlEscaping()
          .create();
  private final Timer lazySaveTimer = new Timer();
  private @NotNull Config config = new Config();
  private boolean isLazySaveScheduled = false;

  public ConfigManager() {}

  /**
   * 加载配置
   *
   * <p>如果找不到文件，则保存一份。
   *
   * <p>如果失败，则记录错误到日志
   */
  public void tryLoad() {
    ThirdPerson.LOGGER.debug("Trying loading config from {}", ThirdPersonConstants.CONFIG_FILE);
    try {
      ThirdPersonConstants.CONFIG_FILE.getParentFile().mkdirs();
      if (ThirdPersonConstants.CONFIG_FILE.exists()) {
        load();
        ThirdPerson.LOGGER.info("Config is loaded from {}", ThirdPersonConstants.CONFIG_FILE);
      } else {
        ThirdPerson.LOGGER.info("Config not found, creating one.");
        trySave();
      }
    } catch (IOException e) {
      ThirdPerson.LOGGER.error("Failed to load config.", e);
    } catch (JsonSyntaxException e) {
      ThirdPerson.LOGGER.error("Config file is broken.", e);
    }
    config.update();
  }

  /**
   * 惰性保存
   *
   * <p>两次保存时间间隔至少为 {@link ThirdPersonConstants#CONFIG_LAZY_SAVE_DELAY}
   */
  public void lazySave() {
    if (!isLazySaveScheduled) {
      isLazySaveScheduled = true;
      lazySaveTimer.schedule(
          new TimerTask() {
            @Override
            public void run() {
              trySave();
              isLazySaveScheduled = false;
            }
          },
          ThirdPersonConstants.CONFIG_LAZY_SAVE_DELAY);
    }
  }

  /**
   * 尝试保存配置文件
   *
   * <p>如果失败，则记录错误到日志
   */
  public void trySave() {
    ThirdPerson.LOGGER.debug("Trying saving config to {}", ThirdPersonConstants.CONFIG_FILE);
    try {
      save();
      ThirdPerson.LOGGER.info("Config is saved.");
    } catch (IOException e) {
      ThirdPerson.LOGGER.error("Failed to save config.", e);
    }
    config.update();
  }

  /** 直接读取配置文件 */
  public void load() throws IOException {
    config =
        GSON.fromJson(
            Files.readString(ThirdPersonConstants.CONFIG_FILE.toPath(), StandardCharsets.UTF_8),
            Config.class);
  }

  /** 直接保存配置文件 */
  public void save() throws IOException {
    FileUtils.writeStringToFile(
        ThirdPersonConstants.CONFIG_FILE, GSON.toJson(this.config), StandardCharsets.UTF_8);
  }

  /** 获取配置屏幕 */
  public @Nullable Screen getConfigScreen(@Nullable Screen parent) {
    var builder = ConfigScreenBuilder.getBuilder();
    if (builder == null) {
      ThirdPerson.LOGGER.warn("No config screen builder available.");
      return null;
    }
    ThirdPerson.LOGGER.debug("Building config screen");
    return builder.build(config, parent);
  }

  /** 获取配置对象 */
  public @NotNull Config getConfig() {
    return this.config;
  }

  /**
   * 在可翻译文本的键前加上mod_id前缀
   *
   * @param name 键名
   * @return ${mod_id}.${id}
   */
  @Contract(value = "_ -> new", pure = true)
  public static @NotNull Component getText(@NotNull String name) {
    return new TranslatableComponent(ThirdPersonConstants.MOD_ID + "." + name);
  }
}
