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

package io.github.fablabsmc.fablabs.api.networking.v1.client;

import io.github.fablabsmc.fablabs.api.networking.v1.PlayContext;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Represents the context for {@link ClientNetworking#getPlayReceiver()}, in which a
 * {@link net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket server to
 * client custom payload packet} is received.
 *
 * @see ClientNetworking#getPlayReceiver()
 */
@Environment(EnvType.CLIENT)
public interface ClientPlayContext extends PlayContext, ClientContext {
	/**
	 * {@inheritDoc}
	 *
	 * <p>In the client play context, the player is always the client's own player.
	 * It is the same as {@code MinecraftClient.getInstance().player}, but this
	 * player is guaranteed to be non-null.</p>
	 *
	 * @return the client's own player
	 */
	@Override
	ClientPlayerEntity getPlayer();

	/**
	 * {@inheritDoc}
	 *
	 * <p>In the client play context, the packet listener is always a {@link
	 * ClientPlayNetworkHandler}, which exposes a few useful properties, including
	 * the {@linkplain ClientPlayNetworkHandler#getWorld() client world}, the
	 * {@linkplain ClientPlayNetworkHandler#getTagManager() tag manager}, the
	 * {@linkplain ClientPlayNetworkHandler#getAdvancementHandler() advancement
	 * manager}, etc.</p>
	 */
	@Override
	ClientPlayNetworkHandler getListener();
}
