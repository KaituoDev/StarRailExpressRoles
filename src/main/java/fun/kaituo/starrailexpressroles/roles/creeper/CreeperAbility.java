package fun.kaituo.starrailexpressroles.roles.creeper;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerShopComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import fun.kaituo.starrailexpressroles.components.AbilityPlayerComponent;
import fun.kaituo.starrailexpressroles.misc.StarRailExpressRolesConfig;
import fun.kaituo.starrailexpressroles.roles.RolesManager;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;

public class CreeperAbility {

    public static void register(@NotNull PlayerEntity player) {
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(player.getWorld());
        AbilityPlayerComponent ability = AbilityPlayerComponent.KEY.get(player);
        PlayerShopComponent playerShop = PlayerShopComponent.KEY.get(player);

        if (GameFunctions.isPlayerAliveAndSurvival(player)
                && gameWorld.isRole(player, RolesManager.CREEPER)
                && ability.cooldown <= 0
                && playerShop.balance >= StarRailExpressRolesConfig.HANDLER.instance().CreeperExplodePrice) {

            playerShop.balance -= StarRailExpressRolesConfig.HANDLER.instance().CreeperExplodePrice;
            playerShop.sync();

            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.SLOWNESS,
                    20 * StarRailExpressRolesConfig.HANDLER.instance().CreeperExplodeChargeDuration,
                    4,
                    false,
                    true,
                    true
            ));

            CreeperComponent.KEY.get(player).ignite();

            ability.setAbilityCooldown(StarRailExpressRolesConfig.HANDLER.instance().CreeperExplodeCooldown);
        }
    }
}
