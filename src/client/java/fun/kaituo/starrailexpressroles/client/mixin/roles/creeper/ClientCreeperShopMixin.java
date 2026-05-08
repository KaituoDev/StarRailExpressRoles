package fun.kaituo.starrailexpressroles.client.mixin.roles.creeper;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.client.gui.screen.ingame.LimitedInventoryScreen;
import dev.doctor4t.wathe.util.ShopEntry;
import fun.kaituo.starrailexpressroles.roles.RolesManager;
import fun.kaituo.starrailexpressroles.roles.RolesShopManager;
import net.minecraft.client.network.ClientPlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(LimitedInventoryScreen.class)
public abstract class ClientCreeperShopMixin {

    @Shadow
    @Final
    @NotNull
    public ClientPlayerEntity player;

    @ModifyVariable(method = "init", at = @At(value = "STORE"), name = "entries")
    private List<ShopEntry> getShop(@NotNull List<ShopEntry> entries) {
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(this.player.getWorld());
        if (gameWorld.isRole(this.player, RolesManager.CREEPER)) {
            entries = RolesShopManager.getCreeperShop(this.player.getWorld());
        }
        return entries;
    }
}
