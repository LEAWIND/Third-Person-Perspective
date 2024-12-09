package com.github.leawind.thirdperson.forge;

import com.github.leawind.thirdperson.ThirdPerson;
import com.github.leawind.thirdperson.ThirdPersonConstants;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@SuppressWarnings("unused")
@Mod(ThirdPersonConstants.MOD_ID)
public final class ThirdPersonForge {
  public ThirdPersonForge() {
    // 仅在客户端运行
    DistExecutor.unsafeRunWhenOn(
        Dist.CLIENT,
        () ->
            () -> {
              EventBuses.registerModEventBus(
                  ThirdPersonConstants.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
              ThirdPerson.init();
              MinecraftForge.EVENT_BUS.register(ThirdPersonEventsForge.class);
            });
  }
}
