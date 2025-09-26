package net.ayoubmrz.thiefmonkeymod.entity.custom;

import net.ayoubmrz.thiefmonkeymod.entity.ModEntities;
import net.ayoubmrz.thiefmonkeymod.entity.ai.ThiefMonkeyMeleeAttackGoal;
import net.ayoubmrz.thiefmonkeymod.item.ModItems;
import net.ayoubmrz.thiefmonkeymod.sound.ModSounds;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.*;

import java.util.Random;


public class ThiefMonkeyEntity extends AnimalEntity implements GeoEntity {

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    private static final TrackedData<Boolean> IS_SHOOTING = DataTracker.registerData(ThiefMonkeyEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private static final TrackedData<Boolean> HAS_ATTACKED = DataTracker.registerData(ThiefMonkeyEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private static final TrackedData<Integer> ATTACK_COOLDOWN = DataTracker.registerData(ThiefMonkeyEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private static final TrackedData<ItemStack> HELD_ITEM = DataTracker.registerData(ThiefMonkeyEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);

    private final SimpleInventory inventory = new SimpleInventory(1);

    private boolean isAttackWindingUp = false;
    private int windupTicks = 0;
    private boolean shouldRun = false;

    public ThiefMonkeyEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void tick() {
        super.tick();

        if (isAttackWindingUp) {
            windupTicks--;

            if (windupTicks <= 0) {
                performAttack();
                isAttackWindingUp = false;
            }
        }

        // Run Away From Player
        if (this.shouldRun) {
            PlayerEntity nearestPlayer = this.getWorld().getClosestPlayer(this, 6.0);
            if (nearestPlayer != null && !nearestPlayer.isCreative()
                    && !nearestPlayer.isSpectator()
                    && !nearestPlayer.getMainHandStack().isOf(ModItems.BANANA)
                    && !nearestPlayer.getMainHandStack().isOf(ModItems.BANANA)) {
                double deltaX = this.getX() - nearestPlayer.getX();
                double deltaZ = this.getZ() - nearestPlayer.getZ();
                double blocksToRun = 8.0;

                double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
                if (distance > 0) {
                    deltaX = (deltaX / distance) * blocksToRun;
                    deltaZ = (deltaZ / distance) * blocksToRun;

                    double fleeX = this.getX() + deltaX;
                    double fleeZ = this.getZ() + deltaZ;

                    this.getNavigation().startMovingTo(fleeX, this.getY(), fleeZ, 1.2);
                }
            }
        }

    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (itemStack.isOf(ModItems.BANANA)) {
            if (!this.getWorld().isClient) {
                // If the monkey has items in inventory, drop them first
                this.setHasAttacked(false);
                this.setAttackCooldown(0);
                this.shouldRun = false;

                boolean hasItems = false;
                for (int i = 0; i < inventory.size(); i++) {
                    ItemStack stack = inventory.getStack(i);
                    if (!stack.isEmpty()) {
                        Vec3d lookDirection = this.getRotationVec(1.0F);
                        Vec3d throwVelocity = lookDirection.multiply(0.3);

                        ItemEntity itemEntity = new ItemEntity(this.getWorld(), this.getX(), this.getY() + 0.5, this.getZ(), stack);
                        itemEntity.setVelocity(throwVelocity);
                        itemEntity.setPickupDelay(20);

                        this.getWorld().spawnEntity(itemEntity);
                        inventory.setStack(i, ItemStack.EMPTY);
                        hasItems = true;
                    }
                }

                if (hasItems) {
                    this.dataTracker.set(HELD_ITEM, ItemStack.EMPTY);
                }

                // If no items were dropped, proceed with normal feeding behavior
                if (!hasItems) {
                    return super.interactMob(player, hand);
                }

                if (!player.getAbilities().creativeMode) {
                    itemStack.decrement(1);
                }

            }
            return ActionResult.SUCCESS;
        }

        return super.interactMob(player, hand);
    }

    public void startAttackWindup() {
        this.isAttackWindingUp = true;
        this.windupTicks = 10;
    }

    private void performAttack() {
        LivingEntity target = this.getTarget();
        if (this.isAlive() && target != null && this.canSee(target)) {
            ServerWorld serverWorld = (ServerWorld) this.getWorld();

            // If target is a player, try to steal an item
            if (target instanceof PlayerEntity player) {
                this.setHasAttacked(true);
                stealRandomItem(player);
            }
            this.tryAttack(serverWorld, target);
        }
    }

    @Override
    public boolean tryAttack(ServerWorld world, Entity target) {
        if (!isAttackWindingUp) {
            startAttackWindup();
            return false;
        }
        return super.tryAttack(world, target);
    }

    private void stealRandomItem(PlayerEntity player) {

        // If Monkey Already Have Stolen Item
        if (!inventory.getStack(0).isEmpty()) {
            return;
        }

        DefaultedList<ItemStack> playerInventory = player.getInventory().getMainStacks();
        Random random = new Random();

        boolean hasItems = playerInventory.stream().anyMatch(stack -> !stack.isEmpty());
        if (!hasItems) {
            this.setHasAttacked(true);
            this.setAttackCooldown(0);
            this.shouldRun = true;
            return; // Player has no items to steal
        }

        int attempts = 0;

        while (true) {

            ++attempts;

            if (attempts >= 100) {
                break;
            }

            int randomSlot = random.nextInt(playerInventory.size());
            ItemStack stack = playerInventory.get(randomSlot);

            if (!stack.isEmpty()) {
                // Take the entire stack
                ItemStack stolenItem = stack.copy();
                playerInventory.set(randomSlot, ItemStack.EMPTY);

                // Add to monkey's inventory
                addToInventory(stolenItem);

                // Mark player inventory as dirty
                player.getInventory().markDirty();

                break;
            }
        }
        this.shouldRun = true;
    }

    private void addToInventory(ItemStack item) {
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.getStack(i).isEmpty()) {
                inventory.setStack(i, item);
                this.dataTracker.set(HELD_ITEM, item);
                break;
            }
        }
    }

    public SimpleInventory getInventory() {
        return inventory;
    }

    @Override
    protected void dropInventory(ServerWorld world) {
        super.dropInventory(world);
        // Drop all items from the monkey's inventory
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty()) {
                this.dropStack(world, stack);
            }
        }

