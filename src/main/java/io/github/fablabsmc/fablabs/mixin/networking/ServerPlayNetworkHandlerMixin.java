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

import io.github.fablabsmc.fablabs.api.networking.v1.server.ServerNetworking;
import io.github.fablabsmc.fablabs.impl.networking.DisconnectPacketSource;
import io.github.fablabsmc.fablabs.impl.networking.server.ServerPlayNetworkAddon;
import io.github.fablabsmc.fablabs.impl.networking.server.ServerPlayNetworkHandlerHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.text.Text;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin implements ServerPlayNetworkHandlerHook, DisconnectPacketSource {
	private ServerPlayNetworkAddon addon;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void networking$ctor(CallbackInfo ci) {
		this.addon = new ServerPlayNetworkAddon((ServerPlayNetworkHandler) (Object) this);
	}

	@Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)
	private void networking$customPayloadReceivedAsync(CustomPayloadC2SPacket packet, CallbackInfo ci) {
		if (this.addon.handle(packet)) {
			ci.cancel();
		}
	}

	@Inject(method = "onDisconnected", at = @At("HEAD"))
	private void networking$onDisconnected(Text reason, CallbackInfo ci) {
		ServerNetworking.PLAY_DISCONNECTED.invoker().handle(this.addon);
	}

	@Override
	public ServerPlayNetworkAddon getAddon() {
		return this.addon;
	}

	@Override
	public Packet<?> makeDisconnectPacket(Text message) {
		return new DisconnectS2CPacket(message);
	}
}
