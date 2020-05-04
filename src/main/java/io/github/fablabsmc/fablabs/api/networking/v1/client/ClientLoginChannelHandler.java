package io.github.fablabsmc.fablabs.api.networking.v1.client;

import java.util.concurrent.CompletableFuture;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import net.minecraft.network.PacketByteBuf;

@FunctionalInterface
public interface ClientLoginChannelHandler {
	CompletableFuture<Response> receive(ClientLoginContext context, PacketByteBuf buf);

	final class Response {
		private final PacketByteBuf buf;
		private final GenericFutureListener<? extends Future<? super Void>> listener;

		public static Response notUnderstood() {
			return notUnderstood(null);
		}

		public static Response notUnderstood(/* Nullable */ GenericFutureListener<? extends Future<? super Void>> listener) {
			return new Response(null, listener);
		}

		public Response(/* Nullable */ PacketByteBuf buf) {
			this(buf, null);
		}

		public Response(/* Nullable */ PacketByteBuf buf, /* Nullable */ GenericFutureListener<? extends Future<? super Void>> listener) {
			this.buf = buf;
			this.listener = listener;
		}

		public /* Nullable */ PacketByteBuf getBuf() {
			return buf;
		}

		public /* Nullable */ GenericFutureListener<? extends Future<? super Void>> getListener() {
			return listener;
		}
	}
}
