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

import java.util.concurrent.Future;

import io.github.fablabsmc.fablabs.api.networking.v1.PacketSender;

import net.minecraft.server.network.ServerLoginNetworkHandler;

/**
 * Represents the context for {@link ServerNetworking#getLoginReceiver()}, in which a
 * {@link net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket login query
 * response packet} is received.
 *
 * <p>Since the response is always received after the query is sent even when the client
 * doesn't handle the channel of the query, {@link #isUnderstood()} <strong>must</strong>
 * be checked when handling in this context.</p>
 *
 * @see ServerNetworking#getLoginReceiver()
 */
public interface ServerLoginContext extends ServerContext {
	/**
	 * {@inheritDoc}
	 *
	 * <p>In a server login context, the network handler is always a {@link
	 * ServerLoginNetworkHandler}.</p>
	 */
	@Override
	ServerLoginNetworkHandler getListener();

	/**
	 * Returns a packet sender that can send additional query request packets.
	 *
	 * <p>If an upcoming query request packet cannot be immediately sent after
	 * receiving this packet, call {@link #waitFor(Future)}, which will make
	 * sure the client is not admitted until the future is {@linkplain Future#isDone()
	 * done}.</p>
	 *
	 * @return the packet sender for query requests
	 */
	PacketSender getPacketSender();

	// utility

	/**
	 * Allows blocking client log-in until the {@code future} is {@link Future#isDone() done}.
	 *
	 * <p>Since packet reception happens on netty's event loops, this allows handlers to
	 * perform logic on the Server Thread, etc. For instance, a handler can prepare an
	 * {@linkplain #getPacketSender() upcoming query request} or check necessary login
	 * data on the server thread.</p>
	 *
	 * <p>Here is an example where the player log-in is blocked so that a credential check and
	 * building of a followup query request can be performed properly on the logical server
	 * thread before the player successfully logs in:
	 * <pre><blockquote>
	 *     ServerNetworking.getLoginReceiver().register(CHECK_CHANNEL, (context, buf) -&gt; {
	 *         if (!context.isUnderstood()) {
	 *             handler.disconnect(new LiteralText("Only accept clients that can check!"));
	 *             return;
	 *         }
	 *         String checkMessage = buf.readString(32767);
	 *         ServerLoginNetworkHandler handler = context.getPacketListener();
	 *         PacketSender sender = context.getPacketSender();
	 *         MinecraftServer server = context.getEngine();
	 *         // Just send the CompletableFuture returned by the server's submit method
	 *         context.waitFor(server.submit(() -&gt; {
	 *             LoginInfoChecker checker = LoginInfoChecker.get(server);
	 *             if (!checker.check(handler.getConnectionInfo(), checkMessage)) {
	 *                 handler.disconnect(new LiteralText("Invalid credentials!"));
	 *                 return;
	 *             }
	 *             sender.send(UPCOMING_CHECK, checker.buildSecondQueryPacket(handler, checkMessage));
	 *         }));
	 *     });
	 * </blockquote></pre>
	 * Usually it is enough to pass the return value for {@link net.minecraft.util.thread.ThreadExecutor#submit(Runnable)}
	 * for {@code future}.</p>
	 *
	 * @param future the future that must be done before the player can log in
	 */
	void waitFor(Future<?> future);
}
