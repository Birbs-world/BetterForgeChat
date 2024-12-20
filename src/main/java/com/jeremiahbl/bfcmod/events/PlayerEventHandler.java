package com.jeremiahbl.bfcmod.events;

import com.jeremiahbl.bfcmod.config.ConfigHandler;
import com.jeremiahbl.bfcmod.config.IReloadable;
import com.jeremiahbl.bfcmod.config.PermissionsHandler;
import com.jeremiahbl.bfcmod.config.PlayerData;
import com.jeremiahbl.bfcmod.utils.BetterForgeChatUtilities;
import com.mojang.authlib.GameProfile;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent.LoadFromFile;
import net.minecraftforge.event.entity.player.PlayerEvent.NameFormat;
import net.minecraftforge.event.entity.player.PlayerEvent.SaveToFile;
import net.minecraftforge.event.entity.player.PlayerEvent.TabListNameFormat;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class PlayerEventHandler implements IReloadable {
	private boolean enableNicknamesInTabList = false;
	private boolean enableMetadataInTabList = false;
	
        @Override
	public void reloadConfigOptions() {
		enableNicknamesInTabList = ConfigHandler.config.enableNicknamesInTabList.get();
		enableMetadataInTabList = ConfigHandler.config.enableMetadataInTabList.get();
	}
	
	@SubscribeEvent
	public void onTabListNameFormatEvent(TabListNameFormat e) { 
		if(ConfigHandler.config.enableTabListIntegration.get() && e.getEntity() != null && e.getEntity() instanceof ServerPlayer) {
			GameProfile player = e.getEntity().getGameProfile();
			e.setDisplayName(BetterForgeChatUtilities.getFormattedPlayerName(player, 
				enableNicknamesInTabList && PermissionsHandler.playerHasPermission(player.getId(), PermissionsHandler.tabListNicknameNode),  
				enableMetadataInTabList  && PermissionsHandler.playerHasPermission(player.getId(), PermissionsHandler.tabListMetadataNode)));
		}
	}
	@SubscribeEvent
	public void onNameFormatEvent(NameFormat e) {
		if(e.getEntity() != null && e.getEntity() instanceof ServerPlayer)
			e.setDisplayname(BetterForgeChatUtilities.getFormattedPlayerName(e.getEntity().getGameProfile()));
	}
	@SubscribeEvent
	public void onSavePlayerData(SaveToFile e) {
		PlayerData.saveToDir(e.getPlayerDirectory());
	}
	@SubscribeEvent
	public void onLoadPlayerData(LoadFromFile e) {
		PlayerData.loadFromDir(e.getPlayerDirectory());
	}
}
