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
package io.github.fablabsmc.fablabs.api.networking.v1.client;

import io.github.fablabsmc.fablabs.api.networking.v1.PacketChannelCallback;
import io.github.fablabsmc.fablabs.api.networking.v1.PacketListenerCallback;
import io.github.fablabsmc.fablabs.api.networking.v1.PacketReceiver;
import io.github.fablabsmc.fablabs.api.networking.v1.PlayPacketSender;
import io.github.fablabsmc.fablabs.impl.networking.client.ClientNetworkingDetails;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Offers access to client-side networking functionalities.
 * 
 * <p>Client-side networking functionalities include receiving clientbound packets,
 * sending serverbound packets, and events related to client-side network handlers.</p>
 * 
 * <p>This class should be only used on the physical client and for the logical client.</p>
 * 
 * @see io.github.fablabsmc.fablabs.api.networking.v1.server.ServerNetworking
 */
public final class ClientNetworking {

	public static final Event<PacketListenerCallback<ClientPlayNetworkHandler>> PLAY_INITIALIZED = EventFactory
			.createArrayBacked(PacketListenerCallback.class, callbacks -> handler -> {
				for (PacketListenerCallback<ClientPlayNetworkHandler> callback : callbacks) {
					callback.handle(handler);
				}
			});
	public static final Event<PacketListenerCallback<ClientPlayNetworkHandler>> PLAY_DISCONNECTED = EventFactory
			.createArrayBacked(PacketListenerCallback.class, callbacks -> handler -> {
				for (PacketListenerCallback<ClientPlayNetworkHandler> callback : callbacks) {
					callback.handle(handler);
				}
			});
	public static final Event<PacketChannelCallback<ClientPlayNetworkHandler>> CHANNEL_REGISTERED = EventFactory
			.createArrayBacked(PacketChannelCallback.class, callbacks -> (handler, channels) -> {
				for (PacketChannelCallback<ClientPlayNetworkHandler> callback : callbacks) {
					callback.handle(handler, channels);
				}
			});
	public static final Event<PacketChannelCallback<ClientPlayNetworkHandler>> CHANNEL_UNREGISTERED = EventFactory
			.createArrayBacked(PacketChannelCallback.class, callbacks -> (handler, channels) -> {
				for (PacketChannelCallback<ClientPlayNetworkHandler> callback : callbacks) {
					callback.handle(handler, channels);
				}
			});

	public static PlayPacketSender getPlaySender() throws IllegalStateException {
		ClientPlayerEntity player = MinecraftClient.getInstance().player;

		if (player == null) {
			throw new IllegalStateException("Cannot get packet sender when not in game!");
		}

		return getPlaySender(player.networkHandler);
	}

	public static PlayPacketSender getPlaySender(ClientPlayNetworkHandler handler) {
		return ClientNetworkingDetails.getAddon(handler);
	}

	public static PacketReceiver<ClientPlayContext> getPlayReceiver() {
		return ClientNetworkingDetails.PLAY;
	}

	public static PacketReceiver<ClientLoginContext> getLoginReceiver() {
		return ClientNetworkingDetails.LOGIN;
	}
}
