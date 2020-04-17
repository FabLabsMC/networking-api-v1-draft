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
package io.github.fablabsmc.fablabs.api.networking.v1.util;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import net.minecraft.network.PacketByteBuf;

/**
 * Utilities for working with netty's future listeners.
 */
public final class FutureListeners {
	/**
	 * Returns a future listener that releases a packet byte buf when the buffer has
	 * been sent to a remote connection.
	 *
	 * @param buf the buffer
	 * @return the future listener
	 */
	public static ChannelFutureListener free(PacketByteBuf buf) {
		return (future) -> {
			if (!isLocalChannel(future.channel())) {
				buf.release();
			}
		};
	}

	/**
	 * Returns whether a netty channel performs local transportation, or if the
	 * message objects in the channel are directly passed than written to and
	 * read from a byte buf.
	 *
	 * @param channel the channel to check
	 * @return whether the channel is local
	 */
	public static boolean isLocalChannel(Channel channel) {
		return channel instanceof LocalServerChannel || channel instanceof LocalChannel;
	}

	/**
	 * Combines two future listeners.
	 *
	 * @param first  the first future listener
	 * @param second the second future listener
	 * @param <A>    the future type of the first listener, used for casting
	 * @param <B>    the future type of the second listener, used for casting
	 * @return the combined future listener.
	 */
	@SuppressWarnings("unchecked") // A, B exist just to allow casting lol
	public static <A extends Future<? super Void>, B extends Future<? super Void>> GenericFutureListener<? extends Future<? super Void>> union(
			GenericFutureListener<A> first, GenericFutureListener<B> second) {
		if (first == null) {
			return second;
		}

		if (second == null) {
			return first;
		}

		return (future) -> {
			first.operationComplete((A) future);
			second.operationComplete((B) future);
		};
	}
}
