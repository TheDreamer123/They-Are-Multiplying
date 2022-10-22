package net.dreamer.theyaremultiplying.mixin;

import net.dreamer.theyaremultiplying.TheyAreMultiplyingAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type,World world) {
        super(type,world);
    }

    @Inject(at = @At("RETURN"), method = "damage")
    public void damageInject(DamageSource source,float amount,CallbackInfoReturnable<Boolean> cir) {
        if(!this.isInvulnerableTo(source))
            if(this instanceof TheyAreMultiplyingAccessor yes) {
                LivingEntity living = (LivingEntity) (Object) this;
                if (living instanceof PhantomEntity) yes.setLoveTicks(0);
            }
    }
}
