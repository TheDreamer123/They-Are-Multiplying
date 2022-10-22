package net.dreamer.theyaremultiplying.util;

import net.dreamer.theyaremultiplying.TheyAreMultiplyingAccessor;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;

public class CursedMatingGoal extends Goal {
    private static final TargetPredicate VALID_MATE_PREDICATE = TargetPredicate.createNonAttackable().setBaseMaxDistance(8.0D).ignoreVisibility();
    protected final MobEntity entity;
    private final Class<? extends MobEntity> entityClass;
    protected final World world;
    @Nullable protected MobEntity mate;
    private int timer;
    private final double chance;

    public CursedMatingGoal(MobEntity entity, double chance) {
        this(entity, chance, entity.getClass());
    }

    public CursedMatingGoal(MobEntity entity, double chance, Class<? extends MobEntity> entityClass) {
        this.entity = entity;
        this.world = entity.world;
        this.entityClass = entityClass;
        this.chance = chance;
        if(entity instanceof SlimeEntity) this.setControls(EnumSet.of(Control.JUMP, Control.MOVE));
        else this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    public boolean canStart() {
        if(!(this.entity instanceof TheyAreMultiplyingAccessor)) return false;
        if(!((TheyAreMultiplyingAccessor) this.entity).isInLove()) return false;
            else {
            this.mate = this.findMate();
            return this.mate != null;
        }
    }

    public boolean shouldContinue() {
        if(this.mate == null) return false;

        return this.mate.isAlive() && ((TheyAreMultiplyingAccessor) this.mate).isInLove() && this.timer < 60;
    }

    public void stop() {
        this.mate = null;
        this.timer = 0;
    }

    public void tick() {
        this.entity.getLookControl().lookAt(this.mate, 10.0F, (float)this.entity.getMaxLookPitchChange());
        this.entity.getNavigation().startMovingTo(this.mate, this.chance);
        ++this.timer;
        if(this.timer >= this.getTickCount(60) && this.entity.squaredDistanceTo(this.mate) < 9.0D) this.breed();
    }

    @Nullable
    private MobEntity findMate() {
        List<? extends MobEntity> list = this.world.getTargets(this.entityClass, VALID_MATE_PREDICATE, this.entity, this.entity.getBoundingBox().expand(8.0D));
        double d = 1.7976931348623157E308D;
        MobEntity entity = null;

        for(MobEntity entity2 : list) {
            if(!(this.entity instanceof TheyAreMultiplyingAccessor)) break;
            if(((TheyAreMultiplyingAccessor) this.entity).canBreedWith(entity2) && this.entity.squaredDistanceTo(entity2) < d) {
                entity = entity2;
                d = this.entity.squaredDistanceTo(entity2);
            }
        }

        return entity;
    }

    protected void breed() {
        if(this.entity instanceof TheyAreMultiplyingAccessor)
            ((TheyAreMultiplyingAccessor) this.entity).breed((ServerWorld)this.world, this.mate);
    }
}
