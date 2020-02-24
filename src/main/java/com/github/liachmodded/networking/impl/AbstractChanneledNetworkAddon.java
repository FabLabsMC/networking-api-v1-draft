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
package com.github.liachmodded.networking.impl;

import com.github.liachmodded.networking.api.PlayContext;
import com.github.liachmodded.networking.api.PlayPacketSender;
import com.github.liachmodded.networking.api.util.PacketByteBufs;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.AsciiString;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.PacketByteBuf;

import java.nio.charset.StandardCharsets;
import java.util.*;

// play
public abstract class AbstractChanneledNetworkAddon<C extends PlayContext> extends AbstractNetworkAddon<C> implements PlayPacketSender {

	protected final Set<Identifier> sendableChannels;
	protected final Set<Identifier> sendableChannelsView;

	protected AbstractChanneledNetworkAddon(BasicPacketReceiver<C> receiver, ClientConnection connection) {
		this(receiver, connection, new HashSet<>());
	}

	protected AbstractChanneledNetworkAddon(BasicPacketReceiver<C> receiver, ClientConnection connection, Set<Identifier> sendableChannels) {
		super(receiver, connection);
		this.sendableChannels = sendableChannels;
		this.sendableChannelsView = Collections.unmodifiableSet(sendableChannels);
	}

	@Override
	protected boolean handle(Identifier channel, PacketByteBuf originalBuf, C context) {
		if (CartNetworkingDetails.REGISTER_CHANNEL.equals(channel)) {
			receiveRegistration(true, PacketByteBufs.slice(originalBuf));
		}
		if (CartNetworkingDetails.UNREGISTER_CHANNEL.equals(channel)) {
			receiveRegistration(false, PacketByteBufs.slice(originalBuf));
		}
		return super.handle(channel, originalBuf, context);
	}

	public void sendRegistration() {
		PacketByteBuf buf = PacketByteBufs.create();

		boolean first = true;
		for (Identifier channel : this.receiver.getChannels()) {
			if (first) {
				first = false;
			} else {
				buf.writeByte(0);
			}
			buf.writeBytes(channel.toString().getBytes(StandardCharsets.US_ASCII));
		}

		sendPacket(CartNetworkingDetails.REGISTER_CHANNEL, buf, (ChannelFutureListener) future -> buf.release());
	}

	// wrap in try with res (buf)
	protected void receiveRegistration(boolean register, PacketByteBuf buf) {
		List<Identifier> ids = new ArrayList<>();

		StringBuilder active = new StringBuilder();
		while (buf.isReadable()) {
			byte b = buf.readByte();
			if (b != 0) {
				active.append(AsciiString.b2c(b));
			} else {
				addId(ids, active);
				active = new StringBuilder();
			}
		}
		addId(ids, active);

		if (register) {
			register(ids);
		} else {
			unregister(ids);
		}
	}

	public void register(List<Identifier> ids) {
		this.sendableChannels.addAll(ids);
		postRegisterEvent(ids);
	}

	public void unregister(List<Identifier> ids) {
		this.sendableChannels.removeAll(ids);
		postUnregisterEvent(ids);
	}

	protected abstract Packet<?> makeUncheckedPacket(Identifier channel, PacketByteBuf buf);

	protected abstract void postRegisterEvent(List<Identifier> ids);

	protected abstract void postUnregisterEvent(List<Identifier> ids);

	private void addId(List<Identifier> ids, StringBuilder sb) {
		String literal = sb.toString();
		try {
			ids.add(new Identifier(literal));
		} catch (InvalidIdentifierException ex) {
			CartNetworkingDetails.LOGGER.warn("Received invalid channel identifier \"{}\" from connection {}", literal, this.connection, ex);
		}
	}

	@Override
	public Collection<Identifier> getChannels() {
		return this.sendableChannelsView;
	}

	@Override
	public boolean hasChannel(Identifier channel) {
		return this.sendableChannels.contains(channel);
	}

	@Override
	protected Packet<?> makePacket(Identifier channel, PacketByteBuf buf) {
		if (CartNetworkingDetails.WARN_UNREGISTERED_PACKETS && !hasChannel(channel)) {
			CartNetworkingDetails.LOGGER.warn("Packet sent to unregistered channel \"{}\" on {}!", channel, this.connection);
		}
		return makeUncheckedPacket(channel, buf);
	}
}
