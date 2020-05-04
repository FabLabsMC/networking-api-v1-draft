package io.github.fablabsmc.fablabs.api.networking.v1.server;

import net.minecraft.network.PacketByteBuf;

@FunctionalInterface
public interface ServerLoginChannelHandler {
	void receive(ServerLoginContext context, PacketByteBuf buf, boolean understood);
}
