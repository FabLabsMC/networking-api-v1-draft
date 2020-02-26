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
package com.github.liachmodded.networking.user;

import com.github.liachmodded.networking.api.server.ServerNetworking;
import com.github.liachmodded.networking.api.util.PacketByteBufs;
import com.github.liachmodded.networking.impl.CartNetworkingDetails;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class NetworkingUser implements ModInitializer {
	
	public static final String ID = "networking-api-v1-draft";
	private static final Logger LOGGER = LogManager.getLogger(ID);
	public static final Identifier TEST_CHANNEL = id("test_channel");

	public static Identifier id(String name) {
		return new Identifier(ID, name);
	}

	public static void sendToTestChannel(ServerPlayerEntity player, String stuff) {
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeString(stuff, 32767);
		ServerNetworking.getPlaySender(player).sendClosedPacket(TEST_CHANNEL, buf);
		CartNetworkingDetails.LOGGER.info("Sent custom payload packet in {}", TEST_CHANNEL);
	}

	public static void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
		LOGGER.info("Registering test command");
		dispatcher.register(
				CommandManager.literal("networktestcommand")
						.then(CommandManager.argument("stuff", StringArgumentType.string())
								.executes(ctx -> {
									String stuff = StringArgumentType.getString(ctx, "stuff");
									sendToTestChannel(ctx.getSource().getPlayer(), stuff);
									return Command.SINGLE_SUCCESS;
								})
						)
		);
	}

	@Override
	public void onInitialize() {
		LOGGER.info("Hello from networking user!");
	}
}
