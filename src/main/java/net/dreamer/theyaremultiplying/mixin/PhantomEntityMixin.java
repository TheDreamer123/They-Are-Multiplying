package net.dreamer.theyaremultiplying.mixin;

import net.dreamer.theyaremultiplying.TheyAreMultiplyingAccessor;
import net.dreamer.theyaremultiplying.util.CursedMatingGoal;
import net.dreamer.theyaremultiplying.util.FoodTagCreator;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.mob.FlyingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PhantomEntity.class)
public abstract class PhantomEntityMixin extends FlyingEntity implements TheyAreMultiplyingAccessor {
    @Shadow @Final private static TrackedData<Integer> SIZE;
    @Shadow public abstract int getPhantomSize();

    private int loveTicks;
    protected int breedingAge;

    protected PhantomEntityMixin(EntityType<? extends FlyingEntity> entityType,World world) {
        super(entityType,world);
    }

    @Override
    public boolean isInLove() {
        return this.loveTicks > 0;
    }

    @Override
    public boolean canBreedWith(MobEntity entity) {
        if(entity == this) return false;
        else if (entity.getClass() != this.getClass()) return false;
        else return this.isInLove() && ((TheyAreMultiplyingAccessor) entity).isInLove();
    }

    @Override
    public void breed(ServerWorld world,MobEntity other) {
        if(!(other instanceof TheyAreMultiplyingAccessor)) return;

        MobEntity abomination = (MobEntity) this.getType().create(world);
        if(abomination != null) {
            this.setBreedingAge(6000);
            ((TheyAreMultiplyingAccessor) other).setBreedingAge(6000);
            this.setLoveTicks(0);
            ((TheyAreMultiplyingAccessor) other).setLoveTicks(0);
            ((PhantomEntity) abomination).setPhantomSize(-2);
            abomination.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), 0.0F, 0.0F);
            world.spawnEntityAndPassengers(abomination);
            world.sendEntityStatus(this, (byte)18);
            if(world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT))
                world.spawnEntity(new ExperienceOrbEntity(world, this.getX(), this.getY(), this.getZ(), this.getRandom().nextInt(7) + 1));
        }
    }

    @Override
    public int getBreedingAge() {
        return this.breedingAge;
    }

    @Override
    public void setLoveTicks(int loveTicks) {
        this.loveTicks = loveTicks;
    }

    @Override
    public boolean canEat() {
        return this.loveTicks <= 0 && getPhantomSize() > -1;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.isIn(FoodTagCreator.createMobFeedingTag(Registry.ENTITY_TYPE.getId(this.getType()),this));
    }

    @Override
    public void setBreedingAge(int breedingAge) {
        this.breedingAge = breedingAge;
    }

    @Override
    public int getLoveTicks() {
        return this.loveTicks;
    }

    @Inject(at = @At("TAIL"), method = "initGoals")
    public void initGoalsInject(CallbackInfo info) {
        this.goalSelector.add(0,new CursedMatingGoal(this,1.0D));
    }

    @Inject(at = @At("TAIL"), method = "tickMovement")
    public void tickMovementInject(CallbackInfo info) {
        if(this.getBreedingAge() != 0) this.loveTicks = 0;

        if(loveTicks > 0) this.loveTicks--;
        if(breedingAge > 0) this.breedingAge--;
    }

    @Inject(at = @At("TAIL"), method = "writeCustomDataToNbt")
    public void writeCustomDataToNbtInject(NbtCompound nbt,CallbackInfo info) {
        nbt.putInt("InLove", this.loveTicks);
    }

    @Inject(at = @At("TAIL"), method = "readCustomDataFromNbt")
    public void readCustomDataFromNbtInject(NbtCompound nbt,CallbackInfo info) {
        this.loveTicks = nbt.getInt("InLove");
    }

    @Inject(at = @At("HEAD"), method = "setPhantomSize", cancellable = true)
    public void setPhantomSizeInject(int size,CallbackInfo info) {
        this.dataTracker.set(SIZE, MathHelper.clamp(size, Integer.MIN_VALUE, Integer.MAX_VALUE));
        info.cancel();
    }
}
