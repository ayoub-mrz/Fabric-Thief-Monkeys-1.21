package net.ayoubmrz.thiefmonkeymod.entity.ai;

import net.ayoubmrz.thiefmonkeymod.entity.custom.ThiefMonkeyEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;

import java.util.Random;

public class ThiefMonkeyMeleeAttackGoal extends Goal {
    protected final ThiefMonkeyEntity mob;
    private final double speed;
    private final boolean pauseWhenMobIdle;
    private Path path;
    private double targetX;
    private double targetY;
    private double targetZ;
    private int updateCountdownTicks;
    private int cooldown;
    private long lastUpdateTime;

    Random random = new Random();
    private int startAttacking = random.nextInt(15, 31);
    private boolean hasStartedAttackPhase = false;

    public ThiefMonkeyMeleeAttackGoal(ThiefMonkeyEntity mob, double speed, boolean pauseWhenMobIdle) {
        this.mob = mob;
        this.speed = speed;
        this.pauseWhenMobIdle = pauseWhenMobIdle;
    }

    public boolean canStart() {
        LivingEntity livingEntity = this.mob.getTarget();

        if (livingEntity == null) {
            hasStartedAttackPhase = false;
            return false;
        }

        if (!hasStartedAttackPhase && this.mob.getAttackCooldown() < (startAttacking * 10)) {
            this.mob.setAttackCooldown(this.mob.getAttackCooldown() + 1);
            return false;
        }
        if (!hasStartedAttackPhase) {
            hasStartedAttackPhase = true;
        }

        long l = this.mob.getWorld().getTime();
        if (l - this.lastUpdateTime < 20L) {
            return false;
        } else {
            this.lastUpdateTime = l;
            if (this.mob.isBaby()) {
                return false;
            } else if (this.mob.hasAttacked()) {
                return false;
            } else if (!livingEntity.isAlive()) {
                return false;
            } else {
                this.path = this.mob.getNavigation().findPathTo(livingEntity, 0);
                if (this.path != null) {
                    return true;
                } else {
                    return this.mob.isInAttackRange(livingEntity);
                }
            }
        }
    }

    public boolean shouldContinue() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (livingEntity == null) {
            return false;
        } else if (!livingEntity.isAlive()) {
            return false;
        } else if (this.mob.hasAttacked()) {
            return false;
        } else if (!this.pauseWhenMobIdle) {
            return !this.mob.getNavigation().isIdle();
        } else if (!this.mob.isInPositionTargetRange(livingEntity.getBlockPos())) {
            return false;
        } else {
            return !(livingEntity instanceof PlayerEntity) || !livingEntity.isSpectator() && !((PlayerEntity)livingEntity).isCreative();
        }
    }

    public void start() {
        this.mob.getNavigation().startMovingAlong(this.path, this.speed);
        this.mob.setAttacking(true);
        this.updateCountdownTicks = 0;
        this.cooldown = 0;
    }

    public void stop() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (!EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(livingEntity)) {
            this.mob.setTarget((LivingEntity)null);
        }

        this.mob.setAttacking(false);
        this.mob.getNavigation().stop();
    }

    public boolean shouldRunEveryTick() { return true; }

    public void tick() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (livingEntity != null) {
            this.mob.getLookControl().lookAt(livingEntity, 30.0F, 30.0F);
            this.updateCountdownTicks = Math.max(this.updateCountdownTicks - 1, 0);
            if ((this.pauseWhenMobIdle || this.mob.getVisibilityCache().canSee(livingEntity)) && this.updateCountdownTicks <= 0 && (this.targetX == (double)0.0F && this.targetY == (double)0.0F && this.targetZ == (double)0.0F || livingEntity.squaredDistanceTo(this.targetX, this.targetY, this.targetZ) >= (double)1.0F || this.mob.getRandom().nextFloat() < 0.05F)) {
                this.targetX = livingEntity.getX();
                this.targetY = livingEntity.getY();
                this.targetZ = livingEntity.getZ();
                this.updateCountdownTicks = 4 + this.mob.getRandom().nextInt(7);
                double d = this.mob.squaredDistanceTo(livingEntity);
                if (d > (double)1024.0F) {
                    this.updateCountdownTicks += 10;
                } else if (d > (double)256.0F) {
                    this.updateCountdownTicks += 5;
                }

                if (!this.mob.getNavigation().startMovingTo(livingEntity, this.speed)) {
                    this.updateCountdownTicks += 15;
                }

                this.updateCountdownTicks = this.getTickCount(this.updateCountdownTicks);
            }

            this.cooldown = Math.max(this.cooldown - 1, 0);
            this.attack(livingEntity);
        }

    }

    protected void attack(LivingEntity target) {
        if (this.canAttack(target)) {
            ServerWorld serverWorld = (ServerWorld) target.getWorld();
            this.resetCooldown();
            this.mob.swingHand(Hand.MAIN_HAND);
            this.mob.tryAttack(serverWorld, target);

            this.mob.setAttackCooldown(0);
            this.hasStartedAttackPhase = false;
            startAttacking = random.nextInt(30, 81);

        }

    }

    protected void resetCooldown() { this.cooldown = this.getTickCount(20); }

    protected boolean isCooledDown() { return this.cooldown <= 0; }

    protected boolean canAttack(LivingEntity target) {
        return this.isCooledDown() && this.mob.isInAttackRange(target) && this.mob.getVisibilityCache().canSee(target);
    }

}
