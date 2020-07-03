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

package io.github.fablabsmc.fablabs.api.networking.v1;

import java.util.List;
import java.util.concurrent.Future;

import io.github.fablabsmc.fablabs.impl.networking.server.ServerLoginNetworkHandlerHook;
import io.github.fablabsmc.fablabs.impl.networking.server.ServerNetworkingDetails;
import io.github.fablabsmc.fablabs.impl.networking.server.ServerPlayNetworkHandlerHook;
import io.github.fablabsmc.fablabs.mixin.networking.access.ServerLoginNetworkHandlerAccess;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Offers access to server-side networking functionalities.
 *
 * <p>Server-side networking functionalities include receiving serverbound packets,
 * sending clientbound packets, and events related to server-side network handlers.</p>
 *
 * <p>This class should be only used for the logical server.</p>
 *
 * @see ClientNetworking
 */
public final class ServerNetworking {
	/**
	 * An event for the server play network handler receiving an update indicating
	 * the connected client's ability to receive packets in certain channels.
	 *
	 * @see PlayPacketSender#hasChannel(Identifier)
	 */
	public static final Event<ChannelRegisteredCallback> CHANNEL_REGISTERED = EventFactory
			.createArrayBacked(ChannelRegisteredCallback.class, callbacks -> (handler, server, sender, channels) -> {
				for (ChannelRegisteredCallback callback : callbacks) {
					callback.onChannelRegistered(handler, server, sender, channels);
				}
			});

	@FunctionalInterface
	public interface ChannelRegisteredCallback {
		void onChannelRegistered(ServerPlayNetworkHandler handler, MinecraftServer server, PlayPacketSender sender, List<Identifier> channels);
	}

	/**
	 * An event for the server play network handler receiving an update indicating
	 * the connected client's lack of ability to receive packets in certain channels.
	 *
	 * @see PlayPacketSender#hasChannel(Identifier)
	 */
	public static final Event<ChannelUnregisteredCallback> CHANNEL_UNREGISTERED = EventFactory
			.createArrayBacked(ChannelUnregisteredCallback.class, callbacks -> (handler, server, sender, channels) -> {
				for (ChannelUnregisteredCallback callback : callbacks) {
					callback.onChannelUnregistered(handler, server, sender, channels);
				}
			});

	@FunctionalInterface
	public interface ChannelUnregisteredCallback {
		void onChannelUnregistered(ServerPlayNetworkHandler handler, MinecraftServer server, PlayPacketSender sender, List<Identifier> channels);
	}

	/**
	 * An event for the initialization of the server play network handler.
	 *
	 * <p>At this stage, the network handler is ready to send packets to the client. Use
	 * {@link #getPlaySender(ServerPlayNetworkHandler)} to obtain the packet sender in the
	 * callback.</p>
	 */
	public static final Event<PlayInitializedCallback> PLAY_INITIALIZED = EventFactory
			.createArrayBacked(PlayInitializedCallback.class, callbacks -> (handler, server, sender) -> {
				for (PlayInitializedCallback callback : callbacks) {
					callback.onPlayInitialized(handler, server, sender);
				}
			});

	@FunctionalInterface
	public interface PlayInitializedCallback {
		void onPlayInitialized(ServerPlayNetworkHandler handler, MinecraftServer server, PlayPacketSender sender);
	}

	/**
	 * An event for the disconnection of the server play network handler.
	 *
	 * <p>No packets should be sent when this event is invoked.</p>
	 */
	public static final Event<PlayDisconnectedCallback> PLAY_DISCONNECTED = EventFactory
			.createArrayBacked(PlayDisconnectedCallback.class, callbacks -> (handler, server) -> {
				for (PlayDisconnectedCallback callback : callbacks) {
					callback.onPlayDisconnected(handler, server);
				}
			});

	@FunctionalInterface
	public interface PlayDisconnectedCallback {
		void onPlayDisconnected(ServerPlayNetworkHandler handler, MinecraftServer server);
	}

	/**
	 * An event for the start of login queries of the server login network handler.
	 *
	 * <p>Use {@link #getLoginSender(ServerLoginNetworkHandler)} to obtain the query request
	 * packet sender in the callback.</p>
	 */
	public static final Event<LoginQueryStartCallback> LOGIN_QUERY_START = EventFactory
			.createArrayBacked(LoginQueryStartCallback.class, callbacks -> (handler, server, sender, synchronizer) -> {
				for (LoginQueryStartCallback callback : callbacks) {
					callback.onLoginStart(handler, server, sender, synchronizer);
				}
			});

	@FunctionalInterface
	public interface LoginQueryStartCallback {
		void onLoginStart(ServerLoginNetworkHandler handler, MinecraftServer server, PacketSender sender, LoginSynchronizer synchronizer);
	}

	/**
	 * An event for the disconnection of the server login network handler.
	 *
	 * <p>No packets should be sent when this event is invoked.</p>
	 */
	public static final Event<LoginDisconnectCallback> LOGIN_DISCONNECTED = EventFactory
			.createArrayBacked(LoginDisconnectCallback.class, callbacks -> (handler, server) -> {
				for (LoginDisconnectCallback callback : callbacks) {
					callback.onLoginDisconnected(handler, server);
				}
			});

