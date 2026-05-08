package fun.kaituo.starrailexpressroles.client.mixin.roles.creeper;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.client.WatheClient;
import fun.kaituo.starrailexpressroles.roles.RolesManager;
import fun.kaituo.starrailexpressroles.roles.creeper.CreeperComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = KeyBinding.class, priority = 6000)
public class CreeperLimitKeysMixin {

    @Inject(method = "wasPressed", at = @At("RETURN"), cancellable = true)
    private void wasPressed(@NotNull CallbackInfoReturnable<Boolean> cir) {
        creeperBurningLimitKeys(cir);
    }

    @Inject(method = "isPressed", at = @At("RETURN"), cancellable = true)
    private void isPressed(@NotNull CallbackInfoReturnable<Boolean> cir) {
        creeperBurningLimitKeys(cir);
    }

    @Unique
    private void creeperBurningLimitKeys(@NotNull CallbackInfoReturnable<Boolean> cir) {
        if (MinecraftClient.getInstance().player == null) {
            return;
        }
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (MinecraftClient.getInstance().world == null) {
            return;
        }
        World world = MinecraftClient.getInstance().world;

        if (WatheClient.isPlayerAliveAndInSurvival()) {
            if (GameWorldComponent.KEY.get(world)
                    .isRole(player, RolesManager.CREEPER)) {
                KeyBinding key = (KeyBinding) (Object) this;
                boolean isUseKey = key.equals(MinecraftClient.getInstance().options.useKey);
                boolean isJumpKey = key.equals(MinecraftClient.getInstance().options.jumpKey);
                boolean isAttackKey = key.equals(MinecraftClient.getInstance().options.attackKey);
                if (CreeperComponent.KEY.get(player).getRemainingBurnTime() > 0
                        && (isUseKey || isJumpKey || isAttackKey)) {
                    cir.setReturnValue(false);
                }
            }
        }
    }
}
