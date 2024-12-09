package com.github.leawind.thirdperson.forge;

import com.github.leawind.thirdperson.api.base.GameEvents;
import com.github.leawind.thirdperson.api.client.event.ThirdPersonCameraSetupEvent;
import com.github.leawind.thirdperson.mixin.CameraInvoker;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ThirdPersonEventsForge {
  /** 低优先级意味着在其他模组之后执行，可以覆盖其他模组对相机位置、朝向所做的修改。 */
  @SuppressWarnings("unused")
  @SubscribeEvent(priority = EventPriority.LOW)
  public static void cameraSetupEvent(EntityViewRenderEvent.CameraSetup event) {
    if (GameEvents.thirdPersonCameraSetup != null) {
      var camera = event.getCamera();
      var evt = new ThirdPersonCameraSetupEvent((float) event.getPartialTicks());
      GameEvents.thirdPersonCameraSetup.accept(evt);
      if (evt.set()) {
        ((CameraInvoker) camera).invokeSetPosition(evt.pos);
        event.setYaw(evt.yRot);
        event.setPitch(evt.xRot);
        // Forge does not update rotation, forwards, up, left
        // So need to invoke vanilla `setRotation` here
        ((CameraInvoker) camera).invokeSetRotation(evt.yRot, evt.xRot);
      }
    }
  }
}
