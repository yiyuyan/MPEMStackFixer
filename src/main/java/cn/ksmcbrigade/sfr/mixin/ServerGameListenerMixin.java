package cn.ksmcbrigade.sfr.mixin;

import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.shuyanmc.mpem.config.CoolConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGameListenerMixin {
    @ModifyConstant(method = "handleSetCreativeModeSlot",constant = @Constant(intValue = 64))
    public int action(int constant) {
        if(CoolConfig.ENABLED.get()) return CoolConfig.MAX_STACK_SIZE.get();
        return 64;
    }
}
