/*
 * This file is part of Blue Power. Blue Power is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. Blue Power is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along
 * with Blue Power. If not, see <http://www.gnu.org/licenses/>
 */

package com.bluepowermod.tile.tier3;

import com.bluepowermod.api.power.BlutricityStorage;
import com.bluepowermod.api.power.CapabilityBlutricity;
import com.bluepowermod.api.power.IPowerBase;
import com.bluepowermod.helper.EnergyHelper;
import com.bluepowermod.tile.BPTileEntityType;
import com.bluepowermod.tile.TileMachineBase;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author MoreThanHidden
 */

public class TileBlulectricFurnace extends TileMachineBase {
    private final BlutricityStorage storage = new BlutricityStorage(1000, 100);
    private LazyOptional<IPowerBase> blutricityCap;

    public TileBlulectricFurnace() {
        super(BPTileEntityType.BLULECTRIC_FURNACE);
    }

    @Override
    public void tick() {
        if (!world.isRemote) {
            storage.resetCurrent();
            //Balance power of attached blulectric blocks.
            for (Direction facing : Direction.values()) {
                TileEntity tile = world.getTileEntity(pos.offset(facing));
                if (tile != null && tile.getCapability(CapabilityBlutricity.BLUTRICITY_CAPABILITY, facing.getOpposite()).isPresent()) {
                    IPowerBase exStorage = tile.getCapability(CapabilityBlutricity.BLUTRICITY_CAPABILITY, facing.getOpposite()).orElse(null);
                    EnergyHelper.balancePower(exStorage, storage);
                }
            }
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityBlutricity.BLUTRICITY_CAPABILITY) {
            if( blutricityCap == null ) blutricityCap = LazyOptional.of( () -> storage );
            return blutricityCap.cast();
        }
        return super.getCapability(cap);
    }

    @Override
    protected void invalidateCaps(){
        super.invalidateCaps();
        if( blutricityCap != null )
        {
            blutricityCap.invalidate();
            blutricityCap = null;
        }
    }

    @Override
    protected void readFromPacketNBT(CompoundNBT tCompound) {
        super.readFromPacketNBT(tCompound);
        if(tCompound.contains("energy")) {
        INBT nbtstorage = tCompound.get("energy");
        CapabilityBlutricity.BLUTRICITY_CAPABILITY.getStorage().readNBT(CapabilityBlutricity.BLUTRICITY_CAPABILITY, storage, null, nbtstorage);
        }
    }

    @Override
    protected void writeToPacketNBT(CompoundNBT tCompound) {
        super.writeToPacketNBT(tCompound);
            INBT nbtstorage = CapabilityBlutricity.BLUTRICITY_CAPABILITY.getStorage().writeNBT(CapabilityBlutricity.BLUTRICITY_CAPABILITY, storage, null);
            tCompound.put("energy", nbtstorage);
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.pos, 3, this.getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        super.onDataPacket(net, pkt);
        handleUpdateTag(pkt.getNbtCompound());
    }

}
