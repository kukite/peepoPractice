package me.quesia.peepopractice.mixin;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.datafixers.DataFixer;
import com.redlimerl.speedrunigt.timer.InGameTimer;
import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.category.utils.InventoryUtils;
import me.quesia.peepopractice.core.category.PracticeCategories;
import me.quesia.peepopractice.core.category.PracticeCategory;
import me.quesia.peepopractice.core.category.utils.StandardSettingsUtils;
import me.quesia.peepopractice.core.global.GlobalOptions;
import me.quesia.peepopractice.core.resource.LocalResourceManager;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.level.storage.LevelStorage;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow @Final public File runDirectory;
    @Shadow @Final private DataFixer dataFixer;
    @Shadow @Nullable public ClientWorld world;
    @Shadow public abstract boolean isIntegratedServerRunning();
    @Shadow public abstract void openScreen(@Nullable Screen screen);
    @Shadow @Final public GameOptions options;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;runTasks()V", shift = At.Shift.AFTER))
    private void peepoPractice$runMoreTasks(CallbackInfo ci) {
        LocalResourceManager.INSTANCE.submit(() -> LocalResourceManager.INSTANCE.tickTasks());
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void peepoPractice$getResources(CallbackInfo ci) {
        PeepoPractice.log("Reloading local resource manager...");

        LocalResourceManager.INSTANCE.submit(() -> {
            LocalResourceManager.INSTANCE.reload().whenComplete((serverResourceManager, throwable) -> {
                if (throwable != null) {
                    PeepoPractice.LOGGER.error(throwable);
                    return;
                }

                PeepoPractice.log("Done reloading local resource manager.");
                synchronized (PeepoPractice.SERVER_RESOURCE_MANAGER) {
                    PeepoPractice.SERVER_RESOURCE_MANAGER.set(serverResourceManager);
                    PeepoPractice.SERVER_RESOURCE_MANAGER.notifyAll();
                }
            });
        });

        PeepoPractice.PRACTICE_LEVEL_STORAGE = new LevelStorage(this.runDirectory.toPath().resolve("practiceSaves"), this.runDirectory.toPath().resolve("backups"), this.dataFixer);
    }

    @Inject(method = "openScreen", at = @At("HEAD"))
    private void peepoPractice$resetCategory(Screen screen, CallbackInfo ci) {
        if (screen instanceof TitleScreen) {
            if (PeepoPractice.RESET_CATEGORY) {
                PeepoPractice.CATEGORY = PracticeCategories.EMPTY;
                InventoryUtils.PREVIOUS_INVENTORY.clear();
            }
            PeepoPractice.RESET_CATEGORY = true;
        }
    }

    @ModifyReceiver(method = "startIntegratedServer(Ljava/lang/String;Lnet/minecraft/util/registry/RegistryTracker$Modifiable;Ljava/util/function/Function;Lcom/mojang/datafixers/util/Function4;ZLnet/minecraft/client/MinecraftClient$WorldLoadAction;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/LevelStorage;createSession(Ljava/lang/String;)Lnet/minecraft/world/level/storage/LevelStorage$Session;"))
    private LevelStorage peepoPractice$differentSavesFolder(LevelStorage storage, String worldName) {
        if (!PeepoPractice.CATEGORY.equals(PracticeCategories.EMPTY) && PeepoPractice.PRACTICE_LEVEL_STORAGE != null) {
            return PeepoPractice.PRACTICE_LEVEL_STORAGE;
        }
        return storage;
    }

    @ModifyReturnValue(method = "getLevelStorage", at = @At("RETURN"))
    private LevelStorage peepoPractice$customLevelStorage(LevelStorage storage) {
        if (!PeepoPractice.CATEGORY.equals(PracticeCategories.EMPTY) && PeepoPractice.PRACTICE_LEVEL_STORAGE != null) {
            return PeepoPractice.PRACTICE_LEVEL_STORAGE;
        }
        return storage;
    }

    @Inject(method = "joinWorld", at = @At("HEAD"))
    private void peepoPractice$disableAtumReset(CallbackInfo ci) {
        PeepoPractice.disableAtumReset();
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void peepoPractice$listenToKeyBindings(CallbackInfo ci) {
        if (this.world != null && this.isIntegratedServerRunning() && !PeepoPractice.CATEGORY.equals(PracticeCategories.EMPTY)) {
            PracticeCategory nextCategory = PeepoPractice.getNextCategory();
            boolean next = PeepoPractice.NEXT_SPLIT_KEY.isPressed() && nextCategory != null;
            if (PeepoPractice.REPLAY_SPLIT_KEY.isPressed() || next) {
                if (next && (FabricLoader.getInstance().isDevelopmentEnvironment() || InGameTimer.getInstance().isCompleted())) {
                    InventoryUtils.saveCurrentPlayerInventory();
                    PeepoPractice.CATEGORY = nextCategory;
                }
                this.openScreen(new CreateWorldScreen(null));
            }
        }
    }

    @Inject(method = "method_29607", at = @At("HEAD"))
    private void peepoPractice$resetSettings1(CallbackInfo ci) {
        PeepoPractice.log("Triggered first standard settings call for " + PeepoPractice.CATEGORY.getId());
        StandardSettingsUtils.triggerStandardSettings(PeepoPractice.CATEGORY);
    }

    @Inject(method = "method_29607", at = @At("TAIL"))
    private void peepoPractice$resetSettings2(CallbackInfo ci) {
        if (!PeepoPractice.HAS_STANDARD_SETTINGS) {
            PeepoPractice.log("Triggered second standard settings call for " + PeepoPractice.CATEGORY.getId());
            StandardSettingsUtils.triggerStandardSettings(PeepoPractice.CATEGORY);
        }
    }

    @ModifyReturnValue(method = "getWindowTitle", at = @At("RETURN"))
    private String peepoPractice$appendTitle(String value) {
        if (GlobalOptions.CHANGE_WINDOW_TITLE.get(this.options)) {
            return value + " (Practice)";
        }
        return value;
    }
}
