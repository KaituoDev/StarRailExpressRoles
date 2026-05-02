package fun.kaituo.starrailexpressroles.mixin.recorder;

import dev.doctor4t.wathe.game.GameFunctions;
import fun.kaituo.starrailexpressroles.misc.KillerRecorder;
import fun.kaituo.starrailexpressroles.misc.ServerTaskScheduler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameFunctions.class)
public abstract class RecordKillerMixin {

    @Inject(method = "killPlayer(Lnet/minecraft/entity/player/PlayerEntity;ZLnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Identifier;)V"
            , at = @At("HEAD"))
    private static void recordKiller(PlayerEntity victim, boolean spawnBody, PlayerEntity killer, Identifier deathReason, CallbackInfo ci) {

        ServerTaskScheduler.runTaskLater(() -> {
            if (victim == null || killer == null) {
                return;
            }

            if (!GameFunctions.isPlayerAliveAndSurvival(victim)) {
                KillerRecorder.record(victim, killer);
            }
        }, 1);
    }

    @Inject(method = "initializeGame", at = @At("HEAD"))
    private static void initializeRecorder(ServerWorld serverWorld, CallbackInfo ci) {
        KillerRecorder.clear();
    }
}
