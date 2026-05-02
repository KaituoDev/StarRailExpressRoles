package fun.kaituo.starrailexpressroles.mixin.host;

import dev.doctor4t.wathe.game.GameFunctions;
import fun.kaituo.starrailexpressroles.components.AbilityPlayerComponent;
import fun.kaituo.starrailexpressroles.roles.avenger.AvengerComponent;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameFunctions.class)
public abstract class GameFunctionMixin {

    @Inject(method = "resetPlayer", at = @At("TAIL"))
    private static void resetPlayer(@NotNull ServerPlayerEntity player, CallbackInfo ci) {
        AbilityPlayerComponent.KEY.get(player).reset();

        AvengerComponent.KEY.get(player).reset();
    }
}
