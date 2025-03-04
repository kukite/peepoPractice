package me.quesia.peepopractice.mixin.world;

import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.category.utils.InventoryUtils;
import me.quesia.peepopractice.core.category.PracticeCategories;
import me.quesia.peepopractice.core.global.GlobalOptions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {
    @ModifyVariable(method = "onPlayerConnect", at = @At("STORE"))
    private RegistryKey<World> peepoPractice$otherDimension(RegistryKey<World> value) {
        if (PeepoPractice.CATEGORY.hasWorldProperties() && PeepoPractice.CATEGORY.getWorldProperties().hasWorldRegistryKey()) {
            return PeepoPractice.CATEGORY.getWorldProperties().getWorldRegistryKey();
        }
        return value;
    }

    @Inject(method = "onPlayerConnect", at = @At("TAIL"))
    private void peepoPractice$setInventory(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        if (!PeepoPractice.CATEGORY.equals(PracticeCategories.EMPTY)) {
            MinecraftClient client = MinecraftClient.getInstance();
            client.inGameHud.setTitles(null, null, -1, -1, -1);
            if (GlobalOptions.SAME_INVENTORY.get(client.options) && !InventoryUtils.PREVIOUS_INVENTORY.isEmpty()) {
                PeepoPractice.log("Using inventory from previous split.");
                player.inventory.clear();
                for (int i = 0; i < InventoryUtils.PREVIOUS_INVENTORY.size(); i++) {
                    player.inventory.setStack(i, InventoryUtils.PREVIOUS_INVENTORY.get(i).copy());
                }
            } else {
                PeepoPractice.log("Using configured inventory.");
                InventoryUtils.putItems(player.inventory, PeepoPractice.CATEGORY);
            }
            if (GlobalOptions.GIVE_SATURATION.get(client.options)) {
                player.getHungerManager().setSaturationLevelClient(10.0F);
            }
        }
    }
}
