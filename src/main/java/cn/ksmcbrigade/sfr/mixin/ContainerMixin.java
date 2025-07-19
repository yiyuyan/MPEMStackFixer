package cn.ksmcbrigade.sfr.mixin;

import net.minecraft.world.Container;
import net.shuyanmc.mpem.config.CoolConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Container.class)
public interface ContainerMixin {
    /**
     * @author KSmc_brigade
     * @reason re to support larger max stack size
     */
    @Overwrite
    default int getMaxStackSize() {
        if(CoolConfig.ENABLED.get()) return CoolConfig.MAX_STACK_SIZE.get();
        return 64;
    }
}
