package cn.ksmcbrigade.sfr.mixin.fixes;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin(GrindstoneMenu.class)
public abstract class GrindstoneMenuMixin extends AbstractContainerMenu {
    @Shadow @Final
    Container repairSlots;

    @Mutable
    @Shadow @Final private ContainerLevelAccess access;

    @Shadow private int xp;

    protected GrindstoneMenuMixin(@Nullable MenuType<?> p_38851_, int p_38852_) {
        super(p_38851_, p_38852_);
    }

    @Redirect(method = "createResult",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Container;setItem(ILnet/minecraft/world/item/ItemStack;)V"))
    public void set(Container instance, int i, ItemStack itemStack){
        if(itemStack.equals(ItemStack.EMPTY))instance.setItem(i,instance.getItem(i).copyWithCount(instance.getItem(i).getCount()-1));
    }

    @Override
    protected @NotNull Slot addSlot(@NotNull Slot p_38898_) {
        if(p_38898_.x==129 && p_38898_.y==34){
            return super.addSlot(new Slot(p_38898_.container,p_38898_.getSlotIndex(),p_38898_.x,p_38898_.y) {
                public boolean mayPlace(ItemStack p_39630_) {
                    return false;
                }

                public void onTake(Player p_150574_, ItemStack p_150575_) {
                    if (net.minecraftforge.common.ForgeHooks.onGrindstoneTake(repairSlots, access, this::getExperienceAmount)) return;
                    access.execute((p_39634_, p_39635_) -> {
                        if (p_39634_ instanceof ServerLevel) {
                            ExperienceOrb.award((ServerLevel)p_39634_, Vec3.atCenterOf(p_39635_), this.getExperienceAmount(p_39634_));
                        }

                        p_39634_.levelEvent(1042, p_39635_, 0);
                    });
                    repairSlots.setItem(0, get(repairSlots,0));
                    repairSlots.setItem(1, get(repairSlots,1));
                }

                private int getExperienceAmount(Level p_39632_) {
                    if (xp > -1) return xp;
                    int l = 0;
                    l += this.getExperienceFromItem(repairSlots.getItem(0));
                    l += this.getExperienceFromItem(repairSlots.getItem(1));
                    if (l > 0) {
                        int i1 = (int)Math.ceil((double)l / 2.0D);
                        return i1 + p_39632_.random.nextInt(i1);
                    } else {
                        return 0;
                    }
                }

                private int getExperienceFromItem(ItemStack p_39637_) {
                    int l = 0;
                    Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(p_39637_);

                    for(Map.Entry<Enchantment, Integer> entry : map.entrySet()) {
                        Enchantment enchantment = entry.getKey();
                        Integer integer = entry.getValue();
                        if (!enchantment.isCurse()) {
                            l += enchantment.getMinCost(integer);
                        }
                    }

                    return l;
                }

                private ItemStack get(Container instance, int i){
                    return instance.getItem(i).copyWithCount(instance.getItem(i).getCount()-1);
                }
            });
        }
        return super.addSlot(p_38898_);
    }
}
