package cn.ksmcbrigade.sfr.mixin.fixes;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AnvilMenu.class)
public class AnvilMenuMixin {

    @Shadow @Final private DataSlot cost;

    @Redirect(method = {"onTake"},at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Container;setItem(ILnet/minecraft/world/item/ItemStack;)V"))
    public void set(Container instance, int i, ItemStack itemStack){
        if(itemStack.equals(ItemStack.EMPTY)){
            instance.setItem(i,instance.getItem(i).copyWithCount(instance.getItem(i).getCount()-1));
        }
        else{
            instance.setItem(i,itemStack.copyWithCount(1));
        }
    }

    @Redirect(method = {"createResult"},at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/ResultContainer;setItem(ILnet/minecraft/world/item/ItemStack;)V"))
    public void set(ResultContainer instance, int i, ItemStack itemStack){
        if(itemStack.equals(ItemStack.EMPTY)){
            instance.setItem(i,instance.getItem(i).copyWithCount(instance.getItem(i).getCount()-1));
        }
        else{
            instance.setItem(i,itemStack.copyWithCount(1));
        }
    }

    @ModifyConstant(method = {"createResult"},constant = @Constant(intValue = 40))
    public int modify(int constant){
        this.cost.set(1);
        return Integer.MAX_VALUE;
    }

    @Redirect(method = {"createResult","mayPickup"},at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/DataSlot;get()I"))
    public int modifyCost(DataSlot instance){
        instance.set(1);
        return 1;
    }

    @Redirect(method = "createResult",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;setRepairCost(I)V"))
    public void modifyCost(ItemStack instance, int p_41743_){
        //nothing
    }
}
