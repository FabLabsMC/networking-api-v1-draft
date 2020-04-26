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

import io.github.fablabsmc.fablabs.api.networking.v1.ListenerContext;
import io.github.fablabsmc.fablabs.api.networking.v1.PacketReceiver;

import net.minecraft.client.MinecraftClient;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Represents a context for {@linkplain PacketReceiver packet reception}
 * on the logical client.
 *
 * <p>Compared to the basic listener context, the client context offers
 * access to the active {@linkplain MinecraftClient <i>Minecraft</i> Client}.</p>
 */
@Environment(EnvType.CLIENT)
public interface ClientContext extends ListenerContext {
	/**
	 * {@inheritDoc}
	 *
	 * <p>In a client context, the game engine is always a <i>Minecraft</i> Client.</p>
	 *
	 * @return the <i>Minecraft</i> Client
	 */
	@Override
	MinecraftClient getEngine();
}
