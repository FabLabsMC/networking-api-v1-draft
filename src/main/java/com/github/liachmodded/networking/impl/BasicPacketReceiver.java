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
package com.github.liachmodded.networking.impl;

import com.github.liachmodded.networking.api.ChannelHandler;
import com.github.liachmodded.networking.api.HandlerContext;
import com.github.liachmodded.networking.api.PacketReceiver;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class BasicPacketReceiver<C extends HandlerContext> implements PacketReceiver<C> {

	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private final Map<Identifier, ChannelHandler<? super C>> handlers;

	public BasicPacketReceiver() {
		this(new HashMap<>()); // sync map should be fine as there is little read write competitions
	}

	public BasicPacketReceiver(Map<Identifier, ChannelHandler<? super C>> map) {
		this.handlers = map;
	}

	public @Nullable ChannelHandler<? super C> get(Identifier channel) {
		Lock lock = this.lock.readLock();
		lock.lock();
		try {
			return this.handlers.get(channel);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean register(Identifier channel, ChannelHandler<? super C> handler) {
		Objects.requireNonNull(handler, "handler");
		Lock lock = this.lock.writeLock();
		lock.lock();
		try {
			return this.handlers.putIfAbsent(channel, handler) == null;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public @Nullable ChannelHandler<? super C> unregister(Identifier channel) {
		Lock lock = this.lock.writeLock();
		lock.lock();
		try {
			return this.handlers.remove(channel);
		} finally {
			lock.unlock();
		}

	}

	@Override
	public Collection<Identifier> getChannels() {
		Lock lock = this.lock.readLock();
		lock.lock();
		try {
			return new HashSet<>(this.handlers.keySet());
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean hasChannel(Identifier channel) {
		Lock lock = this.lock.readLock();
		lock.lock();
		try {
			return this.handlers.containsKey(channel);
		} finally {
			lock.unlock();
		}
	}
}
