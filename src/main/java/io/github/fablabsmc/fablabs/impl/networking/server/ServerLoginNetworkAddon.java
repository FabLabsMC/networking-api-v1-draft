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

package io.github.fablabsmc.fablabs.impl.networking.server;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import io.github.fablabsmc.fablabs.api.networking.v1.PacketSender;
import io.github.fablabsmc.fablabs.api.networking.v1.server.ServerLoginContext;
import io.github.fablabsmc.fablabs.api.networking.v1.server.ServerNetworking;
import io.github.fablabsmc.fablabs.api.networking.v1.util.PacketByteBufs;
import io.github.fablabsmc.fablabs.impl.networking.AbstractNetworkAddon;
import io.github.fablabsmc.fablabs.impl.networking.NetworkingDetails;
import io.github.fablabsmc.fablabs.mixin.networking.access.LoginQueryRequestS2CPacketAccess;
import io.github.fablabsmc.fablabs.mixin.networking.access.LoginQueryResponseC2SPacketAccess;
import io.github.fablabsmc.fablabs.mixin.networking.access.ServerLoginNetworkHandlerAccess;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginCompressionS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.util.Identifier;

public final class ServerLoginNetworkAddon extends AbstractNetworkAddon<ServerLoginContext> {

	private final ServerLoginNetworkHandler handler;
	private final MinecraftServer server;
	private final QueryIdFactory queryIdFactory;
	private final Collection<Future<?>> waits = new ConcurrentLinkedQueue<>();
	private final Map<Integer, Identifier> channels = new ConcurrentHashMap<>();
	private boolean firstQueryTick = true;

	public ServerLoginNetworkAddon(ServerLoginNetworkHandler handler) {
		super(ServerNetworkingDetails.LOGIN, handler.connection);
		this.handler = handler;
		this.server = ((ServerLoginNetworkHandlerAccess) handler).getServer();
		this.queryIdFactory = NetworkingDetails.createQueryIdManager();
	}

	// return true if no longer ticks query
	public boolean queryTick() {
		if (this.firstQueryTick) {
			this.sendCompressionPacket();
			ServerNetworking.LOGIN_QUERY_START.invoker().handle(this.handler);
			this.firstQueryTick = false;
		}

		AtomicReference<Throwable> error = new AtomicReference<>();
		this.waits.removeIf(future -> {
			if (!future.isDone()) {
				return false;
			}

			try {
				future.get();
			} catch (ExecutionException ex) {
				Throwable caught = ex.getCause();
				error.getAndUpdate(oldEx -> {
					if (oldEx == null) {
						return caught;
					}

					oldEx.addSuppressed(caught);
					return oldEx;
				});
			} catch (InterruptedException | CancellationException ignored) {
			}

			return true;
		});
		return this.channels.isEmpty() && this.waits.isEmpty();
	}

	private void sendCompressionPacket() {
		if (this.server.getNetworkCompressionThreshold() >= 0 && !this.connection.isLocal()) {
			this.connection.send(new LoginCompressionS2CPacket(this.server.getNetworkCompressionThreshold()), (channelFuture) -> {
				this.connection.setCompressionThreshold(this.server.getNetworkCompressionThreshold());
			});
		}
	}

	public boolean handle(LoginQueryResponseC2SPacket packet) {
		LoginQueryResponseC2SPacketAccess access = (LoginQueryResponseC2SPacketAccess) packet;
		return handle(access.getQueryId(), access.getResponse());
	}

	private boolean handle(int queryId, PacketByteBuf originalBuf) {
		Identifier channel = channels.remove(queryId);
		if (channel == null) {
			NetworkingDetails.LOGGER.warn("Query ID {} was received but no channel has been associated in {}!", queryId, this.connection);
			return false;
		}

		boolean understood = originalBuf != null;
		return this.handle(channel, understood ? originalBuf : PacketByteBufs.empty(), new Context(queryId, understood));
	}

	@Override
	public Packet<?> makePacket(Identifier channel, PacketByteBuf buf) {
		int queryId = queryIdFactory.nextId();
		channels.put(queryId, channel);
		LoginQueryRequestS2CPacket ret = new LoginQueryRequestS2CPacket();
		LoginQueryRequestS2CPacketAccess access = (LoginQueryRequestS2CPacketAccess) ret;
		access.setQueryId(queryId);
		access.setChannel(channel);
		access.setPayload(buf);
		return ret;
	}

	final class Context implements ServerLoginContext {
		private final int queryId;
		private final boolean understood;

		Context(int queryId, boolean understood) {
			this.queryId = queryId;
			this.understood = understood;
		}

		@Override
		public ServerLoginNetworkHandler getListener() {
			return ServerLoginNetworkAddon.this.handler;
		}

		@Override
		public PacketSender getPacketSender() {
			return ServerLoginNetworkAddon.this;
		}

		@Override
		public int getQueryId() {
			return this.queryId;
		}

		@Override
		public boolean isUnderstood() {
			return this.understood;
		}

		@Override
		public void waitFor(Future<?> future) {
			ServerLoginNetworkAddon.this.waits.add(future);
		}

		@Override
		public MinecraftServer getEngine() {
			return ServerLoginNetworkAddon.this.server;
		}
	}
}
