package io.github.davidutzdev.autologin;

import io.github.davidutzdev.autologin.access.ServerInfoAutoLogin;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;

public class AutoLoginClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            try {
                final ServerInfo info = client.getCurrentServerEntry();

                if (!(info instanceof ServerInfoAutoLogin autoLogin)) return;

                final String loginCommand = autoLogin.autoLogin$getServerLoginCommand();
                if (loginCommand == null || loginCommand.isBlank()) return;

                client.execute(() -> {
                    if (client.player == null) return;

                    final String command = loginCommand.startsWith("/") ? loginCommand.substring(1) : loginCommand;
                    client.player.networkHandler.sendChatCommand(command);
                    client.player.sendMessage(Text.literal("§aAuto-login command sent."), false);
                });

            } catch (Exception e) {
                if (client.player != null) {
                    client.player.sendMessage(Text.literal("§cAuto-login failed: " + e.getMessage()), false);
                }
            }
        });
    }
}
