package io.github.davidutzdev.autologin.mixin.client;

import io.github.davidutzdev.autologin.access.ServerInfoAutoLogin;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.AddServerScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AddServerScreen.class)
public abstract class AddServerScreenMixin extends Screen {

    @Shadow
    private TextFieldWidget addressField;

    @Shadow
    @Final
    private ServerInfo server;
    private TextFieldWidget commandField;

    protected AddServerScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        final int GAP = 10;
        final int LABEL_HEIGHT = 9;
        final int FIELD_HEIGHT = 20;
        final int TOTAL = GAP + LABEL_HEIGHT + FIELD_HEIGHT;

        int labelY = this.addressField.getY() + this.addressField.getHeight() + GAP;
        int fieldY  = labelY + LABEL_HEIGHT + 2;

        commandField = new TextFieldWidget(
                this.textRenderer,
                this.width / 2 - 100,
                fieldY,
                200,
                20,
                Text.literal("Login Command")
        );
        commandField.setMaxLength(256);

        String existing = ((ServerInfoAutoLogin) this.server).autoLogin$getServerLoginCommand();
        commandField.setText(existing != null ? existing : "");

        this.addDrawableChild(commandField);

        // Shift ALL ClickableWidgets that are below the address field
        for (Element element : this.children()) {
            if (element instanceof ClickableWidget widget && widget != commandField) {
                if (widget.getY() > this.addressField.getY()) {
                    widget.setY(widget.getY() + TOTAL);
                }
            }
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        final int GAP = 10;
        final int labelY = this.addressField.getY() + this.addressField.getHeight() + GAP;
        context.drawTextWithShadow(
                this.textRenderer,
                Text.literal("Login Command"),
                this.width / 2 - 100 + 1,
                labelY,
                -6250336
        );
    }

    @Inject(method = "resize", at = @At("HEAD"))
    private void beforeResize(int width, int height, CallbackInfo ci) {
        if (commandField != null) {
            ((ServerInfoAutoLogin) this.server).autoLogin$setServerLoginCommand(commandField.getText());
        }
    }

    @Inject(method = "addAndClose", at = @At("HEAD"))
    private void addAndClose(CallbackInfo ci) {
        final String loginCommand = this.commandField.getText();

        ((ServerInfoAutoLogin) this.server).autoLogin$setServerLoginCommand(loginCommand);
    }
}
