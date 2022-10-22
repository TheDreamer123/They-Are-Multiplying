package net.dreamer.theyaremultiplying.mixin;

import net.dreamer.theyaremultiplying.TheyAreMultiplyingAccessor;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity {
    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType,World world) {
        super(entityType,world);
    }

    @Inject(at = @At("HEAD"), method = "mobTick")
    public void mobTickInject(CallbackInfo info) {
        if(this instanceof TheyAreMultiplyingAccessor yes)
            if (yes.getBreedingAge() != 0) yes.setLoveTicks(0);
    }

    @Inject(at = @At("HEAD"), method = "interactMob", cancellable = true)
    public void interactMobInject(PlayerEntity player,Hand hand,CallbackInfoReturnable<ActionResult> cir) {
        ItemStack itemStack = player.getStackInHand(hand);
        if(this instanceof TheyAreMultiplyingAccessor yes)
            if(yes.isBreedingItem(itemStack)) {
                int i = yes.getBreedingAge();
                if(!this.world.isClient && i == 0 && yes.canEat()) {
                    yes.setLoveTicks(600);
                    if(!player.getAbilities().creativeMode) itemStack.decrement(1);
                    this.world.sendEntityStatus(this,(byte) 18);
                    cir.setReturnValue(ActionResult.SUCCESS);
                }
            }
    }

    @Inject(at = @At("HEAD"), method = "handleStatus", cancellable = true)
    public void handleStatusInject(byte status,CallbackInfo info) {
        if (status == 18) {
            for(int i = 0; i < 7; ++i) {
                double d = this.random.nextGaussian() * 0.02D;
                double e = this.random.nextGaussian() * 0.02D;
                double f = this.random.nextGaussian() * 0.02D;
                this.world.addParticle(ParticleTypes.HEART, this.getParticleX(1.0D), this.getRandomBodyY() + 0.5D, this.getParticleZ(1.0D), d, e, f);
            }
            info.cancel();
        }
    }

    @Inject(at = @At("TAIL"), method = "tickMovement")
    public void tickMovementInject(CallbackInfo info) {
        if(this instanceof TheyAreMultiplyingAccessor yes) {
            MobEntity mob = (MobEntity) (Object) this;
            if(mob instanceof SlimeEntity) {
                if (yes.getBreedingAge() != 0) yes.setLoveTicks(0);

                if (yes.getLoveTicks() > 0) yes.setLoveTicks(yes.getLoveTicks() - 1);
                if (yes.getBreedingAge() > 0) yes.setBreedingAge(yes.getBreedingAge() - 1);
            }
        }
    }
}
