package fun.kaituo.starrailexpressroles.roles;

import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.api.WatheRoles;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerShopComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import fun.kaituo.starrailexpressroles.StarRailExpressRoles;
import fun.kaituo.starrailexpressroles.components.AbilityPlayerComponent;
import fun.kaituo.starrailexpressroles.misc.ServerTaskScheduler;
import fun.kaituo.starrailexpressroles.packet.host.AbilityC2SPacket;
import fun.kaituo.starrailexpressroles.roles.avenger.AvengerComponent;
import fun.kaituo.starrailexpressroles.roles.creeper.CreeperAbility;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.agmas.harpymodloader.events.ModdedRoleAssigned;
import org.agmas.harpymodloader.events.ResetPlayerEvent;
import org.agmas.harpymodloader.modifiers.Modifier;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class RolesManager {

    private static final HashMap<String, Role> ROLES = new HashMap<>();
    public static HashMap<String, Role> getRoles() {
        return ROLES;
    }

    private static final HashMap<String, Modifier> MODIFIERS = new HashMap<>();
    public static HashMap<String, Modifier> getModifiers() {
        return MODIFIERS;
    }

    public static Role AVENGER = registerRole(new Role(
            Identifier.of(StarRailExpressRoles.MOD_ID, "avenger"),
            0xDE1300,
            true,
            false,
            Role.MoodType.REAL,
            WatheRoles.CIVILIAN.getMaxSprintTime(),
            false
    ));
    public static Role CREEPER = registerRole(new Role(
            Identifier.of(StarRailExpressRoles.MOD_ID, "creeper"),
            0x36AE24,
            false,
            true,
            Role.MoodType.FAKE,
            -1,
            true
    ));

    public static Role registerRole(Role role) {
        WatheRoles.registerRole(role);
        ROLES.put(role.identifier().getPath(), role);
        return role;
    }

    ///  注册身份技能
    public static void registerRolesAbility() {
        ServerPlayNetworking.registerGlobalReceiver(AbilityC2SPacket.ID, (payload, context) -> {
            CreeperAbility.register(context.player());
        });
    }

    /// 添加有被动收入的身份
    public static List<Role> rolesHavePassiveIncome() {
        List<Role> roles = new ArrayList<>();
        roles.add(RolesManager.CREEPER);
        return List.copyOf(roles);
    }

    /// 添加有任务收入的身份
    public static List<Role> rolesHaveTaskIncome() {
        List<Role> roles = new ArrayList<>();
        roles.add(RolesManager.CREEPER);
        return List.copyOf(roles);
    }

    /**
     * At game start.
     */
    public static void setDefaultEvents() {
        ModdedRoleAssigned.EVENT.register((player, role)->{
            AbilityPlayerComponent ability = AbilityPlayerComponent.KEY.get(player);
            if (FabricLoader.getInstance().isModLoaded("kinswathe")) {
                try {
                    // 反射获取开局冷却
                    Class<?> configClass = Class.forName("org.BsXinQin.kinswathe.KinsWatheConfig");
                    int startingCooldown = getStartingCooldown(configClass);

                    ability.cooldown = startingCooldown * 20;
                } catch (Exception e) {
                    ability.cooldown = 0;
                }
            }
            GameWorldComponent gameWorld = GameWorldComponent.KEY.get(player.getWorld());
            PlayerShopComponent playerShop = PlayerShopComponent.KEY.get(player);

            if (role.equals(AVENGER)) {
                ServerTaskScheduler.runTaskLater(() -> {
                            if (GameFunctions.isPlayerAliveAndSurvival(player)) {
                                AvengerComponent.KEY.get(player).chooseRandomPrincipal(new HashSet<>(gameWorld.getRoles().keySet()));
                            }
                        }
                        , 20);
            }
        });
    }

    private static int getStartingCooldown(Class<?> configClass)
            throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Field handlerField = configClass.getDeclaredField("HANDLER");
        Object handler = handlerField.get(null); // 静态字段

        Method instanceMethod = handler.getClass().getMethod("instance");
        Object configInstance = instanceMethod.invoke(handler);

        Field cooldownField = configInstance.getClass().getDeclaredField("StartingCooldown");
        return cooldownField.getInt(configInstance);
    }

    public static void resetEvents() {
        ResetPlayerEvent.EVENT.register(player -> {
            AvengerComponent.KEY.get(player).reset();
        });
    }

    public static void init() {

        resetEvents();

        setDefaultEvents();

        registerRolesAbility();
    }
}
