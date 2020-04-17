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

import net.minecraft.entity.player.PlayerEntity;

/**
 * Represents a context for {@linkplain PacketReceiver packet reception}
 * in {@linkplain net.minecraft.network.NetworkState#PLAY play stage} of
 * the game in a channel.
 * 
 * <p>Compared to the basic listener context, the play context offers
 * more access to the game.</p>
 */
public interface PlayContext extends ListenerContext {
	/**
	 * Returns the packet sender corresponding this context.
	 * 
	 * <p>This packet sender may be useful for responding after receiving the packet.</p>
	 *
	 * @return the packet sender
	 */
	PlayPacketSender getPacketSender();

	/**
	 * Returns a player associated with the current packet.
	 */
	PlayerEntity getPlayer();

}
