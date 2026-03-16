package io.github.davidutzdev.autologin.mixin.client;

import io.github.davidutzdev.autologin.access.ServerInfoAutoLogin;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerInfo.class)
public class ServerInfoMixin implements ServerInfoAutoLogin {

    @Unique
    private String serverLoginCommand = "";

    @Inject(method = "toNbt", at = @At("RETURN"))
    private void saveLoginCommand(CallbackInfoReturnable<NbtCompound> cir) {
        NbtCompound nbt = cir.getReturnValue();
        nbt.putString("serverLoginCommand", serverLoginCommand);
    }

    @Inject(method = "fromNbt", at = @At("RETURN"))
    private static void loadLoginCommand(NbtCompound root, CallbackInfoReturnable<ServerInfo> cir) {
        ServerInfo info = cir.getReturnValue();

        if (root.contains("serverLoginCommand")) {
            ((ServerInfoMixin)(Object)info).serverLoginCommand = root.getString("serverLoginCommand").orElseGet(() -> "");
        }
    }

    @Override
    public String autoLogin$getServerLoginCommand() {
        return serverLoginCommand;
    }

    @Override
    public void autoLogin$setServerLoginCommand(String command) {
        this.serverLoginCommand = command;
    }

    @Inject(method = "copyFrom", at = @At("TAIL"))
    public void copyFromLoginCommand(ServerInfo serverInfo, CallbackInfo ci) {
        this.serverLoginCommand = ((ServerInfoAutoLogin) serverInfo).autoLogin$getServerLoginCommand();
    }
}
