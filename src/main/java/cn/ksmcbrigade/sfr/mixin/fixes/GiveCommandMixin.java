package cn.ksmcbrigade.sfr.mixin.fixes;

import net.minecraft.server.commands.GiveCommand;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GiveCommand.class)
public class GiveCommandMixin {
    @Redirect(method = "giveItem",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;getMaxStackSize()I"))
    private static int get(Item instance){
        return instance.getDefaultInstance().getMaxStackSize();
    }
}
