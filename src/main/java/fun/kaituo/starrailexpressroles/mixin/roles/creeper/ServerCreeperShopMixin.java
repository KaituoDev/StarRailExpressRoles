package fun.kaituo.starrailexpressroles.mixin.roles.creeper;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerShopComponent;
import dev.doctor4t.wathe.util.ShopEntry;
import fun.kaituo.starrailexpressroles.roles.RolesManager;
import fun.kaituo.starrailexpressroles.roles.RolesShopManager;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerShopComponent.class)
public abstract class ServerCreeperShopMixin {

    @Shadow
    public int balance;
    @Shadow public abstract void sync();
    @Shadow @Final
    @NotNull
    private PlayerEntity player;

    @Inject(method = "tryBuy", at = @At("HEAD"), cancellable = true)
    void tryBuy(int index, @NotNull CallbackInfo ci) {
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(this.player.getWorld());
        if (gameWorld.isRole(this.player, RolesManager.CREEPER)) {
            if (index < 0 || index >= RolesShopManager.getCreeperShop(this.player.getWorld()).size()) return;
            ShopEntry entries = RolesShopManager.getCreeperShop(this.player.getWorld()).get(index);
            if (RolesShopManager.handlePurchase(this.player, this.balance, entries.stack().getItem(), entries.price())) {
                this.balance -= entries.price();
                this.sync();
            }
            ci.cancel();
        }
    }
}
