package net.deadlydiamond98.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.deadlydiamond98.util.ExtraHealthHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @ModifyVariable(method = "renderHealthBar", at = @At("HEAD"), argsOnly = true)
    private float modifyMaxHealth(float maxHealth, @Local(argsOnly = true) PlayerEntity player) {
        return maxHealth - ExtraHealthHelper.getBarrierHealth(player);
    }

    @ModifyVariable(method = "renderHealthBar", at = @At("HEAD"), argsOnly = true, ordinal = 6)
    private int modifyAbsorb(int absorb, @Local(argsOnly = true) PlayerEntity player) {
        return absorb + ExtraHealthHelper.getBarrierHealth(player);
    }

    @ModifyVariable(method = "renderHealthBar", at = @At("HEAD"), argsOnly = true, ordinal = 4)
    private int modifyHealth(int health, @Local(argsOnly = true) PlayerEntity player) {
        return health - ExtraHealthHelper.getBarrierHealth(player);
    }
}
