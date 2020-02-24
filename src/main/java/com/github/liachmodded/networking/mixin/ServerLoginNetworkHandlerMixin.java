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
package com.github.liachmodded.networking.mixin;

import com.github.liachmodded.networking.impl.server.ServerLoginNetworkAddon;
import com.github.liachmodded.networking.impl.server.ServerLoginNetworkHandlerHook;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLoginNetworkHandler.class)
public abstract class ServerLoginNetworkHandlerMixin implements ServerLoginNetworkHandlerHook {

	@Shadow @Final @Mutable private String field_14165;

	@Shadow public abstract void acceptPlayer();

	private ServerLoginNetworkAddon addon;

	@Inject(method = "<init>", at = @At("RETURN"))
	public void networking$ctor(CallbackInfo ci) {
		this.addon = new ServerLoginNetworkAddon((ServerLoginNetworkHandler) (Object) this);
	}
	
	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerLoginNetworkHandler;acceptPlayer()V"))
	public void networking$onAcceptPlayer(ServerLoginNetworkHandler handler) {
		if (this.addon.queryTick()) {
			acceptPlayer();
		}
	}

	@Inject(method = "onQueryResponse", at = @At(value = "HEAD"), cancellable = true)
	public void networking$customPayloadReceivedAsync(LoginQueryResponseC2SPacket packet, CallbackInfo ci) {
		if (this.addon.handle(packet)) {
			ci.cancel();
		}
	}

	@ModifyArg(method = "onHello", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/login/LoginHelloS2CPacket;<init>(Ljava/lang/String;Ljava/security/PublicKey;[B)V"), index = 0)
	public String networking$redirectServerId(String oldId) {
		return this.field_14165;
	}
	
	@Redirect(method = "acceptPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getNetworkCompressionThreshold()I", ordinal = 0))
	public int networking$removeLateCompressionPacketSending(MinecraftServer server) {
		return -1;
	}

	@Override
	public ServerLoginNetworkAddon getAddon() {
		return this.addon;
	}

	@Override
	public void setServerId(String id) {
		this.field_14165 = id;
	}
}
