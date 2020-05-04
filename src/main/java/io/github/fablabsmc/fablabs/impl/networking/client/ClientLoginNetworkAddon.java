/*
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <http://unlicense.org>
 */

package io.github.fablabsmc.fablabs.impl.networking.client;

import java.util.concurrent.CompletableFuture;

import io.github.fablabsmc.fablabs.api.networking.v1.client.ClientLoginChannelHandler;
import io.github.fablabsmc.fablabs.api.networking.v1.client.ClientLoginContext;
import io.github.fablabsmc.fablabs.api.networking.v1.util.PacketByteBufs;
import io.github.fablabsmc.fablabs.impl.networking.NetworkingDetails;
import io.github.fablabsmc.fablabs.mixin.networking.access.LoginQueryRequestS2CPacketAccess;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket;
import net.minecraft.util.Identifier;

public final class ClientLoginNetworkAddon implements ClientLoginContext {
	private final ClientLoginNetworkHandler handler;

	public ClientLoginNetworkAddon(ClientLoginNetworkHandler handler) {
		this.handler = handler;
	}

	public boolean handlePacket(LoginQueryRequestS2CPacket packet) {
		LoginQueryRequestS2CPacketAccess access = (LoginQueryRequestS2CPacketAccess) packet;
		return handlePacket(packet.getQueryId(), access.getChannel(), access.getPayload());
	}

	private boolean handlePacket(int queryId, Identifier channel, PacketByteBuf originalBuf) {
		ClientLoginChannelHandler handler = ClientNetworkingDetails.LOGIN.get(channel);

		if (handler == null) {
			return false;
		}

		PacketByteBuf buf = PacketByteBufs.slice(originalBuf);

		try {
			CompletableFuture<ClientLoginChannelHandler.Response> future = handler.receive(this, buf);
			future.thenAccept(result -> {
				LoginQueryResponseC2SPacket packet = new LoginQueryResponseC2SPacket(queryId, result.getBuf());
				this.handler.getConnection().send(packet, (GenericFutureListener<? extends Future<? super Void>>) result.getListener());
			});
		} catch (Throwable ex) {
			NetworkingDetails.LOGGER.error("Encountered exception while handling in channel \"{}\"", channel, ex);
			throw ex;
		}

		return true;
	}

	@Override
	public ClientLoginNetworkHandler getListener() {
		return this.handler;
	}

	@Override
	public MinecraftClient getEngine() {
		return MinecraftClient.getInstance();
	}
}
