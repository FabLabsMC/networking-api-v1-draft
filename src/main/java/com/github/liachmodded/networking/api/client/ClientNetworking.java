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
package com.github.liachmodded.networking.api.client;

import com.github.liachmodded.networking.api.PacketChannelCallback;
import com.github.liachmodded.networking.api.PacketListenerCallback;
import com.github.liachmodded.networking.api.PacketReceiver;
import com.github.liachmodded.networking.api.PlayPacketSender;
import com.github.liachmodded.networking.api.util.Event;
import com.github.liachmodded.networking.impl.client.ClientNetworkingDetails;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;

public final class ClientNetworking {

	public static final Event<PacketListenerCallback<ClientPlayNetworkHandler>> PLAY_INITIALIZED = Event
			.createArrayBacked(PacketListenerCallback.class, callbacks -> handler -> {
				for (PacketListenerCallback<ClientPlayNetworkHandler> callback : callbacks) {
					callback.handle(handler);
				}
			});
	public static final Event<PacketListenerCallback<ClientPlayNetworkHandler>> PLAY_DISCONNECTED = Event
			.createArrayBacked(PacketListenerCallback.class, callbacks -> handler -> {
				for (PacketListenerCallback<ClientPlayNetworkHandler> callback : callbacks) {
					callback.handle(handler);
				}
			});
	public static final Event<PacketChannelCallback<ClientPlayNetworkHandler>> CHANNEL_REGISTERED = Event
			.createArrayBacked(PacketChannelCallback.class, callbacks -> (handler, channels) -> {
				for (PacketChannelCallback<ClientPlayNetworkHandler> callback : callbacks) {
					callback.handle(handler, channels);
				}
			});
	public static final Event<PacketChannelCallback<ClientPlayNetworkHandler>> CHANNEL_UNREGISTERED = Event
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

	public static PacketReceiver<PlayS2CContext> getPlayReceiver() {
		return ClientNetworkingDetails.PLAY;
	}

	public static PacketReceiver<LoginS2CContext> getLoginReceiver() {
		return ClientNetworkingDetails.LOGIN;
	}
}
