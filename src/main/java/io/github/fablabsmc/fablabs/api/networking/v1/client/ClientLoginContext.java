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

import java.util.concurrent.CompletableFuture;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.network.PacketByteBuf;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Represents the context for {@link ClientNetworking#getLoginReceiver()}, in which a
 * {@link net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket login
 * query request packest} is received.
 *
 * <p>Compared to other type of packet reception context, the client login query packet
 * reception is expected to respond after receiving a packet. Use {@code respond} methods
 * to send a response; if none of the {@code respond} methods are called immediately within
 * {@link io.github.fablabsmc.fablabs.api.networking.v1.ChannelHandler#receive(io.github.fablabsmc.fablabs.api.networking.v1.ListenerContext, PacketByteBuf)},
 * a "not understood" response will be sent to the server. If a response cannot be calculated
 * immediately, use {@link #respond(CompletableFuture)} or {@link #respond(CompletableFuture, GenericFutureListener)},
 * which can send the response when the {@link CompletableFuture} is completed.</p>
 *
 * @see ClientNetworking#getLoginReceiver()
 */
@Environment(EnvType.CLIENT)
public interface ClientLoginContext extends ClientContext {
	/**
	 * {@inheritDoc}
	 *
	 * <p>In a client login context, the network handler is always a {@link
	 * ClientLoginNetworkHandler}.</p>
	 */
	@Override
	ClientLoginNetworkHandler getListener();

	// packet qualities

	/**
	 * Returns the integer ID of the query request.
	 */
	int getQueryId();

	// utilities
	// if none of these "respond" is called, an unknown packet will be sent

	/**
	 * Sends a response to the server.
	 *
	 * <p>The {@code response} may be {@code null} to indicate a "not understood"
	 * query response.</p>
	 *
	 * @param buf the content of the response, may be {@code null}
	 */
	void respond(PacketByteBuf buf);

	/**
	 * Sends a response to the server.
	 *
	 * <p>The {@code response} may be {@code null} to indicate a "not understood"
	 * query response.</p>
	 *
	 * @param buf      the content of the response, may be {@code null}
	 * @param callback a callback when the response is sent, may be {@code null}
	 */
	void respond(PacketByteBuf buf, GenericFutureListener<? extends Future<? super Void>> callback);

	/**
	 * Schedule to send a response to the server when the {@code future} is completed.
	 *
	 * <p>If the future {@linkplain CompletableFuture#complete(Object) completed} with {@code null}
	 * result, a "not understood" query response is sent to the server.</p>
	 *
	 * <p>If the future {@linkplain CompletableFuture#completeExceptionally(Throwable) completed
	 * exceptionally}, a "not understood" query response is sent to the server.</p>
	 *
	 * @param future the future that calculates the response
	 */
	void respond(CompletableFuture<? extends PacketByteBuf> future);

	/**
	 * Schedule to send a response to the server when the {@code future} is completed.
	 *
	 * <p>If the future {@linkplain CompletableFuture#complete(Object) completed} with {@code null}
	 * result, a "not understood" query response is sent to the server.</p>
	 *
	 * <p>If the future {@linkplain CompletableFuture#completeExceptionally(Throwable) completed
	 * exceptionally}, a "not understood" query response is sent to the server.</p>
	 *
	 * @param future   the future that calculates the response
	 * @param callback a callback when the response is sent, may be {@code null}
	 */
	void respond(CompletableFuture<? extends PacketByteBuf> future, GenericFutureListener<? extends Future<? super Void>> callback);
}
