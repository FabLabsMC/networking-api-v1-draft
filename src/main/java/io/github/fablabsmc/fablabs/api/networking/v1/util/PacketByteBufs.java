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
package io.github.fablabsmc.fablabs.api.networking.v1.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.util.PacketByteBuf;

/**
 * Utility methods for working with packet byte bufs.
 */
public final class PacketByteBufs {
	private static final PacketByteBuf EMPTY_PACKET_BYTE_BUF = new PacketByteBuf(Unpooled.EMPTY_BUFFER);

	private PacketByteBufs() {
	}

	/**
	 * Returns an empty instance of packet byte buf.
	 *
	 * @return an empty buf
	 */
	public static PacketByteBuf empty() {
		return EMPTY_PACKET_BYTE_BUF;
	}

	// Convenience methods for byte buf methods that return a new byte buf

	/**
	 * Returns a new heap memory-backed instance of packet byte buf.
	 *
	 * @return a new buf
	 */
	public static PacketByteBuf create() {
		return new PacketByteBuf(Unpooled.buffer());
	}

	/**
	 * Wraps the newly created buf from {@code buf.readBytes} in a packet byte buf.
	 *
	 * @param buf    the original buf
	 * @param length the number of bytes to transfer
	 * @return the transferred bytes
	 * @see ByteBuf#readBytes(int)
	 */
	public static PacketByteBuf readBytes(ByteBuf buf, int length) {
		return new PacketByteBuf(buf.readBytes(length));
	}

	/**
	 * Wraps the newly created buf from {@code buf.readSlice} in a packet byte buf.
	 *
	 * @param buf    the original buf
	 * @param length the size of the new slice
	 * @return the newly created slice
	 * @see ByteBuf#readSlice(int)
	 */
	public static PacketByteBuf readSlice(ByteBuf buf, int length) {
		return new PacketByteBuf(buf.readSlice(length));
	}

	/**
	 * Wraps the newly created buf from {@code buf.readRetainedSlice} in a packet byte buf.
	 *
	 * @param buf    the original buf
	 * @param length the size of the new slice
	 * @return the newly created slice
	 * @see ByteBuf#readRetainedSlice(int)
	 */
	public static PacketByteBuf readRetainedSlice(ByteBuf buf, int length) {
		return new PacketByteBuf(buf.readRetainedSlice(length));
	}

	/**
	 * Wraps the newly created buf from {@code buf.copy} in a packet byte buf.
	 *
	 * @param buf the original buf
	 * @return a copy of the buf
	 * @see ByteBuf#copy()
	 */
	public static PacketByteBuf copy(ByteBuf buf) {
		return new PacketByteBuf(buf.copy());
	}

	/**
	 * Wraps the newly created buf from {@code buf.copy} in a packet byte buf.
	 *
	 * @param buf    the original buf
	 * @param index  the starting index
	 * @param length the size of the copy
	 * @return a copy of the buf
	 * @see ByteBuf#copy(int, int)
	 */
	public static PacketByteBuf copy(ByteBuf buf, int index, int length) {
		return new PacketByteBuf(buf.copy(index, length));
	}

	/**
	 * Wraps the newly created buf from {@code buf.slice} in a packet byte buf.
	 *
	 * @param buf the original buf
	 * @return a slice of the buf
	 * @see ByteBuf#slice()
	 */
	public static PacketByteBuf slice(ByteBuf buf) {
		return new PacketByteBuf(buf.slice());
	}

	/**
	 * Wraps the newly created buf from {@code buf.retainedSlice} in a packet byte buf.
	 *
	 * @param buf the original buf
	 * @return a slice of the buf
	 * @see ByteBuf#retainedSlice()
	 */
	public static PacketByteBuf retainedSlice(ByteBuf buf) {
		return new PacketByteBuf(buf.retainedSlice());
	}

	/**
	 * Wraps the newly created buf from {@code buf.slice} in a packet byte buf.
	 *
	 * @param buf    the original buf
	 * @param index  the starting index
	 * @param length the size of the copy
	 * @return a slice of the buf
	 * @see ByteBuf#slice(int, int)
	 */
	public static PacketByteBuf slice(ByteBuf buf, int index, int length) {
		return new PacketByteBuf(buf.slice(index, length));
	}

	/**
	 * Wraps the newly created buf from {@code buf.retainedSlice} in a packet byte buf.
	 *
	 * @param buf    the original buf
	 * @param index  the starting index
	 * @param length the size of the copy
	 * @return a slice of the buf
	 * @see ByteBuf#retainedSlice(int, int)
	 */
	public static PacketByteBuf retainedSlice(ByteBuf buf, int index, int length) {
		return new PacketByteBuf(buf.retainedSlice(index, length));
	}

	/**
	 * Wraps the newly created buf from {@code buf.duplicate} in a packet byte buf.
	 *
	 * @param buf the original buf
	 * @return a duplicate of the buf
	 * @see ByteBuf#duplicate()
	 */
	public static PacketByteBuf duplicate(ByteBuf buf) {
		return new PacketByteBuf(buf.duplicate());
	}

	/**
	 * Wraps the newly created buf from {@code buf.retainedDuplicate} in a packet byte buf.
	 *
	 * @param buf the original buf
	 * @return a duplicate of the buf
	 * @see ByteBuf#retainedDuplicate()
	 */
	public static PacketByteBuf retainedDuplicate(ByteBuf buf) {
		return new PacketByteBuf(buf.retainedDuplicate());
	}
}
