package com.jeremiahbl.bfcmod.events;

import com.jeremiahbl.bfcmod.config.ConfigHandler;
import com.jeremiahbl.bfcmod.config.IReloadable;
import com.jeremiahbl.bfcmod.config.PermissionsHandler;
import com.jeremiahbl.bfcmod.config.PlayerData;
import com.jeremiahbl.bfcmod.utils.BetterForgeChatUtilities;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
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
	
	public void reloadConfigOptions() {
		enableNicknamesInTabList = ConfigHandler.config.enableNicknamesInTabList.get();
		enableMetadataInTabList = ConfigHandler.config.enableMetadataInTabList.get();
	}
	
	@SubscribeEvent
	public void onTabListNameFormatEvent(TabListNameFormat e) {
		if(ConfigHandler.config.enableTabListIntegration.get()) {
			Player player = e.getPlayer();
			if(player instanceof ServerPlayer) {
				ServerPlayer sexyPlayer = (ServerPlayer) player;
				e.setDisplayName(BetterForgeChatUtilities.getFormattedPlayerName(sexyPlayer, 
						enableNicknamesInTabList && PermissionsHandler.playerHasPermission(sexyPlayer, PermissionsHandler.tabListNicknameNode),  
						enableMetadataInTabList  && PermissionsHandler.playerHasPermission(sexyPlayer, PermissionsHandler.tabListMetadataNode)));
			}
		}
	}
	@SubscribeEvent
	public void onNameFormatEvent(NameFormat e) {
		Player player = e.getPlayer();
		if(player instanceof ServerPlayer) 
			e.setDisplayname(BetterForgeChatUtilities.getFormattedPlayerName((ServerPlayer) player));
	}
	@SubscribeEvent
	public void onSavePlayerData(SaveToFile e) {
		PlayerData.saveToDir(e.getPlayerDirectory());
	}
	@SubscribeEvent
	public void onLoadPlayerData(LoadFromFile e) {
		PlayerData.loadFromDir(e.getPlayerDirectory());
	}
	/*@SubscribeEvent
	public void onPermissionsChanged(PermissionsChangedEvent e) {
		Player player = e.getPlayer();
		if(player instanceof ServerPlayer) {
			// Update tab list name so if the players group changes or there tab
			// list permissions are revoked the tab list name gets updated
			ServerPlayer sexyPlayer = (ServerPlayer) player;
			sexyPlayer.refreshTabListName();
		}
	}*/
}
