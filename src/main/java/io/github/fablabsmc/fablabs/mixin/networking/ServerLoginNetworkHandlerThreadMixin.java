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

import io.github.fablabsmc.fablabs.mixin.networking.access.ServerLoginNetworkHandlerAccess;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(targets = "net/minecraft/server/network/ServerLoginNetworkHandler$1")
public abstract class ServerLoginNetworkHandlerThreadMixin extends Thread {
	@Shadow(aliases = { "this$0", "field_14176" })
	private ServerLoginNetworkHandler field_14176;

	@ModifyArg(method = "run()V", remap = false, at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkEncryptionUtils;generateServerId(Ljava/lang/String;Ljava/security/PublicKey;Ljavax/crypto/SecretKey;)[B"), index = 0)
	public String networking$redirectServerId(String oldName) {
		return ((ServerLoginNetworkHandlerAccess) this.field_14176).getServerId();
	}
}
