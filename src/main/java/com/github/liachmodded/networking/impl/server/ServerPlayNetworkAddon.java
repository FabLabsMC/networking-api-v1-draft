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
package com.github.liachmodded.networking.impl.server;

import com.github.liachmodded.networking.api.PlayPacketSender;
import com.github.liachmodded.networking.api.server.PlayC2SContext;
import com.github.liachmodded.networking.api.server.ServerNetworking;
import com.github.liachmodded.networking.impl.AbstractChanneledNetworkAddon;
import com.github.liachmodded.networking.mixin.access.CustomPayloadC2SPacketAccess;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

import java.util.List;

public final class ServerPlayNetworkAddon extends AbstractChanneledNetworkAddon<PlayC2SContext> implements PlayC2SContext {
	
	private final ServerPlayNetworkHandler handler;
	
	public ServerPlayNetworkAddon(ServerPlayNetworkHandler handler) {
		super(ServerNetworkingDetails.PLAY, handler.getConnection());
		this.handler = handler;
	}

	// also expose sendRegistration
	
	public boolean handle(CustomPayloadC2SPacket packet) {
		CustomPayloadC2SPacketAccess access = (CustomPayloadC2SPacketAccess) packet;
		return handle(access.getChannel(), access.getData(), this);
	}
	
	// impl details

	@Override protected void schedule(Runnable task) {
		this.handler.player.server.execute(task);
	}

	@Override
	protected Packet<?> makeUncheckedPacket(Identifier channel, PacketByteBuf buf) {
		return new CustomPayloadS2CPacket(channel, buf);
	}

	@Override
	protected void postRegisterEvent(List<Identifier> ids) {
		ServerNetworking.CHANNEL_REGISTERED.invoker().handle(handler, ids);
	}

	@Override
	protected void postUnregisterEvent(List<Identifier> ids) {
		ServerNetworking.CHANNEL_UNREGISTERED.invoker().handle(handler, ids);
	}
	
	// context

	@Override
	public ServerPlayerEntity getPlayer() {
		return this.handler.player;
	}

	@Override
	public ServerPlayNetworkHandler getListener() {
		return this.handler;
	}

	@Override
	public PlayPacketSender getPacketSender() {
		return this;
	}

	@Override
	public MinecraftServer getEngine() {
		return this.handler.player.server;
	}
}
