package fun.kaituo.starrailexpressroles.client;

import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import fun.kaituo.starrailexpressroles.StarRailExpressRoles;
import fun.kaituo.starrailexpressroles.packet.host.AbilityC2SPacket;
import fun.kaituo.starrailexpressroles.roles.RolesManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.agmas.noellesroles.client.NoellesrolesClient;
import org.lwjgl.glfw.GLFW;

public class InitializeClient {

    public static KeyBinding abilityBind;
    public static long BLACKOUT_TIME = 0;

    /// 设置技能按键
    public static void registerAbilityKey() {
        if (FabricLoader.getInstance().isModLoaded("noellesroles")) {
            if (abilityBind == null) ClientTickEvents.START_CLIENT_TICK.register(client -> {
                abilityBind = NoellesrolesClient.abilityBind;
            });
        } else if (!FabricLoader.getInstance().isModLoaded("noellesroles") && FabricLoader.getInstance().isModLoaded("starexpress")) {
            abilityBind = KeyBindingHelper.registerKeyBinding(new KeyBinding("key." + StarRailExpressRoles.MOD_ID + ".ability", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "category.wathe.keybinds"));
        } else {
            abilityBind = KeyBindingHelper.registerKeyBinding(new KeyBinding("key." + StarRailExpressRoles.MOD_ID + ".ability", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G, "category.wathe.keybinds"));
        }
    }

    /// 设置有技能的角色
    public static void setRoleAbilityPackets() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (abilityBind == null) return;
            if (abilityBind.isPressed()) {
                client.execute(() -> {
                    if (MinecraftClient.getInstance().player == null) return;
                    GameWorldComponent gameWorld = GameWorldComponent.KEY.get(MinecraftClient.getInstance().player.getWorld());
                    boolean sendAbilityPacket = false;
                    Role[] rolesWithAbility = new Role[]{
                            RolesManager.CREEPER
                    };
                    for (Role role : rolesWithAbility) {
                        if (gameWorld.isRole(MinecraftClient.getInstance().player, role)) sendAbilityPacket = true;
                    }
                    if (!sendAbilityPacket) return;
                    ClientPlayNetworking.send(new AbilityC2SPacket());
                });
            }
        });
    }

    public static void initializeAll() {
        registerAbilityKey();

        setRoleAbilityPackets();
    }

}
