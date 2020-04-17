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

import java.util.List;

import io.github.fablabsmc.fablabs.api.networking.v1.PlayPacketSender;
import io.github.fablabsmc.fablabs.api.networking.v1.client.ClientNetworking;
import io.github.fablabsmc.fablabs.api.networking.v1.client.ClientPlayContext;
import io.github.fablabsmc.fablabs.impl.networking.AbstractChanneledNetworkAddon;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;

public final class ClientPlayNetworkAddon extends AbstractChanneledNetworkAddon<ClientPlayContext> implements ClientPlayContext {

	private final ClientPlayNetworkHandler handler;

	public ClientPlayNetworkAddon(ClientPlayNetworkHandler handler) {
		super(ClientNetworkingDetails.PLAY, handler.getConnection());
		this.handler = handler;
	}

	// also expose sendRegistration

	public void onServerReady() {
		sendRegistration();
		ClientNetworking.PLAY_INITIALIZED.invoker().handle(this.handler);
	}

	public boolean handle(CustomPayloadS2CPacket packet) {
		PacketByteBuf buf = packet.getData();
		try {
			return handle(packet.getChannel(), buf, this);
		} finally {
			buf.release();
		}
	}

	// impl details

	@Override
	protected void schedule(Runnable task) {
		MinecraftClient.getInstance().execute(task);
	}

	@Override
	protected Packet<?> makeUncheckedPacket(Identifier channel, PacketByteBuf buf) {
		return new CustomPayloadC2SPacket(channel, buf);
	}

	@Override
	protected void postRegisterEvent(List<Identifier> ids) {
		ClientNetworking.CHANNEL_REGISTERED.invoker().handle(handler, ids);
	}

	@Override
	protected void postUnregisterEvent(List<Identifier> ids) {
		ClientNetworking.CHANNEL_UNREGISTERED.invoker().handle(handler, ids);
	}

	// context stuff

	@Override
	public ClientPlayerEntity getPlayer() {
		return MinecraftClient.getInstance().player;
	}

	@Override
	public ClientPlayNetworkHandler getListener() {
		return this.handler;
	}

	@Override
	public PlayPacketSender getPacketSender() {
		return this;
	}

	@Override
	public MinecraftClient getEngine() {
		return MinecraftClient.getInstance();
	}
}
