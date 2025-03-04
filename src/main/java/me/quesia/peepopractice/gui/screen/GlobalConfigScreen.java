package me.quesia.peepopractice.gui.screen;

import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.PracticeWriter;
import me.quesia.peepopractice.core.global.GlobalOptions;
import me.quesia.peepopractice.gui.widget.LimitlessButtonWidget;
import me.quesia.peepopractice.gui.widget.LimitlessDoubleOptionSliderWidget;
import me.quesia.peepopractice.mixin.access.ButtonWidgetAccessor;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.BooleanOption;
import net.minecraft.client.options.DoubleOption;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

public class GlobalConfigScreen extends Screen {
    private static final DoubleOption[] BACKGROUND_OPTIONS = {
            GlobalOptions.BACKGROUND_RED,
            GlobalOptions.BACKGROUND_GREEN,
            GlobalOptions.BACKGROUND_BLUE
    };
    private static final BooleanOption[] OPTIONS = {
            GlobalOptions.SAME_INVENTORY,
            GlobalOptions.CHANGE_WINDOW_TITLE,
            GlobalOptions.GIVE_SATURATION
    };

    public GlobalConfigScreen() {
        super(new LiteralText("Global Config"));
    }

    @Override
    protected void init() {
        if (this.client == null) { return; }

        GlobalOptions.SAME_INVENTORY.setTooltip(this.client.textRenderer.wrapLines(new LiteralText("If enabled, whenever you press 'Next Split' you will continue with the inventory from the previous split."), 200));
        GlobalOptions.CHANGE_WINDOW_TITLE.setTooltip(this.client.textRenderer.wrapLines(new LiteralText("If enabled, the text '(Practice)' will be appended to the game's window title."), 200));
        GlobalOptions.GIVE_SATURATION.setTooltip(this.client.textRenderer.wrapLines(new LiteralText("If enabled, you'll get 10 saturation when you spawn in."), 200));

        int btnWidth = this.width / 2;
        int btnHeight = this.height / 8;
        int yOffset = btnHeight / 8;

        for (int i = -1; i <= 1; i++) {
            DoubleOption option = BACKGROUND_OPTIONS[i + 1];
            int actualWidth = btnWidth / BACKGROUND_OPTIONS.length + 1;
            this.addButton(new LimitlessDoubleOptionSliderWidget(this.client.options, this.width / 2 + actualWidth * i - actualWidth / 2, 8 + btnHeight, actualWidth, btnHeight, option));
        }

        int index = 0;
        for (BooleanOption option : OPTIONS) {
            index++;
            int x = this.width / 2 - btnWidth / 2;
            int y = 8 + btnHeight + yOffset * index + btnHeight * index;
            AbstractButtonWidget button = option.createButton(this.client.options, x, y, btnWidth);
            ButtonWidget.PressAction action = ((ButtonWidgetAccessor) button).getOnPress();
            this.addButton(new LimitlessButtonWidget(x, y, btnWidth, btnHeight, button.getMessage(), action, option.getTooltip().isPresent() ? (button1, matrices, mouseX, mouseY) -> this.renderTooltip(matrices, option.getTooltip().get(), mouseX, mouseY) : null));
        }

        this.addButton(new LimitlessButtonWidget(this.width / 2 - btnWidth / 2, this.height - btnHeight - yOffset * 4, btnWidth, btnHeight, ScreenTexts.DONE, b -> this.onClose()));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.fillGradient(matrices, 0, 0, this.width, this.height, PeepoPractice.BACKGROUND_COLOUR, PeepoPractice.BACKGROUND_COLOUR);
        super.render(matrices, mouseX, mouseY, delta);
        this.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 13, 16777215);
    }

    @Override
    public void tick() {
        PeepoPractice.BACKGROUND_COLOUR = PeepoPractice.updateBackgroundColor();
        super.tick();
    }

    @Override
    public void onClose() {
        PracticeWriter.GLOBAL_CONFIG.write();
        super.onClose();
    }
}
