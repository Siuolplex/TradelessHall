package io.siuolplex.tradelesshall.mixin;

import io.siuolplex.tradelesshall.HelpMeBeEthical;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Villager.class)
public abstract class VillagerMixin extends AbstractVillager implements HelpMeBeEthical {
    @Unique
    public long lastWoken;
    @Unique
    public AABB minimumMovementZone = new AABB(this.brain.getMemory(MemoryModuleType.HOME).orElseGet(() -> GlobalPos.of(this.level().dimension(), this.blockPosition())).pos()).inflate(5.0D, 5.0D, 5.0D);
    @Unique
    public boolean hasLeftMMZ = false;

    public VillagerMixin(EntityType<? extends AbstractVillager> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "allowedToRestock", at = @At("RETURN"), cancellable = true)
    private void vehicleProtection(CallbackInfoReturnable<Boolean> cir) {
        if (!tradelessHall$vehicleCheck()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "allowedToRestock", at = @At("RETURN"), cancellable = true)
    private void sleepProtection(CallbackInfoReturnable<Boolean> cir) {
        if (!tradelessHall$sleepCheck()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "allowedToRestock", at = @At("RETURN"), cancellable = true)
    private void boxProtection(CallbackInfoReturnable<Boolean> cir) {
        if (!tradelessHall$boxCheck()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void mmzVerify(CallbackInfo ci) {
        if (!hasLeftMMZ && !minimumMovementZone.contains(position())) {
           hasLeftMMZ = true;
        }
    }

    @Inject(method = "stopSleeping", at = @At("TAIL"))
    private void boxSetup(CallbackInfo ci) {
        lastWoken = this.level().getGameTime();
        hasLeftMMZ = false;
        this.minimumMovementZone = new AABB(this.blockPosition()).inflate(5.0D, 5.0D, 5.0D);
    }

    public boolean tradelessHall$vehicleCheck() {
        return (this.getVehicle() == null);
    }

    public boolean tradelessHall$sleepCheck() {
        return (this.level().getGameTime() - this.brain.getMemory(MemoryModuleType.LAST_WOKEN).orElseGet(() -> {
            this.brain.setMemory(MemoryModuleType.LAST_WOKEN, this.level().getGameTime());
            lastWoken = this.level().getGameTime();
            return this.level().getGameTime();
        }) <= 18000L);
    }

    public boolean tradelessHall$boxCheck() {
        return (this.hasLeftMMZ);
    }
}
