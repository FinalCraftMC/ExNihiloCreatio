package exnihilocreatio.barrel.modes.block;

import exnihilocreatio.barrel.IBarrelMode;
import exnihilocreatio.networking.MessageBarrelModeUpdate;
import exnihilocreatio.networking.PacketHandler;
import exnihilocreatio.texturing.Color;
import exnihilocreatio.tiles.TileBarrel;
import exnihilocreatio.util.ItemInfo;
import exnihilocreatio.util.Util;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;

import java.util.List;

public class BarrelModeBlock implements IBarrelMode {

    private final BarrelItemHandlerBlock handler = new BarrelItemHandlerBlock(null);
    @Getter
    @Setter
    private ItemInfo block;

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        if (block != null) {
            tag.setString("block", block.toString());
        }
        if (!handler.getStackInSlot(0).isEmpty()) {
            handler.getStackInSlot(0).writeToNBT(tag);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        if (tag.hasKey("block")) {
            block = new ItemInfo(tag.getString("block"));
        }

        handler.setStackInSlot(0, new ItemStack(tag));
    }

    @Override
    public boolean isTriggerItemStack(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isTriggerFluidStack(FluidStack stack) {
        return false;
    }

    @Override
    public String getName() {
        return "block";
    }

    @Override
    public List<String> getWailaTooltip(TileBarrel barrel, List<String> currenttip) {
        if (!handler.getStackInSlot(0).isEmpty())
            currenttip.add(handler.getStackInSlot(0).getDisplayName());
        return currenttip;
    }

    @Override
    public void onBlockActivated(World world, TileBarrel barrel, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!handler.getStackInSlot(0).isEmpty()) {
            Util.dropItemInWorld(barrel, player, handler.getStackInSlot(0), 0.02);
            handler.setBarrel(barrel);
            handler.setStackInSlot(0, ItemStack.EMPTY);
            barrel.setMode("null");
            PacketHandler.sendToAllAround(new MessageBarrelModeUpdate("null", barrel.getPos()), barrel);
            barrel.markDirty();
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    @SideOnly(Side.CLIENT)
    public TextureAtlasSprite getTextureForRender(TileBarrel barrel) {
        handler.setBarrel(barrel);
        ItemStack stack = handler.getStackInSlot(0);
        if (stack.isEmpty())
            return Util.getTextureFromBlockState(Blocks.AIR.getDefaultState());
        return Util.getTextureFromBlockState(Block.getBlockFromItem(stack.getItem()).getStateFromMeta(stack.getItemDamage()));
    }

    @Override
    public Color getColorForRender(TileBarrel barrel) {
        return Util.whiteColor;
    }

    @Override
    public float getFilledLevelForRender(TileBarrel barrel) {
        return 0.9375F;
    }

    @Override
    public void update(TileBarrel barrel) {
    }

    @Override
    public void addItem(ItemStack stack, TileBarrel barrel) {
        handler.setBarrel(barrel);
        if (handler.getStackInSlot(0).isEmpty()) {
            handler.insertItem(0, stack, false);
        }
    }

    @Override
    public ItemStackHandler getHandler(TileBarrel barrel) {
        handler.setBarrel(barrel);
        return handler;
    }

    @Override
    public FluidTank getFluidHandler(TileBarrel barrel) {
        return null;
    }

    @Override
    public boolean canFillWithFluid(TileBarrel barrel) {
        return false;
    }

}
