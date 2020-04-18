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

import java.util.Collection;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

/**
 * Supports sending packets to channels in the play network handlers.
 *
 * <p>Compared to a simple packet sender, the play packet sender is informed
 * if its connected recipient may {@link #hasChannel(Identifier) accept packets
 * in certain channels}. When the {@code networking-api-v1-draft.warnUnregisteredPackets}
 * system property is absent or set to {@code true} and the recipient did not
 * declare its ability to receive packets in a channel a packet was sent in, a
 * warning is logged.</p>
 */
public interface PlayPacketSender extends PacketSender, ChannelAware {
	/**
	 * {@inheritDoc}
	 *
	 * <p>When the {@code networking-api-v1-draft.warnUnregisteredPackets} system
	 * property is absent or set to {@code true} and the {@code channel} is not
	 * {@linkplain #hasChannel(Identifier) registered}, a warning will be logged.</p>
	 *
	 * @param channel the id of the channel
	 * @param buf the content of the packet
	 */
	@Override
	void sendPacket(Identifier channel, PacketByteBuf buf);

	/**
	 * {@inheritDoc}
	 *
	 * <p>When the {@code networking-api-v1-draft.warnUnregisteredPackets} system
	 * property is absent or set to {@code true} and the {@code channel} is not
	 * {@linkplain #hasChannel(Identifier) registered}, a warning will be logged.</p>
	 *
	 * @param channel the id of the channel
	 * @param buf the content of the packet
	 * @param callback an optional callback to execute after the packet is sent, may be {@code null}
	 */
	@Override
	void sendPacket(Identifier channel, PacketByteBuf buf, /* Nullable */ GenericFutureListener<? extends Future<? super Void>> callback);

	/**
	 * Returns the ids of all channels the recipient side of this sender has declared
	 * ability to receive.
	 *
	 * <p>This collection does not contain duplicate channels.</p>
	 *
	 * @return a collection of channels
	 */
	@Override
	Collection<Identifier> getChannels();

	/**
	 * Returns if the recipient side of this sender has declared its ability to receive
	 * in a certain channel.
	 *
	 * @param channel the id of the channel to check
	 * @return whether the recipient declares it can receive in that channel
	 */
	@Override
	boolean hasChannel(Identifier channel);
}
