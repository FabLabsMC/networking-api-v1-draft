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

package io.github.fablabsmc.fablabs.impl.networking;

import java.util.Locale;

import io.github.fablabsmc.fablabs.api.networking.v1.ListenerContext;

public enum OffThreadGameAccessPolicy {
	PERMIT {
		@Override
		public void check(ListenerContext context, String content) {
		}
	},
	WARN {
		@Override
		public void check(ListenerContext context, String content) {
			if (!context.getEngine().isOnThread()) {
				NetworkingDetails.LOGGER.warn("Accessing {} out of the main thread!", content);
			}
		}
	},
	THROW {
		@Override
		public void check(ListenerContext context, String content) {
			if (!context.getEngine().isOnThread()) {
				throw new IllegalStateException(String.format("Accessing %s out of the main thread!", content));
			}
		}
	};

	public static OffThreadGameAccessPolicy parse(String propertyKey, OffThreadGameAccessPolicy fallback) {
		String value = System.getProperty(propertyKey);

		if (value == null) {
			return fallback;
		}

		try {
			return OffThreadGameAccessPolicy.valueOf(value.toUpperCase(Locale.ROOT));
		} catch (IllegalArgumentException ex) {
			return fallback;
		}
	}

	public abstract void check(ListenerContext context, String content);
}
