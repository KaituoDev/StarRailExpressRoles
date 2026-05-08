package fun.kaituo.starrailexpressroles.roles.creeper;

import dev.doctor4t.wathe.game.GameFunctions;
import dev.doctor4t.wathe.index.WatheItems;
import dev.doctor4t.wathe.index.WatheParticles;
import dev.doctor4t.wathe.index.WatheSounds;
import fun.kaituo.starrailexpressroles.StarRailExpressRoles;
import fun.kaituo.starrailexpressroles.misc.StarRailExpressRolesConfig;
import fun.kaituo.starrailexpressroles.roles.DeathReasonManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import java.util.ArrayList;
import java.util.UUID;

public class CreeperComponent implements AutoSyncedComponent, ServerTickingComponent {

    public static final ComponentKey<CreeperComponent> KEY = ComponentRegistry.getOrCreate(
            Identifier.of(StarRailExpressRoles.MOD_ID, "creeper"),
            CreeperComponent.class
    );

    private final PlayerEntity player;

    private int remainingBurnTime = 0;

    public CreeperComponent(PlayerEntity player) {
        this.player = player;
    }

    public int getRemainingBurnTime() {
        return this.remainingBurnTime;
    }

    @Override
    public void serverTick() {
        if (this.remainingBurnTime > 0) {

            if (player.getWorld() instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(ParticleTypes.ANGRY_VILLAGER,
                        player.getX() + Math.random()/10, player.getY() + 2.25, player.getZ() + Math.random()/10,
                        10, 0, 0, 0, 0.05f);
            }

            if (this.remainingBurnTime == 1) {
                this.explode();
            }
            --this.remainingBurnTime;
            this.sync();
        }
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound nbtCompound, RegistryWrapper.@NotNull WrapperLookup wrapperLookup) {
        this.remainingBurnTime = nbtCompound.contains("remainingBurnTime") ?
                nbtCompound.getInt("remainingBurnTime") : 0;
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound nbtCompound, RegistryWrapper.@NotNull WrapperLookup wrapperLookup) {
        nbtCompound.putInt("remainingBurnTime", this.remainingBurnTime);
    }

    public void sync() {
        KEY.sync(this.player);
    }

    public void ignite() {
        if (this.player.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.playSound(this.player, this.player.getX(), this.player.getY(), this.player.getZ(),
                    SoundEvents.ENTITY_CREEPER_PRIMED, SoundCategory.PLAYERS, 5f, 1f + (float)(Math.random()/10 - 0.05));
        }

        this.remainingBurnTime = 20 * StarRailExpressRolesConfig.HANDLER.instance().CreeperExplodeChargeDuration;
        this.sync();
    }

    public void explode() {
        if (!(this.player.getWorld() instanceof ServerWorld world)) {
            return;
        }

        float X = (float) this.player.getX();
        float Y = (float) this.player.getY();
        float Z = (float) this.player.getZ();

        float explodeSoundPitch = (float) (1 + Math.random()/10 - 0.05);

        world.playSound(this.player, X, Y + 1, Z, WatheSounds.ITEM_GRENADE_EXPLODE, SoundCategory.PLAYERS, 3f, explodeSoundPitch);

        world.spawnParticles(WatheParticles.BIG_EXPLOSION, X, Y + 1f, Z, 1, 0, 0, 0, 0);
        world.spawnParticles(ParticleTypes.SMOKE, X, Y + 1f, Z, 100, 0, 0, 0, 0.2f);
        world.spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, WatheItems.GRENADE.getDefaultStack()), X, Y + 1f, Z, 100, 0, 0, 0, 1f);

        ArrayList<UUID> targetIDs = new ArrayList<>();
        for (ServerPlayerEntity target : world.getPlayers()) {
            if (target.getUuid() == this.player.getUuid()) {
                continue;
            }
            if (!this.player.getBoundingBox().expand(3, 2, 3).contains(target.getPos())) {
                continue;
            }
            if (!GameFunctions.isPlayerAliveAndSurvival(target)) {
                continue;
            }

            targetIDs.add(target.getUuid());
        }
        for (UUID uuid : targetIDs) {
            PlayerEntity target = world.getPlayerByUuid(uuid);
            if (target != null) {
                GameFunctions.killPlayer(target, true, this.player, DeathReasonManager.CREEPER_EXPLOSION);
            }
        }
    }
}
