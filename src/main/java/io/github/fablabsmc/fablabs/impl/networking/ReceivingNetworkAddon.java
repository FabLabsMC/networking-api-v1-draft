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
package io.github.fablabsmc.fablabs.impl.networking;

import io.github.fablabsmc.fablabs.api.networking.v1.ChannelHandler;
import io.github.fablabsmc.fablabs.api.networking.v1.HandlerContext;
import io.github.fablabsmc.fablabs.api.networking.v1.util.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

// client login
public abstract class ReceivingNetworkAddon<C extends HandlerContext> {
	protected final BasicPacketReceiver<C> receiver;

	protected ReceivingNetworkAddon(BasicPacketReceiver<C> receiver) {
		this.receiver = receiver;
	}

	// always supposed to handle async!
	protected boolean handle(Identifier channel, PacketByteBuf originalBuf, C context) {
		ChannelHandler<? super C> handler = this.receiver.get(channel);

		if (handler == null)
			return false;

		PacketByteBuf buf = PacketByteBufs.slice(originalBuf);
		try {
			handler.receive(context, buf);
		} catch (Throwable ex) {
			NetworkingDetails.LOGGER.error("Encountered exception while handling in channel \"{}\"", channel, ex);
			handler.rethrow(ex);
		}

		return true;
	}
}
