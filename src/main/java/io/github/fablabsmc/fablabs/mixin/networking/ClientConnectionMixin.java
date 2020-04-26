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

package io.github.fablabsmc.fablabs.mixin.networking;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

import io.github.fablabsmc.fablabs.impl.networking.ChannelInfoHolder;
import io.github.fablabsmc.fablabs.impl.networking.DisconnectPacketSource;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

@Mixin(ClientConnection.class)
public abstract class ClientConnectionMixin implements ChannelInfoHolder {
	@Shadow
	private PacketListener packetListener;

	private Collection<Identifier> playChannels;

	@Shadow
	public abstract void send(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> callback);

	@Shadow
	public abstract void disconnect(Text disconnectReason);

	@Inject(method = "<init>", at = @At("RETURN"))
	private void networking$ctor(NetworkSide side, CallbackInfo ci) {
		this.playChannels = Collections.newSetFromMap(new ConcurrentHashMap<>());
	}

	@Redirect(method = "exceptionCaught", remap = false, at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;send(Lnet/minecraft/network/Packet;Lio/netty/util/concurrent/GenericFutureListener;)V"))
	private void networking$resendOnExceptionCaught(ClientConnection self, Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> listener) {
		PacketListener handler = this.packetListener;

		if (handler instanceof DisconnectPacketSource) {
			this.send(((DisconnectPacketSource) handler).makeDisconnectPacket(new TranslatableText("disconnect.genericReason")), listener);
		} else {
			this.disconnect(new TranslatableText("disconnect.genericReason")); // Don't send packet if we cannot send proper packets
		}
	}

	@Override
	public Collection<Identifier> getChannels() {
		return this.playChannels;
	}
}
