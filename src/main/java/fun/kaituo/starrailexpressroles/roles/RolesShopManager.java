package fun.kaituo.starrailexpressroles.roles;

import dev.doctor4t.wathe.cca.PlayerShopComponent;
import dev.doctor4t.wathe.index.WatheItems;
import dev.doctor4t.wathe.index.WatheSounds;
import dev.doctor4t.wathe.util.ShopEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RolesShopManager {

    public static List<ShopEntry> getCreeperShop(@NotNull World world) {
        return Util.make(new ArrayList<>(), (entries) -> {
            entries.add(new ShopEntry(WatheItems.KNIFE.getDefaultStack(), 100, ShopEntry.Type.WEAPON));
            entries.add(new ShopEntry(WatheItems.GRENADE.getDefaultStack(), 350, ShopEntry.Type.WEAPON));
            entries.add(new ShopEntry(WatheItems.FIRECRACKER.getDefaultStack(), 10, ShopEntry.Type.TOOL));
            entries.add(new ShopEntry(WatheItems.LOCKPICK.getDefaultStack(), 50, ShopEntry.Type.TOOL));
            entries.add(new ShopEntry(WatheItems.CROWBAR.getDefaultStack(), 25, ShopEntry.Type.TOOL));
            entries.add(new ShopEntry(WatheItems.NOTE.getDefaultStack(), 10, ShopEntry.Type.TOOL));
        });
    }

    public static boolean handlePurchase(@NotNull PlayerEntity player, int balance, @NotNull Item item, int price) {
        if (balance >= price && !player.getItemCooldownManager().isCoolingDown(item)) {

            if (item == WatheItems.NOTE) {
                player.giveItemStack((new ItemStack(WatheItems.NOTE, 4)));
            }
            else if (item == WatheItems.BLACKOUT) {
                PlayerShopComponent.useBlackout(player);
            }
            else if (item == WatheItems.PSYCHO_MODE) {
                PlayerShopComponent.usePsychoMode(player);
            }
            else {
                player.giveItemStack(item.getDefaultStack());
            }

            if (player instanceof @NotNull ServerPlayerEntity serverPlayer) {
                serverPlayer.playSoundToPlayer(WatheSounds.UI_SHOP_BUY, SoundCategory.PLAYERS,1.0F, 0.9F + player.getRandom().nextFloat() * 0.2F);
            }
            return true;
        } else {

            player.sendMessage(Text.translatable("shop.purchase_failed").withColor(0xAA0000), true);
            if (player instanceof @NotNull ServerPlayerEntity serverPlayer) {
                serverPlayer.playSoundToPlayer(WatheSounds.UI_SHOP_BUY_FAIL, SoundCategory.PLAYERS,1.0F, 0.9F + player.getRandom().nextFloat() * 0.2F);
            }
            return false;
        }
    }
}
