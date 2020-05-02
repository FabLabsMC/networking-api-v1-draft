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

import net.minecraft.util.Identifier;

/**
 * Associates packets to individual packet reception handlers by channel.
 *
 * @param <C> the receiver's context beyond channel
 */
public interface PacketReceiver<C extends ListenerContext> extends ChannelAware {
	/**
	 * Registers a handler to a channel.
	 *
	 * <p>If a handler is already registered to the {@code channel}, this method
	 * will return {@code false}, and no change will be made. Use {@link
	 * #unregister(Identifier)} to unregister the existing handler.</p>
	 *
	 * @param channel the id of the channel
	 * @param handler the handler
	 * @return whether the handler is registered
	 */
	boolean register(Identifier channel, ChannelHandler<? super C> handler);

	/**
	 * Removes the handler of a channel.
	 *
	 * <p>The {@code channel} is guaranteed not to have a handler after this call.</p>
	 *
	 * @param channel the id of the channel
	 * @return the previous handler, or {@code null} if no handler was bound to the channel
	 */
	/* Nullable */ ChannelHandler<? super C> unregister(Identifier channel);

	/**
	 * Returns the collection of all channels that have handlers in this receiver.
	 *
	 * <p>This collection does not contain duplicate channels.</p>
	 *
	 * @return a collection of channels
	 */
	@Override
	Collection<Identifier> getChannels();

	/**
	 * Returns whether a channel has a handler in this receiver.
	 *
	 * @param channel the id of the channel to check
	 * @return whether the channel has a handler
	 */
	@Override
	boolean hasChannel(Identifier channel);
}
