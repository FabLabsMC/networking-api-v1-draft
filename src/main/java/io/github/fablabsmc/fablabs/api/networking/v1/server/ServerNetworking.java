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

package io.github.fablabsmc.fablabs.api.networking.v1.server;

import io.github.fablabsmc.fablabs.api.networking.v1.PacketChannelCallback;
import io.github.fablabsmc.fablabs.api.networking.v1.PacketListenerCallback;
import io.github.fablabsmc.fablabs.api.networking.v1.PacketReceiver;
import io.github.fablabsmc.fablabs.api.networking.v1.PacketSender;
import io.github.fablabsmc.fablabs.api.networking.v1.PlayPacketSender;
import io.github.fablabsmc.fablabs.impl.networking.server.ServerLoginNetworkHandlerHook;
import io.github.fablabsmc.fablabs.impl.networking.server.ServerNetworkingDetails;
import io.github.fablabsmc.fablabs.impl.networking.server.ServerPlayNetworkHandlerHook;
import io.github.fablabsmc.fablabs.mixin.networking.access.ServerLoginNetworkHandlerAccess;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class ServerNetworking {

	public static final Event<PacketChannelCallback<ServerPlayNetworkHandler>> CHANNEL_REGISTERED = EventFactory
			.createArrayBacked(PacketChannelCallback.class, callbacks -> (handler, channels) -> {
				for (PacketChannelCallback<ServerPlayNetworkHandler> callback : callbacks) {
					callback.handle(handler, channels);
				}
			});
	public static final Event<PacketChannelCallback<ServerPlayNetworkHandler>> CHANNEL_UNREGISTERED = EventFactory
			.createArrayBacked(PacketChannelCallback.class, callbacks -> (handler, channels) -> {
				for (PacketChannelCallback<ServerPlayNetworkHandler> callback : callbacks) {
					callback.handle(handler, channels);
				}
			});
	public static final Event<PacketListenerCallback<ServerPlayNetworkHandler>> PLAY_INITIALIZED = EventFactory
			.createArrayBacked(PacketListenerCallback.class, callbacks -> handler -> {
				for (PacketListenerCallback<ServerPlayNetworkHandler> callback : callbacks) {
					callback.handle(handler);
				}
			});
	public static final Event<PacketListenerCallback<ServerPlayNetworkHandler>> PLAY_DISCONNECTED = EventFactory
			.createArrayBacked(PacketListenerCallback.class, callbacks -> handler -> {
				for (PacketListenerCallback<ServerPlayNetworkHandler> callback : callbacks) {
					callback.handle(handler);
				}
			});
	public static final Event<PacketListenerCallback<ServerLoginNetworkHandler>> LOGIN_QUERY_START = EventFactory
			.createArrayBacked(PacketListenerCallback.class, callbacks -> handler -> {
				for (PacketListenerCallback<ServerLoginNetworkHandler> callback : callbacks) {
					callback.handle(handler);
				}
			});
	public static final Event<PacketListenerCallback<ServerLoginNetworkHandler>> LOGIN_DISCONNECTED = EventFactory
			.createArrayBacked(PacketListenerCallback.class, callbacks -> handler -> {
				for (PacketListenerCallback<ServerLoginNetworkHandler> callback : callbacks) {
					callback.handle(handler);
				}
			});

	public static PacketReceiver<ServerPlayContext> getPlayReceiver() {
		return ServerNetworkingDetails.PLAY;
	}

	public static PacketReceiver<ServerLoginContext> getLoginReceiver() {
		return ServerNetworkingDetails.LOGIN;
	}

	public static PlayPacketSender getPlaySender(ServerPlayNetworkHandler handler) {
		return ((ServerPlayNetworkHandlerHook) handler).getAddon();
	}

	public static PacketSender getLoginSender(ServerLoginNetworkHandler handler) {
		return ((ServerLoginNetworkHandlerHook) handler).getAddon();
	}

	public static PlayPacketSender getPlaySender(ServerPlayerEntity player) {
		return getPlaySender(player.networkHandler);
	}

	public static MinecraftServer getServer(ServerPlayNetworkHandler handler) {
		return handler.player.server;
	}

	public static MinecraftServer getServer(ServerLoginNetworkHandler handler) {
		return ((ServerLoginNetworkHandlerAccess) handler).getServer();
	}

}
