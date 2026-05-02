package fun.kaituo.starrailexpressroles.misc;

import net.minecraft.entity.player.PlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KillerRecorder {

    /**
     * Record the name of the killer of the player with the given UUID.
     */
    private static final Map<UUID, String> killerList = new HashMap<>();

    /**
     *
     * @param victim Target player to be retrieved
     * @return The name of the killer of the victim. Returns "???" if the victim died accidentally.
     */
    public static String getKillerName(PlayerEntity victim) {
        if (victim == null) {
            return "???";
        }

        UUID victimID = victim.getUuid();
        if (killerList.containsKey(victimID)) {
            return killerList.get(victimID);
        }

        return "???";
    }

    public static void record(PlayerEntity victim, PlayerEntity killer) {
        killerList.put(
                victim.getUuid(),
                killer.getName().getLiteralString());
    }

    public static void clear() {
        killerList.clear();
    }
}
