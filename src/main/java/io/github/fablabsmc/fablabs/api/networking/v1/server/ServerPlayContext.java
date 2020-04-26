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

package io.github.fablabsmc.fablabs.api.networking.v1.server;

import io.github.fablabsmc.fablabs.api.networking.v1.PlayContext;

import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Represents the context for {@link ServerNetworking#getPlayReceiver()}, in which a
 * {@link net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket client to server
 * custom payload packet} is received.
 *
 * @see ServerNetworking#getPlayReceiver()
 */
public interface ServerPlayContext extends ServerContext, PlayContext {
	/**
	 * {@inheritDoc}
	 *
	 * <p>In the server play context, the player is always a server player
	 * associated with the {@link #getListener() network handler}.</p>
	 *
	 * @return a server player
	 */
	@Override
	ServerPlayerEntity getPlayer();

	/**
	 * {@inheritDoc}
	 *
	 * <p>In the client play context, the packet listener is always a {@link
	 * ServerPlayNetworkHandler}.</p>
	 */
	@Override
	ServerPlayNetworkHandler getListener();
}
