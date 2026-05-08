package fun.kaituo.starrailexpressroles.client.mixin.roles.creeper;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerShopComponent;
import dev.doctor4t.wathe.client.WatheClient;
import fun.kaituo.starrailexpressroles.client.InitializeClient;
import fun.kaituo.starrailexpressroles.components.AbilityPlayerComponent;
import fun.kaituo.starrailexpressroles.misc.StarRailExpressRolesConfig;
import fun.kaituo.starrailexpressroles.roles.RolesManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class ClientCreeperHudMixin {

    @Shadow
    public abstract TextRenderer getTextRenderer();

    @Inject(method = "render", at = @At("TAIL"))
    public void getAbilityHud(@NotNull DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {

        if (MinecraftClient.getInstance().player == null) {
            return;
        }

        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(MinecraftClient.getInstance().player.getWorld());
        AbilityPlayerComponent ability = AbilityPlayerComponent.KEY.get(MinecraftClient.getInstance().player);
        PlayerShopComponent playerShop = PlayerShopComponent.KEY.get(MinecraftClient.getInstance().player);

        if (gameWorld.isRole(MinecraftClient.getInstance().player, RolesManager.CREEPER) && WatheClient.isPlayerAliveAndInSurvival()) {

            Text line;
            if (playerShop.balance < StarRailExpressRolesConfig.HANDLER.instance().CreeperExplodePrice) {
                line = Text.translatable("tip.starrailexpressroles.ability.not_enough_money", StarRailExpressRolesConfig.HANDLER.instance().CreeperExplodePrice);
            } else if (ability.cooldown > 0) {
                line = Text.translatable("tip.starrailexpressroles.cooldown", ability.cooldown / 20);
            } else {
                line = Text.translatable("tip.starrailexpressroles.ability.can_use", InitializeClient.abilityBind.getBoundKeyLocalizedText());
            }

            int drawY = context.getScaledWindowHeight() - getTextRenderer().getWrappedLinesHeight(line, 65535);

            context.drawTextWithShadow(getTextRenderer(), line, context.getScaledWindowWidth() - getTextRenderer().getWidth(line), drawY, RolesManager.CREEPER.color());
        }
    }
}
