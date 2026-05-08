package fun.kaituo.starrailexpressroles.client.mixin.roles.creeper;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.client.WatheClient;
import dev.doctor4t.wathe.game.GameFunctions;
import fun.kaituo.starrailexpressroles.roles.RolesManager;
import fun.kaituo.starrailexpressroles.roles.creeper.CreeperComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WatheClient.class)
public class CreeperInstinctMixin {

    @Inject(method = "getInstinctHighlight", at = @At("HEAD"), cancellable = true)
    private static void getInstinctHighlight(@NotNull Entity target, @NotNull CallbackInfoReturnable<Integer> cir) {
        if (MinecraftClient.getInstance().world == null) {
            return;
        }
        World world = MinecraftClient.getInstance().world;

        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(world);
        if (target instanceof @NotNull PlayerEntity targetPlayer) {
            if (GameFunctions.isPlayerAliveAndSurvival(targetPlayer)) {
                if (gameWorld.isRole(targetPlayer, RolesManager.CREEPER)) {
                    if (CreeperComponent.KEY.get(targetPlayer).getRemainingBurnTime() > 0) {
                        cir.setReturnValue(16760346);
                    }
                }
            }
        }
    }
}
