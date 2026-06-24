package fun.kaituo.starrailexpressroles.misc;

import dev.doctor4t.wathe.game.GameConstants;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import fun.kaituo.starrailexpressroles.StarRailExpressRoles;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

public class StarRailExpressRolesConfig {

    public static ConfigClassHandler<StarRailExpressRolesConfig> HANDLER = ConfigClassHandler
            .createBuilder(StarRailExpressRolesConfig.class)
            .id(Identifier.of(StarRailExpressRoles.MOD_ID, "config"))
            .serializer(config ->
                    GsonConfigSerializerBuilder
                            .create(config)
                            .setPath(FabricLoader.getInstance().getConfigDir().resolve(StarRailExpressRoles.MOD_ID + ".json5"))
                            .setJson5(true)
                            .build()
            )
            .build();

    /// Roles
    // Creeper
    @SerialEntry
    public int CreeperExplodePrice = 200;
    @SerialEntry
    public int CreeperExplodeCooldown = GameConstants.getInTicks(1,0) / 20;
    @SerialEntry
    public int CreeperExplodeChargeDuration = GameConstants.getInTicks(0,3) / 20;
}