        // Clear the inventory
        inventory.clear();
    }

    @Override
    public @Nullable PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return ModEntities.THIEF_MONKEY.create(world, SpawnReason.BREEDING);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(IS_SHOOTING, false);
        builder.add(HAS_ATTACKED, false);
        builder.add(ATTACK_COOLDOWN, 0);
        builder.add(HELD_ITEM, ItemStack.EMPTY);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
    }

    public boolean hasAttacked() { return this.dataTracker.get(HAS_ATTACKED); }

    public void setHasAttacked(boolean hasAttacked) { this.dataTracker.set(HAS_ATTACKED, hasAttacked); }

    public int getAttackCooldown() { return this.dataTracker.get(ATTACK_COOLDOWN); }

    public void setAttackCooldown(int attackCooldown) { this.dataTracker.set(ATTACK_COOLDOWN, attackCooldown); }

    public ItemStack getHeldItem() { return this.dataTracker.get(HELD_ITEM); }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 10.0D)
                .add(EntityAttributes.ATTACK_DAMAGE, 1.0F)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.4F)
                .add(EntityAttributes.FOLLOW_RANGE, 35.0F)
                .add(EntityAttributes.TEMPT_RANGE, 10.0D);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new ThiefMonkeyMeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 0.8F));
        this.goalSelector.add(2, new AnimalMateGoal(this, 0.4f));
        this.goalSelector.add(3, new TemptGoal(this, 0.8f, Ingredient.ofItems(ModItems.BANANA), false));
        this.goalSelector.add(4, new FollowParentGoal(this, 0.6f));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.4f, 1));
        this.goalSelector.add(6, new LookAroundGoal(this));
        this.goalSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("controller", 5, this::predicate));
        controllers.add(new AnimationController<>("attackController", 0, this::attackPredicate));
    }



    private PlayState attackPredicate(AnimationTest<GeoAnimatable> event) {
        if (this.handSwinging) {
            event.controller().forceAnimationReset();
            event.controller().setAnimation(
                    RawAnimation.begin().then("animation.thief_monkey.attack", Animation.LoopType.PLAY_ONCE)
            );
            this.handSwinging = false;
            return PlayState.CONTINUE;
        }

        return PlayState.CONTINUE;
    }

    private PlayState predicate(AnimationTest<GeoAnimatable> animationState) {
        var controller = animationState.controller();

        if (animationState.isMoving() && !this.handSwinging) {
            controller.setAnimation(RawAnimation.begin().then("animation.thief_monkey.walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        controller.setAnimation(RawAnimation.begin().then("animation.thief_monkey.idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public void lookAt(EntityAnchorArgumentType.EntityAnchor anchorPoint, Vec3d target) {
        super.lookAt(anchorPoint, target);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.isOf(ModItems.BANANA);
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        SoundEvent[] hurtSounds = {
                ModSounds.MONKEY_AMBIENT1,
                ModSounds.MONKEY_AMBIENT2,
                ModSounds.MONKEY_AMBIENT3,
                ModSounds.MONKEY_AMBIENT4
        };
        return hurtSounds[this.random.nextInt(hurtSounds.length)];
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return this.random.nextBoolean() ? ModSounds.MONKEY_HURT : ModSounds.MONKEY_HURT2;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return this.random.nextBoolean() ? ModSounds.MONKEY_DEATH1 : ModSounds.MONKEY_DEATH2;
    }

    @Override
    public ItemStack getMainHandStack() {
        return this.getHeldItem();
    }

    @Override
    public ItemStack getOffHandStack() {
        return ItemStack.EMPTY;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);

        // Save inventory
        ItemStack stack = inventory.getStack(0);
        if (!stack.isEmpty()) {
            NbtCompound itemNbt = (NbtCompound) stack.toNbt(this.getRegistryManager());
            nbt.put("Item", itemNbt);
        }

        // Save HAS_ATTACKED state
        nbt.putBoolean("HasAttacked", this.hasAttacked());

        // Save HELD_ITEM state
        ItemStack heldItem = this.getHeldItem();
        if (!heldItem.isEmpty()) {
            NbtCompound heldItemNbt = (NbtCompound) heldItem.toNbt(this.getRegistryManager());
            nbt.put("HeldItem", heldItemNbt);
        }

        // Save shouldRun state
        nbt.putBoolean("ShouldRun", this.shouldRun);

    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        // Load inventory
        if (nbt.contains("Item")) {
            ItemStack stack = ItemStack.fromNbt(this.getRegistryManager(), nbt.getCompound("Item").get()).orElse(ItemStack.EMPTY);
            inventory.setStack(0, stack);
        } else {
            inventory.clear();
        }

        // Load HAS_ATTACKED state
        if (nbt.contains("HasAttacked")) {
            this.setHasAttacked(nbt.getBoolean("HasAttacked").get());
        }

        // Load HELD_ITEM state
        if (nbt.contains("HeldItem")) {
            ItemStack heldItem = ItemStack.fromNbt(this.getRegistryManager(), nbt.getCompound("HeldItem").get()).orElse(ItemStack.EMPTY);
            this.dataTracker.set(HELD_ITEM, heldItem);
        } else {
            this.dataTracker.set(HELD_ITEM, ItemStack.EMPTY);
        }

        // Load shouldRun state
        if (nbt.contains("ShouldRun")) {
            this.shouldRun = nbt.getBoolean("ShouldRun").get();
        }

    }

}