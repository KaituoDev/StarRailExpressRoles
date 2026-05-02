package fun.kaituo.starrailexpressroles.client;

import net.fabricmc.api.ClientModInitializer;

public class StarRailExpressrolesClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        InitializeClient.initializeAll();
    }
}
