package fun.kaituo.starrailexpressroles.roles.avenger;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import dev.doctor4t.wathe.index.WatheItems;
import dev.doctor4t.wathe.index.WatheSounds;
import fun.kaituo.starrailexpressroles.StarRailExpressRoles;
import fun.kaituo.starrailexpressroles.misc.KillerRecorder;
import fun.kaituo.starrailexpressroles.misc.ServerTaskScheduler;
import fun.kaituo.starrailexpressroles.roles.RolesManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class AvengerComponent implements AutoSyncedComponent, ServerTickingComponent {

    public static final ComponentKey<AvengerComponent> KEY = ComponentRegistry.getOrCreate(
            Identifier.of(StarRailExpressRoles.MOD_ID, "avenger"),
            AvengerComponent.class
    );

    private final PlayerEntity player;

    private String killerName = "";
    private String principalName = "";
    private UUID principalID = null;

    private boolean failed = false;

    public AvengerComponent(PlayerEntity player) {
        this.player = player;
    }

    public String getKillerName() {
        return killerName;
    }
    public String getPrincipalName() {
        return principalName;
    }
    public UUID getPrincipalID() {
        return principalID;
    }

    public boolean hasFailed() {
        return failed;
    }

    @Override
    public void serverTick() {

        World world = player.getWorld();
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(world);
        if (!gameWorld.isRunning()) {
            return;
        }
        if (!gameWorld.isRole(this.player, RolesManager.AVENGER)) {
            return;
        }

        if (this.failed) {
            return;
        }
        if (principalID == null) {
            return;
        }

        if (!GameFunctions.isPlayerAliveAndSurvival(world.getPlayerByUuid(principalID))) {
            failed = true;
            this.sync();

            // Wait for the KillerRecorder
            ServerTaskScheduler.runTaskLater(() -> {
                this.killerName = KillerRecorder.getKillerName(world.getPlayerByUuid(principalID));
                this.startSeekingRevenge();
                this.sync();
            }, 2);
        }

    }

    @Override
    public void readFromNbt(@NotNull NbtCompound nbtCompound, RegistryWrapper.@NotNull WrapperLookup wrapperLookup) {
        this.killerName = nbtCompound.getString("killerName");
        this.principalName = nbtCompound.getString("principalName");
        this.principalID = (nbtCompound.containsUuid("principalID")) ? nbtCompound.getUuid("principalID") : null;
        this.failed = nbtCompound.getBoolean("failed");
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound nbtCompound, RegistryWrapper.@NotNull WrapperLookup wrapperLookup) {
        nbtCompound.putString("killerName", killerName);
        nbtCompound.putString("principalName", principalName);
        if (principalID != null) {
            nbtCompound.putUuid("principalID", principalID);
        }
        nbtCompound.putBoolean("failed", failed);
    }

    public void reset() {
        this.killerName = "";
        this.principalName = "";
        this.principalID = null;
        this.failed = false;
        this.sync();
    }

    public void sync() {
        KEY.sync(this.player);
    }

    public void setPrincipal(PlayerEntity player) {
        this.principalName = player.getName().getLiteralString();
        this.principalID = player.getUuid();
        this.failed = false;
        this.sync();
    }

    public void chooseRandomPrincipal(Set<UUID> playerIDs) {
        playerIDs.remove(this.player.getUuid());
        playerIDs.removeIf((playerID) -> (this.player.getWorld().getPlayerByUuid(playerID) == null));

        if (playerIDs.isEmpty()) {
            return;
        }

        List<PlayerEntity> players = new ArrayList<>();
        for (UUID playerID : playerIDs) {
            players.add(this.player.getWorld().getPlayerByUuid(playerID));
        }

        Collections.shuffle(players);
        setPrincipal(players.getFirst());
    }

    public void startSeekingRevenge() {

        if (!GameFunctions.isPlayerAliveAndSurvival(this.player)) {
            return;
        }

        AtomicInteger promptMessageCountdown = new AtomicInteger(200);
        ServerTaskScheduler.runTaskLoop(() -> {
            if (!(this.player instanceof ServerPlayerEntity serverPlayer)) {
                return;
            }
            if (!GameFunctions.isPlayerAliveAndSurvival(this.player)) {
                return;
            }

            switch (promptMessageCountdown.getAndDecrement()) {
                case 200 :
                    serverPlayer.playSoundToPlayer(WatheSounds.UI_RISER, SoundCategory.MASTER, 10f, 1f);
                    break;
                case 180 :
                    serverPlayer.playSoundToPlayer(WatheSounds.UI_PIANO, SoundCategory.MASTER, 10f, 1.25f);
                    serverPlayer.networkHandler.sendPacket(new TitleFadeS2CPacket(0, 60, 0));
                    serverPlayer.networkHandler.sendPacket(new TitleS2CPacket(Text.translatable("tip.starrailexpressroles.avenger.failure", Text.literal(this.principalName).formatted(Formatting.GREEN))));
                    break;
                case 120 :
                    serverPlayer.playSoundToPlayer(WatheSounds.UI_PIANO, SoundCategory.MASTER, 10f, 1.5f);
                    serverPlayer.networkHandler.sendPacket(new TitleFadeS2CPacket(0, 60, 0));
                    serverPlayer.networkHandler.sendPacket(new TitleS2CPacket(Text.translatable("tip.starrailexpressroles.avenger.revenge_start")));
                    break;
                case 60 :
                    serverPlayer.playSoundToPlayer(WatheSounds.UI_PIANO, SoundCategory.MASTER, 10f, 1.75f);
                    serverPlayer.networkHandler.sendPacket(new TitleFadeS2CPacket(0, 60, 0));
                    serverPlayer.networkHandler.sendPacket(new TitleS2CPacket(Text.translatable("tip.starrailexpressroles.avenger.prompt_killer", Text.literal(this.killerName).formatted(Formatting.RED))));

                    this.player.giveItemStack(new ItemStack(WatheItems.REVOLVER));
                    break;
                case 1 :
                    serverPlayer.playSoundToPlayer(WatheSounds.UI_PIANO_STINGER, SoundCategory.MASTER, 10f, 1f);
                    break;
            }
        }, 0, 200);
    }
}
