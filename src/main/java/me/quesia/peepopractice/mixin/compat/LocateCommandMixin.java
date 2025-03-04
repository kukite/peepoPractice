package me.quesia.peepopractice.mixin.compat;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.quesia.peepopractice.PeepoPractice;
import net.minecraft.server.command.LocateCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.world.gen.feature.StructureFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocateCommand.class)
public abstract class LocateCommandMixin {

    private static final SimpleCommandExceptionType PEEPO_PRACTICE$AFFECTED_EXCEPTION = new SimpleCommandExceptionType(new LiteralText("This structure cannot be located as it is affected by the current practice category."));

    @Inject(method = "execute", at = @At("HEAD"))
    private static void peepoPractice$cancelAffectedLocate(ServerCommandSource source, StructureFeature<?> structureFeature, CallbackInfoReturnable<Integer> cir) throws CommandSyntaxException {
        if (PeepoPractice.CATEGORY.hasStructureProperties() && PeepoPractice.CATEGORY.findStructureProperties(structureFeature) != null) {
            throw PEEPO_PRACTICE$AFFECTED_EXCEPTION.create();
        }
    }
}
