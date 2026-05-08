package fun.kaituo.starrailexpressroles.packet;

import fun.kaituo.starrailexpressroles.packet.host.AbilityC2SPacket;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class PacketManager {

    /// 注册网络数据包
    public static void registerPackets() {
        PayloadTypeRegistry.playC2S().register(AbilityC2SPacket.ID, AbilityC2SPacket.CODEC);
    }
}
