package fun.kaituo.starrailexpressroles;

import fun.kaituo.starrailexpressroles.misc.ServerTaskScheduler;
import fun.kaituo.starrailexpressroles.roles.RolesManager;
import net.fabricmc.api.ModInitializer;

public class StarRailExpressRoles implements ModInitializer {

    public static String MOD_ID = "starrailexpressroles";

    @Override
    public void onInitialize() {

        RolesManager.init();

        ServerTaskScheduler.init();

    }

}
