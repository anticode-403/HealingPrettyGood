package net.deadlydiamond98.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.deadlydiamond98.HealingPrettyGood;
import net.deadlydiamond98.util.ExtraHealthHelper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Unique
    private final Identifier BARRIER_HEARTS = new Identifier(HealingPrettyGood.MOD_ID, "textures/gui/barrier_hearts.png");

    @ModifyExpressionValue(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getHealth()F"))
    private float modifyHealth(float health, @Local PlayerEntity player) {
        return health - ExtraHealthHelper.getBarrierHealth(player);
    }

    @ModifyVariable(method = "renderHealthBar", at = @At("HEAD"), argsOnly = true)
    private boolean noBlinkingWhileBarrier(boolean blinking, @Local(argsOnly = true) PlayerEntity player) {
        if (ExtraHealthHelper.getBarrierHealth(player) > 0) return false;
        else return blinking;
    }

    @Inject(method = "renderHealthBar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;drawHeart(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/gui/hud/InGameHud$HeartType;IIIZZ)V", ordinal = 0, shift = At.Shift.AFTER))
    private void renderCustomBarrierHearts(DrawContext context, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking, CallbackInfo ci, @Local(ordinal = 10) int l, @Local(ordinal = 11) int m, @Local(ordinal = 14) int heartX, @Local(ordinal = 15) int heartY) {
        int barrier = ExtraHealthHelper.getBarrierHealth(player);
        int r = m * 2;
        int actualJ = MathHelper.ceil((double)(maxHealth - barrier) / (double)2.0F);
        int s = r - actualJ * 2;

        if (m >= actualJ && r > actualJ && r < maxHealth) {
            boolean halfHeart = s + 1 == barrier;
            context.drawTexture(BARRIER_HEARTS, heartX, heartY, halfHeart ? 9 : 0, player.getWorld().getLevelProperties().isHardcore() ? 9 : 0, 9, 9, 18, 17);
        }
    }
}
