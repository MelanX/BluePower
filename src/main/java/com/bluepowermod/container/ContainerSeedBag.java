/*
 * This file is part of Blue Power.
 *
 *     Blue Power is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Blue Power is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Blue Power.  If not, see <http://www.gnu.org/licenses/>
 *     
 *     @author Lumien
 */

package com.bluepowermod.container;

import com.bluepowermod.client.gui.BPContainerType;
import com.bluepowermod.item.ItemCanvasBag;
import com.bluepowermod.tile.tier1.TileBuffer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

import com.bluepowermod.container.inventory.InventoryItem;
import com.bluepowermod.container.slot.SlotLocked;
import com.bluepowermod.container.slot.SlotSeedBag;
import com.bluepowermod.item.ItemSeedBag;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraftforge.items.ItemStackHandler;

public class ContainerSeedBag extends Container {

    private final ItemStackHandler seedBagInvHandler;
    private Hand activeHand;

    public ContainerSeedBag(int windowId, PlayerInventory playerInventory) {
        super(BPContainerType.SEEDBAG, windowId);
        seedBagInvHandler = new ItemStackHandler(9);
        
        //Get Active hand
        activeHand = Hand.MAIN_HAND;
        ItemStack seedBag = playerInventory.player.getHeldItem(activeHand);
        if(!(seedBag.getItem() instanceof ItemSeedBag)){
            seedBag = playerInventory.player.getHeldItemOffhand();
            activeHand = Hand.OFF_HAND;
        }

        //Get Items from the NBT Handler
        if (seedBag.hasTag()) seedBagInvHandler.deserializeNBT(seedBag.getTag().getCompound("inv"));
        
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                this.addSlot(new SlotSeedBag(seedBagInvHandler, j + i * 3, 62 + j * 18, 17 + i * 18));
            }
        }
        
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        
        for (int i = 0; i < 9; ++i) {
            if (playerInventory.currentItem == i) {
                this.addSlot(new SlotLocked(playerInventory, i, 8 + i * 18, 142));
            } else {
                this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
            }
        }

    }
    
    @Override
    public boolean canInteractWith(PlayerEntity player) {
    
        return !player.getHeldItemMainhand().isEmpty() && player.getHeldItemMainhand().getItem() instanceof ItemSeedBag;
    }

    @Override
    public void onContainerClosed(PlayerEntity playerIn) {
        //Update items in the NBT
        ItemStack seedBag = playerIn.getHeldItem(activeHand);
        if (!seedBag.hasTag())
            seedBag.setTag(new CompoundNBT());
        if (seedBag.getTag() != null) {
            seedBag.getTag().put("inv", seedBagInvHandler.serializeNBT());
        }
        super.onContainerClosed(playerIn);
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity par1EntityPlayer, int par2) {

        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot) this.inventorySlots.get(par2);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (par2 < 9) {
                if (!this.mergeItemStack(itemstack1, 9, 45, true)) { return ItemStack.EMPTY; }
            } else if (!this.mergeItemStack(itemstack1, 0, 9, false)) { return ItemStack.EMPTY; }

            if (itemstack1.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) { return ItemStack.EMPTY; }

            slot.onSlotChange(itemstack, itemstack1);
        }

        return itemstack;
    }

}
