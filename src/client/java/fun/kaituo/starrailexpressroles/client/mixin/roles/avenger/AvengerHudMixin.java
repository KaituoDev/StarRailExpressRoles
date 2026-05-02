package fun.kaituo.starrailexpressroles.client.mixin.roles.avenger;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.client.gui.RoleNameRenderer;
import fun.kaituo.starrailexpressroles.roles.RolesManager;
import fun.kaituo.starrailexpressroles.roles.avenger.AvengerComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RoleNameRenderer.class)
public abstract class AvengerHudMixin {

    @Unique
    private static final int killerColor = 16603720;
    @Unique
    private static final int principalColor = 3996833;

    @Inject(method = "renderHud", at = @At("TAIL"))
    private static void showKiller(TextRenderer renderer, ClientPlayerEntity player, DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {

        ClientPlayerEntity clientPlayer = MinecraftClient.getInstance().player;
        if (clientPlayer != null) {

            GameWorldComponent gameWorld = GameWorldComponent.KEY.get(clientPlayer.getWorld());
            if (gameWorld.isRunning()) {

                if (gameWorld.getRole(player) != null && gameWorld.getRole(player).equals(RolesManager.AVENGER)) {

                    AvengerComponent avengerComponent = AvengerComponent.KEY.get(clientPlayer);
                    if (avengerComponent.hasFailed()) {
                        renderKillerHud(renderer, context, AvengerComponent.KEY.get(clientPlayer).getKillerName());
                    }
                }

            }

        }

    }

    @Inject(method = "renderHud", at = @At("TAIL"))
    private static void showPrincipal(TextRenderer renderer, ClientPlayerEntity player, DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {

        ClientPlayerEntity clientPlayer = MinecraftClient.getInstance().player;
        if (clientPlayer != null) {

            GameWorldComponent gameWorld = GameWorldComponent.KEY.get(clientPlayer.getWorld());
            if (gameWorld.isRunning()) {

                if (gameWorld.getRole(player) != null && gameWorld.getRole(player).equals(RolesManager.AVENGER)) {

                    AvengerComponent avengerComponent = AvengerComponent.KEY.get(clientPlayer);
                    if (!avengerComponent.hasFailed()) {
                        renderPrincipalHud(renderer, context, AvengerComponent.KEY.get(clientPlayer).getPrincipalName());
                    }
                }

            }

        }

    }

    @Unique
    private static void renderKillerHud(TextRenderer renderer, DrawContext context, String killerName) {

        Text prompt = Text.translatable("hud.starrailexpressroles.avenger.prompt_killer", killerName);

        context.drawText(
                renderer,
                prompt,
                context.getScaledWindowWidth() - renderer.getWidth(prompt),
                context.getScaledWindowHeight() - 3 * renderer.getWrappedLinesHeight(prompt, 65535),
                killerColor,
                true
        );

    }

    @Unique
    private static void renderPrincipalHud(TextRenderer renderer, DrawContext context, String principalName) {

        Text prompt = Text.translatable("hud.starrailexpressroles.avenger.prompt_principal", principalName);

        context.drawText(
                renderer,
                prompt,
                context.getScaledWindowWidth() - renderer.getWidth(prompt),
                context.getScaledWindowHeight() - 3 * renderer.getWrappedLinesHeight(prompt, 65535),
                principalColor,
                true
        );

    }
}
