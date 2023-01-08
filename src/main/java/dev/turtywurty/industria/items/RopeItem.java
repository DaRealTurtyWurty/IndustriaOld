package dev.turtywurty.industria.items;

import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.entity.RopeEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class RopeItem extends Item {
    private static final Component ROPE_POSITION_SET = Component.translatable("message.industria.rope_position_set");

    public RopeItem() {
        super(new Item.Properties().tab(Industria.TAB));
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();

        BlockPos pos = pContext.getClickedPos().immutable();
        BlockState state = level.getBlockState(pos);
        if (!state.is(BlockTags.FENCES)) return InteractionResult.FAIL;

        ItemStack handItem = pContext.getItemInHand();
        CompoundTag nbt = handItem.getOrCreateTag();

        if (!nbt.contains(Industria.MODID)) {
            var industria = new CompoundTag();
            industria.put("RopePosition", NbtUtils.writeBlockPos(pos));
            nbt.put(Industria.MODID, industria);
            return InteractionResult.SUCCESS;
        }

        CompoundTag industria = nbt.getCompound(Industria.MODID);
        if (!industria.contains("RopePosition")) {
            industria.put("RopePosition", NbtUtils.writeBlockPos(pos));
            pContext.getPlayer().displayClientMessage(ROPE_POSITION_SET, true);
            return InteractionResult.SUCCESS;
        }

        BlockPos startPos = NbtUtils.readBlockPos(industria.getCompound("RopePosition")).immutable();
        if (startPos.equals(pos)) return InteractionResult.FAIL;

        if (!level.getBlockState(startPos).is(BlockTags.FENCES)) {
            industria.put("RopePosition", NbtUtils.writeBlockPos(pos));
            return InteractionResult.SUCCESS;
        }

        var rope = new RopeEntity(level, startPos);
        level.addFreshEntity(rope);
        rope.addEnd(pos);
        rope.setShouldRender(pos);

        RopeEntity.getRopeEntity(level, pos).ifPresentOrElse(ropeEntity -> ropeEntity.addEnd(startPos), () -> {
            var rope2 = new RopeEntity(level, pos);
            level.addFreshEntity(rope2);
            rope2.addEnd(startPos);
        });

        rope.playPlacementSound();

        if (!pContext.getPlayer().isCreative()) {
            handItem.shrink(1);
        }

        return InteractionResult.SUCCESS;
    }
}
