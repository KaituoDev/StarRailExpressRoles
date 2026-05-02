package fun.kaituo.starrailexpressroles.client;

import fun.kaituo.starrailexpressroles.StarRailExpressRoles;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
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

    public static void initializeAll() {
        registerAbilityKey();
        initializeRenders();
    }

    private static void initializeRenders() {

    }

}
