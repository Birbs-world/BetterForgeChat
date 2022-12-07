package com.jeremiahbl.bfcmod.utils;

import com.jeremiahbl.bfcmod.BetterForgeChat;
import com.jeremiahbl.bfcmod.TextFormatter;
import com.jeremiahbl.bfcmod.config.ConfigHandler;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;

public class BetterForgeChatUtilities {
	private static String playerNameFormat = "";
	
	public static void reloadConfig() {
		playerNameFormat = ConfigHandler.config.playerNameFormat.get();
	}
	
	public static String getRawPreferredPlayerName(ServerPlayer player) {
		return getRawPreferredPlayerName(player, true, true);
	}
	public static String getRawPreferredPlayerName(ServerPlayer player, boolean enableNickname, boolean enableMetadata) {
		String name = BetterForgeChat.instance.nicknameProvider != null && enableNickname ? BetterForgeChat.instance.nicknameProvider.getPlayerChatName(player) : player.getGameProfile().getName();
		String pfx = "", sfx = "";
		if(enableMetadata && BetterForgeChat.instance.metadataProvider != null) {
			String[] dat = BetterForgeChat.instance.metadataProvider.getPlayerPrefixAndSuffix(player);
			pfx = dat[0];
			sfx = dat[1];
		}
		String fmat = ConfigHandler.config.playerNameFormat.get();
		if(name == null) {
			BetterForgeChat.LOGGER.info("NicknameProvider (FTB Essentials) returned a null nickname, please post issue on GitHub!");
			name = player.getGameProfile().getName();
		}
		if(fmat == null) {
			BetterForgeChat.LOGGER.warn("Could not get playerNameFormat from configuration file, please post issue on GitHub!");
			return player.getGameProfile().getName();
		} else return fmat.replace("$prefix", pfx).replace("$name", name).replace("$suffix", sfx);
	}
	public static TextComponent getFormattedPlayerName(ServerPlayer player) {
		return TextFormatter.stringToFormattedText(getRawPreferredPlayerName(player));
	}
	public static TextComponent getFormattedPlayerName(ServerPlayer player, boolean enableNickname, boolean enableMetadata) {
		return TextFormatter.stringToFormattedText(getRawPreferredPlayerName(player, enableNickname, enableMetadata));
	}
}
