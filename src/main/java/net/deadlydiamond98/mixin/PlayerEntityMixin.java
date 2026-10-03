package net.deadlydiamond98.mixin;

import net.deadlydiamond98.misc.HealPGoodAdvancements;
import net.deadlydiamond98.util.ExtraHealthHelper;
import net.deadlydiamond98.util.IPlayerHealth;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements IPlayerHealth {

    @Unique
    private int tempHealth, permHealth;

    @Unique
    private final static TrackedData<Integer> BARRIER_HEALTH = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);

    @Inject(method = "tick", at = @At("HEAD"))
    public void healpgood$tick(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        if (tempHealth == 20 && permHealth == 20 && !player.getWorld().isClient()) {
            HealPGoodAdvancements.MAX_HEALTH.trigger((ServerPlayerEntity) player);
        }
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    public void healpgood$writeCustomDataToNbt(NbtCompound nbt, CallbackInfo info) {
        nbt.putInt("tempHealth", tempHealth);
        nbt.putInt("permHealth", permHealth);
        nbt.putInt("barrierHealth", ((PlayerEntity)(Object)this).getDataTracker().get(BARRIER_HEALTH));
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    public void healpgood$readCustomDataFromNbt(NbtCompound nbt, CallbackInfo info) {
        this.tempHealth = nbt.getInt("tempHealth");
        this.permHealth = nbt.getInt("permHealth");
        ((PlayerEntity)(Object)this).getDataTracker().set(BARRIER_HEALTH, nbt.getInt("barrierHealth"));
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void healpgood$addCustomData(CallbackInfo ci) {
        ((PlayerEntity)(Object)this).getDataTracker().startTracking(BARRIER_HEALTH, 0);
    }

    @Override
    public void healpgood$setTempHealth(int value) {
        this.tempHealth = value;
    }

    @Override
    public int healpgood$getTempHealth() {
        return this.tempHealth;
    }

    @Override
    public void healpgood$setPermHealth(int value) {
        this.permHealth = value;
    }

    @Override
    public int healpgood$getPermHealth() {
        return this.permHealth;
    }

    @Override
    public void healpgood$setBarrierHealth(int value) {
        ((PlayerEntity)(Object)this).getDataTracker().set(BARRIER_HEALTH, value);
    }

    @Override
    public int healpgood$getBarrierHealth() {
        return ((PlayerEntity)(Object)this).getDataTracker().get(BARRIER_HEALTH);
    }

    @Inject(method = "applyDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setHealth(F)V", shift = At.Shift.AFTER))
    private void removeBarrierHealth(DamageSource source, float amount, CallbackInfo ci) {
        ExtraHealthHelper.removeBarrierHealth((PlayerEntity) (Object) this, MathHelper.floor(amount));
    }
}
