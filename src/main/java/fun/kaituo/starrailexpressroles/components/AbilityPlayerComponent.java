package fun.kaituo.starrailexpressroles.components;

import fun.kaituo.starrailexpressroles.StarRailExpressRoles;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ClientTickingComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

public class AbilityPlayerComponent implements AutoSyncedComponent, ServerTickingComponent, ClientTickingComponent {

    public static final ComponentKey<AbilityPlayerComponent> KEY = ComponentRegistry.getOrCreate(
            Identifier.of(StarRailExpressRoles.MOD_ID, "ability"),
            AbilityPlayerComponent.class
    );

    private final PlayerEntity player;
    public int cooldown = 0;

    public AbilityPlayerComponent(@NotNull PlayerEntity player) {
        this.player = player;
    }

    @Override
    public void clientTick() {

    }

    @Override
    public void serverTick() {
        if (this.cooldown > 0) {
            -- this.cooldown;
            this.sync();
        }
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.@NotNull WrapperLookup registryLookup) {
        this.cooldown = (tag.contains("cooldown")) ? tag.getInt("cooldown") : 0;
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.@NotNull WrapperLookup registryLookup) {
        tag.putInt("cooldown", this.cooldown);
    }

    public void setAbilityCooldown(int seconds) {
        this.cooldown = seconds * 20;
    }

    public void reset() {
        this.cooldown = 0;
        this.sync();
    }

    public void sync() {
        KEY.sync(this.player);
    }

}
