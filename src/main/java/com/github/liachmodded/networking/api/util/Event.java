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
package com.github.liachmodded.networking.api.util;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.function.Function;

public interface Event<T> {
	// not thread safe impl lol
	static <T> Event<T> createArrayBacked(Class<? super T> type, Function<T[], T> invokerFactory) {
		return new Event<T>() {
			private T[] array = null;
			private T currentInvoker = null;
			
			@Override 
			public T invoker() {
				if (currentInvoker == null) {
					currentInvoker = makeInvoker();
				}
				return currentInvoker;
			}
			
			@SuppressWarnings("unchecked")
			private T makeInvoker() {
				if (array == null) {
					array = (T[]) Array.newInstance(type, 0);
				}
				return invokerFactory.apply(array);
			}

			@Override
			@SuppressWarnings("unchecked")
			public void register(T subscriber) {
				if (array == null) {
					array = (T[]) Array.newInstance(type, 1);
				} else {
					array = Arrays.copyOf(array, array.length + 1);
				}
				array[array.length - 1] = subscriber;
				
				currentInvoker = null;
			}
		};
	}
	
	T invoker();
	
	void register(T subscriber);
}
