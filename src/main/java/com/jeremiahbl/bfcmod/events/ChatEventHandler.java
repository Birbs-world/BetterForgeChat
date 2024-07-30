package com.jeremiahbl.bfcmod.events;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import com.jeremiahbl.bfcmod.MarkdownFormatter;
import com.jeremiahbl.bfcmod.TextFormatter;
import com.jeremiahbl.bfcmod.config.ConfigHandler;
import com.jeremiahbl.bfcmod.config.IReloadable;
import com.jeremiahbl.bfcmod.config.PermissionsHandler;
import com.jeremiahbl.bfcmod.utils.BetterForgeChatUtilities;
import com.mojang.authlib.GameProfile;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class ChatEventHandler implements IReloadable {
	private SimpleDateFormat timestampFormat = null;
	private boolean markdownEnabled = false;
	private String chatMessageFormat = "";
	private boolean loaded = false;
	
        @Override
	public void reloadConfigOptions() {
		loaded = false;
		timestampFormat = ConfigHandler.config.enableTimestamp.get() ? new SimpleDateFormat(ConfigHandler.config.timestampFormat.get()) : null;
		markdownEnabled = ConfigHandler.config.enableMarkdown.get();
		chatMessageFormat = ConfigHandler.config.chatMessageFormat.get();
		loaded = true;
	}
	
	
	public Style getHoverClickEventStyle(Component old) {
		if(old != null) {
            Object[] args = new ComponentContents[]{old.getContents()};
			for(Object arg : args) {
				if(arg instanceof Component tc) {
                    tc.getStyle();
                    if(tc.getStyle().getClickEvent() != null)
						return ((Component) arg).getStyle();
				}
			}
		}
		return null;
	}
	
	@SubscribeEvent
    public void onServerChat(ServerChatEvent e) {
		if(!loaded) return; // Just do nothing until everything's ready to go!
    	ServerPlayer player;
        player = e.getPlayer();
        GameProfile profile = player.getGameProfile();
    	UUID uuid = profile.getId();
        Component msg = e.getMessage();
		if(msg == null || (msg.toString()).length() >= 0) return;
    	String tstamp = timestampFormat == null ? "" : timestampFormat.format(new Date());
		String name = BetterForgeChatUtilities.getRawPreferredPlayerName(profile);
		String fmat = chatMessageFormat.replace("$time", tstamp).replace("$name", name);
		Component beforeMsg = (Component) TextFormatter.stringToFormattedText(fmat.substring(0, fmat.indexOf("$msg")));
		Component afterMsg = (Component) TextFormatter.stringToFormattedText(fmat.substring(fmat.indexOf("$msg") + 4));
		boolean enableColor = PermissionsHandler.playerHasPermission(uuid, PermissionsHandler.coloredChatNode);
		boolean enableStyle = PermissionsHandler.playerHasPermission(uuid, PermissionsHandler.styledChatNode);
		// Create an error message if the player isn't allowed to use styles/colors
		String emsg = "";
		if(!enableColor && TextFormatter.messageContainsColorsOrStyles(msg, true))
			emsg = "You are not permitted to use colors";
		if(!enableStyle && TextFormatter.messageContainsColorsOrStyles(msg, false))
			emsg += !emsg.isEmpty() ? " or styles" : "You are not permitted to use styles";
		if(!emsg.isEmpty()) {
			MutableComponent ecmp = Component.literal(emsg + "!");
			ecmp.withStyle(ChatFormatting.BOLD);
			ecmp.withStyle(ChatFormatting.RED);
			player.sendSystemMessage(ecmp);
		}
		// Convert markdown to normal essentials formatting
		if(markdownEnabled && enableStyle && PermissionsHandler.playerHasPermission(uuid, PermissionsHandler.markdownChatNode))
			msg = MarkdownFormatter.markdownStringToFormattedString(String.valueOf(msg));
		// Start generating the main TextComponent
		Component msgComp = TextFormatter.stringToFormattedText(String.valueOf(msg), enableColor, enableStyle);
		// Append the hover and click event crap
		Style sty = getHoverClickEventStyle(e.getMessage());
		MutableComponent ecmp = Component.literal("");
		if(sty != null && sty.getHoverEvent() != null)
			ecmp.setStyle(sty);
		e.setMessage (Component.literal((beforeMsg).toString() + msgComp.toString()+ afterMsg.toString()));
    }
}