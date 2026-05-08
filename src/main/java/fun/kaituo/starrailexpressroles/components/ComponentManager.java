package fun.kaituo.starrailexpressroles.components;

import fun.kaituo.starrailexpressroles.roles.avenger.AvengerComponent;
import fun.kaituo.starrailexpressroles.roles.creeper.CreeperComponent;
import net.minecraft.entity.player.PlayerEntity;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;

public class ComponentManager implements EntityComponentInitializer {

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry entityComponentFactoryRegistry) {
        entityComponentFactoryRegistry.beginRegistration(
                PlayerEntity.class,
                AbilityPlayerComponent.KEY
        ).respawnStrategy(RespawnCopyStrategy.NEVER_COPY).end(AbilityPlayerComponent::new);

        entityComponentFactoryRegistry.beginRegistration(
                PlayerEntity.class,
                AvengerComponent.KEY
        ).respawnStrategy(RespawnCopyStrategy.NEVER_COPY).end(AvengerComponent::new);
        entityComponentFactoryRegistry.beginRegistration(
                PlayerEntity.class,
                CreeperComponent.KEY
        ).respawnStrategy(RespawnCopyStrategy.NEVER_COPY).end(CreeperComponent::new);
    }
}
