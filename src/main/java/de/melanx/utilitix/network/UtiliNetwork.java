package de.melanx.utilitix.network;

import io.github.noeppi_noeppi.libx.mod.ModX;
import io.github.noeppi_noeppi.libx.network.NetworkX;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.PacketDistributor;

public class UtiliNetwork extends NetworkX {

    public UtiliNetwork(ModX mod) {
        super(mod);
    }

    @Override
    protected String getProtocolVersion() {
        return "1";
    }

    @Override
    protected void registerPackets() {
        this.register(new ItemEntityRepairedHandler.Serializer(), () -> ItemEntityRepairedHandler::handle, NetworkDirection.PLAY_TO_CLIENT);
    }

    public void updateItemEntityDamage(ServerPlayerEntity player, ItemEntity entity) {
        this.instance.send(PacketDistributor.PLAYER.with(() -> player), new ItemEntityRepairedHandler.Message(entity.getUniqueID()));
    }
}
