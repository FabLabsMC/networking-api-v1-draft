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

import io.github.fablabsmc.fablabs.api.networking.v1.client.ClientLoginContext;
import io.github.fablabsmc.fablabs.impl.networking.ReceivingNetworkAddon;
import io.github.fablabsmc.fablabs.mixin.networking.access.LoginQueryRequestS2CPacketAccess;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket;
import net.minecraft.util.Identifier;

public final class ClientLoginNetworkAddon extends ReceivingNetworkAddon<ClientLoginContext> {

	private final ClientLoginNetworkHandler handler;

	public ClientLoginNetworkAddon(ClientLoginNetworkHandler handler) {
		super(ClientNetworkingDetails.LOGIN);
		this.handler = handler;
	}

	public boolean handlePacket(LoginQueryRequestS2CPacket packet) {
		LoginQueryRequestS2CPacketAccess access = (LoginQueryRequestS2CPacketAccess) packet;
		return handlePacket(packet.getQueryId(), access.getChannel(), access.getPayload());
	}

	private boolean handlePacket(int queryId, Identifier channel, PacketByteBuf originalBuf) {
		try (Context context = new Context(queryId)) {
			return handle(channel, originalBuf, context);
		}
	}

	final class Context implements ClientLoginContext, AutoCloseable {

		private final int queryId;
		private boolean responded;

		Context(int queryId) {
			this.queryId = queryId;
			this.responded = false;
		}

		@Override
		public void close() {
			if (!responded && handler.getConnection().isOpen()) {
				respond((PacketByteBuf) null);
			}
		}

		@Override
		public ClientLoginNetworkHandler getListener() {
			return ClientLoginNetworkAddon.this.handler;
		}

		@Override
		public int getQueryId() {
			return this.queryId;
		}

		@Override
		public void respond(PacketByteBuf buf) {
			respond(buf, null);
		}

		@Override
		public void respond(CompletableFuture<? extends PacketByteBuf> future) {
			respond(future, null);
		}

		@Override
		public void respond(PacketByteBuf buf, GenericFutureListener<? extends Future<? super Void>> callback) {
			handler.getConnection().send(buildPacket(buf), callback);
			this.responded = true;
		}

		@Override
		public void respond(CompletableFuture<? extends PacketByteBuf> future, GenericFutureListener<? extends Future<? super Void>> callback) {
			ClientConnection connection = handler.getConnection();
			future.whenCompleteAsync((buf, ex) -> {
				if (ex != null || buf == null) {
					throw new RuntimeException(ex);
					// todo how to handle?
				}

				connection.send(buildPacket(buf), callback);
			});
			this.responded = true;
		}

		@Override
		public MinecraftClient getEngine() {
			return MinecraftClient.getInstance(); // may need update in the future?
		}

		private LoginQueryResponseC2SPacket buildPacket(PacketByteBuf buf) {
			return new LoginQueryResponseC2SPacket(this.queryId, buf);
		}
	}
}
