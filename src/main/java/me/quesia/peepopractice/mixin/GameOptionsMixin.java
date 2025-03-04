package me.quesia.peepopractice.mixin;

import me.quesia.peepopractice.core.category.utils.KeyBindingUtils;
import me.quesia.peepopractice.core.category.utils.StandardSettingsUtils;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.options.Option;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameOptions.class)
public abstract class GameOptionsMixin {
    @Mutable @Shadow @Final public KeyBinding[] keysAll;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void peepoPractice$setRenderDistanceMax(CallbackInfo ci) {
        StandardSettingsUtils.RENDER_DISTANCE.setMax((float) Option.RENDER_DISTANCE.getMax());
    }

    @Inject(method = "load", at = @At("HEAD"))
    private void peepoPractice$registerKeyBindings(CallbackInfo ci) {
        this.keysAll = KeyBindingUtils.process(this.keysAll);
    }
}
