package net.dreamer.theyaremultiplying;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

public interface TheyAreMultiplyingAccessor {
    boolean isInLove();
    boolean canBreedWith(MobEntity entity);
    void breed(ServerWorld world,MobEntity other);
    int getBreedingAge();
    void setLoveTicks(int loveTicks);
    boolean canEat();
    boolean isBreedingItem(ItemStack stack);
    void setBreedingAge(int breedingAge);
    int getLoveTicks();
}
