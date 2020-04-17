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

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCounted;

import net.minecraft.network.PacketByteBuf;

/**
 * Handles packets in a channel.
 * 
 * <p>This is supposed to be implemented by API users to accomplish packet handling
 * functionalities.</p>
 * 
 * @param <C> the listener context
 * @see PacketReceiver
 */
@FunctionalInterface
public interface ChannelHandler<C extends ListenerContext> {

	/**
	 * Receives a packet.
	 * 
	 * <p>This method is executed on {@linkplain io.netty.channel.EventLoop netty's event loops}.
	 * Modification to the {@linkplain ListenerContext#getEngine() game} should be
	 * {@linkplain net.minecraft.util.thread.ThreadExecutor#submit(Runnable) scheduled}.</p>
	 * 
	 * <p>The {@code buf} will be {@linkplain ReferenceCounted#release() released} on exiting this
	 * method. To ensure access to the buf later, you should {@link ReferenceCounted#retain()
	 * retain} the {@code buf}.</p>
	 * 
	 * <p>An example usage can be like below, assuming {@code C} is a 
	 * {@linkplain io.github.fablabsmc.fablabs.api.networking.v1.client.ClientContext client context}:
	 * <pre><blockquote>
	 *     (context, buf) -&rt; {
	 *         String message = buf.readString(32767);
	 *         context.getEngine().submit(() -&rt; {
	 *             context.getEngine().send(() -&rt; context.getEngine().inGameHud.setOverlayMessage(message, true));
	 *         });
	 *     }
	 * </blockquote></pre></p>
	 * 
	 * <p>When this method throws an exception, it will be captured and logged. The exception
	 * will be fed to {@link #rethrow(Throwable)}, which by default throws the exception to
	 * the {@linkplain io.netty.channel.EventLoop event loop} and handled by {@link 
	 * net.minecraft.network.ClientConnection#exceptionCaught(ChannelHandlerContext, Throwable)},
	 * causing a disconnection.</p>
	 * 
	 * @param context the context for the packet
	 * @param buf the content of the packet
	 */
	void receive(C context, PacketByteBuf buf);

	// todo do we need this rethrow functionality
	// throw networking errors, may report custom message as well
	// may throw OffThreadException.INSTANCE for example
	/**
	 * Handles a exception thrown by {@link #receive(ListenerContext, PacketByteBuf)}.
	 * 
	 * <p>By default, this implementation will simply throw the captured exception.</p>
	 * 
	 * <p>Throwables thrown by this method will be handled by {@link
	 * net.minecraft.network.ClientConnection#exceptionCaught(ChannelHandlerContext, Throwable)}.</p>
	 * 
	 * @param ex the captured exception
	 * @param <E> the throwable type variable, which allows throwing any throwable as unchecked
	 * @throws E any exception as a result of exception handling.
	 */
	@SuppressWarnings("unchecked")
	default <E extends Throwable> void rethrow(Throwable ex) throws E {
		throw (E) ex;
	}
}
