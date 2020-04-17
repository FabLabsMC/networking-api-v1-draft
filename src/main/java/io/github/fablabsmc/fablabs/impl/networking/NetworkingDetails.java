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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import io.github.fablabsmc.fablabs.api.networking.v1.client.ClientNetworking;
import io.github.fablabsmc.fablabs.api.networking.v1.server.ServerNetworking;
import io.github.fablabsmc.fablabs.api.networking.v1.util.PacketByteBufs;
import io.github.fablabsmc.fablabs.impl.networking.client.ClientNetworkingDetails;
import io.github.fablabsmc.fablabs.impl.networking.server.QueryIdFactory;
import io.github.fablabsmc.fablabs.impl.networking.server.ServerNetworkingDetails;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public final class NetworkingDetails {

	public static final String MOD_ID = "networking-api-v1-draft";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	public static final Identifier REGISTER_CHANNEL = new Identifier("minecraft", "register");
	public static final Identifier UNREGISTER_CHANNEL = new Identifier("minecraft", "unregister");
	public static final Identifier EARLY_REGISTRATION_CHANNEL = new Identifier(MOD_ID, "early_registration");
	public static final boolean WARN_UNREGISTERED_PACKETS = Boolean
			.parseBoolean(System.getProperty(MOD_ID + ".warnUnregisteredPackets", "true"));

	public static QueryIdFactory createQueryIdManager() {
		// todo incremental ids or randomized
		return new QueryIdFactory() {
			private final AtomicInteger currentId = new AtomicInteger();

			@Override
			public int nextId() {
				return currentId.getAndIncrement();
			}
		};
	}

	public static void initialize() {
		ServerNetworking.LOGIN_QUERY_START.register(handler -> {
			PacketByteBuf buf = PacketByteBufs.create();
			Collection<Identifier> channels = ServerNetworkingDetails.PLAY.getChannels();
			buf.writeVarInt(channels.size());
			for (Identifier id : channels) {
				buf.writeIdentifier(id);
			}
			ServerNetworking.getLoginSender(handler).sendPacket(EARLY_REGISTRATION_CHANNEL, buf);
			NetworkingDetails.LOGGER.debug("Sent accepted channels to the client");
		});
		ServerNetworking.getLoginReceiver().register(EARLY_REGISTRATION_CHANNEL, (context, buf) -> {
			if (!context.isUnderstood()) {
				return;
			}

			int n = buf.readVarInt();
			List<Identifier> ids = new ArrayList<>(n);
			for (int i = 0; i < n; i++) {
				ids.add(buf.readIdentifier());
			}

			((ChannelInfoHolder) context.getListener().getConnection()).getChannels().addAll(ids);
			NetworkingDetails.LOGGER.debug("Received accepted channels from the client");
		});
	}

	public static void clientInitialize() {
		ClientNetworking.getLoginReceiver().register(EARLY_REGISTRATION_CHANNEL, (context, buf) -> {
			int n = buf.readVarInt();
			List<Identifier> ids = new ArrayList<>(n);
			for (int i = 0; i < n; i++) {
				ids.add(buf.readIdentifier());
			}

			((ChannelInfoHolder) context.getListener().getConnection()).getChannels().addAll(ids);
			NetworkingDetails.LOGGER.debug("Received accepted channels from the server");

			PacketByteBuf response = PacketByteBufs.create();
			Collection<Identifier> channels = ClientNetworkingDetails.PLAY.getChannels();
			response.writeVarInt(channels.size());
			for (Identifier id : channels) {
				response.writeIdentifier(id);
			}

			context.respond(response);
			NetworkingDetails.LOGGER.debug("Sent accepted channels to the server");
		});
	}
}
