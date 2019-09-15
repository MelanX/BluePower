package com.bluepowermod.block.power;

import com.bluepowermod.block.BlockContainerFacingBase;
import com.bluepowermod.reference.Refs;
import com.bluepowermod.tile.tier3.TileBlulectricFurnace;
import net.minecraft.block.material.Material;

/**
 * @author MoreThanHidden
 */
public class BlockBlulectricFurnace  extends BlockContainerFacingBase {

    public BlockBlulectricFurnace() {
        super(Material.ROCK, TileBlulectricFurnace.class);
        setRegistryName(Refs.MODID, Refs.BLULECTRICFURNACE_NAME);
    }

}
