package cn.ksmcbrigade.sfr.mixin.fixes;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnchantmentMenu.class)
public class EnchantmentTableMenuMixin {
    @Redirect(method = "lambda$clickMenuButton$1",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Container;setItem(ILnet/minecraft/world/item/ItemStack;)V"))
    public void set(Container instance, int i, ItemStack itemStack){
        if(itemStack.equals(ItemStack.EMPTY))instance.setItem(i,instance.getItem(i).copyWithCount(instance.getItem(i).getCount()-1));
    }
}