	@FunctionalInterface
	public interface LoginDisconnectCallback {
		void onLoginDisconnected(ServerLoginNetworkHandler handler, MinecraftServer server);
	}

	/**
	 * Returns the packet receiver for channel handler registration on server play network
	 * handlers, receiving {@link net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket
	 * client to server custom payload packets}.
	 */
	public static PacketReceiver<PlayChannelHandler> getPlayReceiver() {
		return ServerNetworkingDetails.PLAY;
	}

	@FunctionalInterface
	public interface PlayChannelHandler {
		void receive(ServerPlayNetworkHandler handler, MinecraftServer server, PlayPacketSender sender, PacketByteBuf buf);
	}

	/**
	 * Returns the packet receiver for channel handler registration on server play network
	 * handlers, receiving {@link net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket
	 * login query response packets}.
	 */
	public static PacketReceiver<LoginChannelHandler> getLoginReceiver() {
		return ServerNetworkingDetails.LOGIN;
	}

	@FunctionalInterface
	public interface LoginChannelHandler {
		void receive(ServerLoginNetworkHandler handler, MinecraftServer server, PacketSender sender, PacketByteBuf buf, boolean understood, LoginSynchronizer synchronizer);
	}

	/**
	 * Returns the packet sender for a server play network handler.
	 *
	 * @param handler a server play network handler
	 * @return the associated packet sender
	 */
	public static PlayPacketSender getPlaySender(ServerPlayNetworkHandler handler) {
		return ((ServerPlayNetworkHandlerHook) handler).getAddon();
	}

	/**
	 * Returns the login query packet sender for a server login network handler.
	 *
	 * @param handler the server login network handler
	 * @return the associated login query packet sender
	 */
	public static PacketSender getLoginSender(ServerLoginNetworkHandler handler) {
		return ((ServerLoginNetworkHandlerHook) handler).getAddon();
	}

	/**
	 * Returns the packet sender for a server player.
	 *
	 * <p>This is a shortcut for {@link #getPlaySender(ServerPlayNetworkHandler)}.</p>
	 *
	 * @param player a server player
	 * @return the associated packet sender
	 */
	public static PlayPacketSender getPlaySender(ServerPlayerEntity player) {
		return getPlaySender(player.networkHandler);
	}

	/**
	 * Returns the <i>Minecraft</i> Server of a server play network handler.
	 *
	 * @param handler the server play network handler
	 */
	public static MinecraftServer getServer(ServerPlayNetworkHandler handler) {
		return handler.player.server;
	}

	/**
	 * Returns the <i>Minecraft</i> Server of a server login network handler.
	 *
	 * @param handler the server login network handler
	 */
	public static MinecraftServer getServer(ServerLoginNetworkHandler handler) {
		return ((ServerLoginNetworkHandlerAccess) handler).getServer();
	}

	// Not intended to be implemented by users
	public interface LoginSynchronizer {
		/**
		 * Allows blocking client log-in until the {@code future} is {@link Future#isDone() done}.
		 *
		 * <p>Since packet reception happens on netty's event loops, this allows handlers to
		 * perform logic on the Server Thread, etc. For instance, a handler can prepare an
		 * upcoming query request or check necessary login data on the server thread.</p>
		 *
		 * <p>Here is an example where the player log-in is blocked so that a credential check and
		 * building of a followup query request can be performed properly on the logical server
		 * thread before the player successfully logs in:
		 * <pre><blockquote>
		 *     ServerNetworking.getLoginReceiver().register(CHECK_CHANNEL, (context, buf) -&gt; {
		 *         if (!context.isUnderstood()) {
		 *             handler.disconnect(new LiteralText("Only accept clients that can check!"));
		 *             return;
		 *         }
		 *         String checkMessage = buf.readString(32767);
		 *         ServerLoginNetworkHandler handler = context.getPacketListener();
		 *         PacketSender sender = context.getPacketSender();
		 *         MinecraftServer server = context.getEngine();
		 *         // Just send the CompletableFuture returned by the server's submit method
		 *         context.waitFor(server.submit(() -&gt; {
		 *             LoginInfoChecker checker = LoginInfoChecker.get(server);
		 *             if (!checker.check(handler.getConnectionInfo(), checkMessage)) {
		 *                 handler.disconnect(new LiteralText("Invalid credentials!"));
		 *                 return;
		 *             }
		 *             sender.send(UPCOMING_CHECK, checker.buildSecondQueryPacket(handler, checkMessage));
		 *         }));
		 *     });
		 * </blockquote></pre>
		 * Usually it is enough to pass the return value for {@link net.minecraft.util.thread.ThreadExecutor#submit(Runnable)}
		 * for {@code future}.</p>
		 *
		 * @param future the future that must be done before the player can log in
		 */
		void waitFor(Future<?> future);
	}
}
