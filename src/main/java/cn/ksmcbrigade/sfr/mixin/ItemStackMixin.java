package cn.ksmcbrigade.sfr.mixin;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.shuyanmc.mpem.config.CoolConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Shadow private int count;

    @Mutable
    @Shadow @Final public static Codec<ItemStack> CODEC;

    @Inject(method = "<clinit>",at = @At("TAIL"))
    private static void clinit(CallbackInfo ci){
        CODEC = RecordCodecBuilder.create((p_258963_) -> {
            return p_258963_.group(BuiltInRegistries.ITEM.byNameCodec().fieldOf("id").forGetter(ItemStack::getItem), Codec.INT.fieldOf("Count").forGetter(ItemStack::getCount), CompoundTag.CODEC.optionalFieldOf("tag").forGetter((p_281115_) -> {
                return Optional.ofNullable(p_281115_.getTag());
            }),CODEC.INT.optionalFieldOf("countMod").forGetter((i)->{
                return Optional.of(i.getCount());
            })).apply(p_258963_, (item,count,tag,modCount)->{
                int countD = count;
                if(modCount.isPresent()) countD = modCount.get();;
                ItemStack stack = new ItemStack(item,countD);
                tag.ifPresent(stack::setTag);
                return stack;
            });

        });
    }

    @Inject(method = "getMaxStackSize",at = @At("RETURN"),cancellable = true)
    public void getMax(CallbackInfoReturnable<Integer> cir){
        if(CoolConfig.ENABLED.get()){
            cir.setReturnValue(CoolConfig.MAX_STACK_SIZE.get());
        }
    }

    @Redirect(method = "save",at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;putByte(Ljava/lang/String;B)V"))
    public void save(CompoundTag instance, String p_128345_, byte p_128346_){
        instance.putByte(p_128345_,p_128346_);
        instance.putInt("countMod",this.count);
    }

    @Inject(method = "<init>(Lnet/minecraft/nbt/CompoundTag;)V",at = @At("TAIL"))
    public void init(CompoundTag p_41608_, CallbackInfo ci){
        if (p_41608_.contains("countMod")) {
            this.count = p_41608_.getInt("countMod");
        }
    }
}
