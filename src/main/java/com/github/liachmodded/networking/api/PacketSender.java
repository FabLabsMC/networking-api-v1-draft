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
package com.github.liachmodded.networking.api;

import com.google.common.annotations.Beta;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public interface PacketSender {

	// todo ask ppl to use the listener version to release the buf after sending!!!
	void sendPacket(Identifier channel, PacketByteBuf buf);

	// the generic future listener can accept ChannelFutureListener
	void sendPacket(Identifier channel, PacketByteBuf buf, @Nullable GenericFutureListener<? extends Future<? super Void>> callback);

	// todo subject to modification/removal
	@Deprecated @Beta
	default void sendPacket(Identifier channel, Supplier<PacketByteBuf> bufSupplier) {
		PacketByteBuf buf = bufSupplier.get();
		sendPacket(channel, buf, none -> buf.release());
	}

	@Deprecated @Beta
	default void sendPacket(Identifier channel, Supplier<PacketByteBuf> bufSupplier, @Nullable GenericFutureListener<? extends Future<? super Void>> callback) {
		PacketByteBuf buf = bufSupplier.get();
		sendPacket(channel, buf, none -> {
			if (callback != null) {
				callback.operationComplete(null);
			}
			buf.release();
		});
	}
}