package com.buuz135.industrial.tile.agriculture;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.tile.CustomColoredItemHandler;
import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import com.buuz135.industrial.utils.ItemStackUtils;
import com.buuz135.industrial.utils.WorkUtils;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.inventory.FluidTankType;

import java.util.List;

public class AnimalResourceHarvesterTile extends WorkingAreaElectricMachine {

    private ItemStackHandler outItems;
    private IFluidTank tank;

    public AnimalResourceHarvesterTile() {
        super(AnimalResourceHarvesterTile.class.getName().hashCode(), 2, 2, false);
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        tank = this.addSimpleFluidTank(8000, "tank", EnumDyeColor.WHITE, 50, 25, FluidTankType.OUTPUT, fluidStack -> false, fluidStack -> true);
        outItems = new ItemStackHandler(3 * 4) {
            @Override
            protected void onContentsChanged(int slot) {
                AnimalResourceHarvesterTile.this.markDirty();
            }
        };
        this.addInventory(new CustomColoredItemHandler(outItems, EnumDyeColor.ORANGE, "Output Items", 18 * 5 + 3, 25, 4, 3) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return false;
            }

            @Override
            public boolean canExtractItem(int slot) {
                return true;
            }
        });
        this.addInventoryToStorage(outItems, "outItems");
    }

    @Override
    public float work() {
        if (WorkUtils.isDisabled(this.getBlockType())) return 0;
        List<EntitySheep> animals = this.world.getEntitiesWithinAABB(EntitySheep.class, getWorkingArea());
        for (EntitySheep sheep : animals) {
            if (!sheep.getSheared()) {
                List<ItemStack> stacks = sheep.onSheared(new ItemStack(Items.SHEARS), this.world, null, 0);
                for (ItemStack stack : stacks) {
                    ItemHandlerHelper.insertItem(outItems, stack, false);
                }
                return 1;
            }
        }
        for (EntityCow cow : this.world.getEntitiesWithinAABB(EntityCow.class, getWorkingArea())) {
            FakePlayer player = IndustrialForegoing.getFakePlayer(this.world);
            player.setPosition(cow.posX, cow.posY, cow.posZ);
            player.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Items.BUCKET));
            if (cow.processInteract(player, EnumHand.MAIN_HAND)) {
                ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
                if (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
                    IFluidHandlerItem fluidHandlerItem = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
                    tank.fill(fluidHandlerItem.drain(Integer.MAX_VALUE, true), true);
                }
            }
        }

        //milkTank.fill(new FluidStack(FluidsRegistry.MILK, cows.size() * 1000), true);
        return 1;
    }

    @Override
    public AxisAlignedBB getWorkingArea() {
        EnumFacing f = this.getFacing().getOpposite();
        BlockPos corner1 = new BlockPos(0, 0, 0).offset(f, getRadius() + 1);
        return new AxisAlignedBB(this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.pos.getX() + 1, this.pos.getY() + 1, this.pos.getZ() + 1).offset(corner1).grow(getRadius(), 0, getRadius()).setMaxY(this.getPos().getY() + getHeight());
    }

    @Override
    protected boolean acceptsFluidItem(ItemStack stack) {
        return ItemStackUtils.acceptsFluidItem(stack);
    }

    @Override
    protected void processFluidItems(ItemStackHandler fluidItems) {
        ItemStackUtils.processFluidItems(fluidItems, tank);
    }
}
