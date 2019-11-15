package com.bartz24.whatttheloot;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class SuperSilkTool extends ToolItem {
    public SuperSilkTool() {
        super(1.0f, -3.0f, WhatTheLoot.SuperSilk, Collections.EMPTY_SET, new Item.Properties().group(WhatTheLoot.main).addToolType(ToolType.PICKAXE, WhatTheLoot.SuperSilk.getHarvestLevel()));
        this.setRegistryName("whattheloot", "supersilktool");
    }

    public float getDestroySpeed(final ItemStack stack, final BlockState state) {
        return this.efficiency;
    }

    public boolean canHarvestBlock(final BlockState blockIn) {
        Block block = blockIn.getBlock();
        int i = this.getTier().getHarvestLevel();
        return i >= blockIn.getHarvestLevel();
    }

    public boolean hitEntity(final ItemStack stack, final LivingEntity target, final LivingEntity attacker) {
        return true;
    }

    public boolean onBlockDestroyed(final ItemStack stack, final World worldIn, final BlockState state, final BlockPos pos, final LivingEntity entityLiving) {
        return true;
    }

    public boolean isEnchantable(final ItemStack stack) {
        return true;
    }

    public void addInformation(final ItemStack p_77624_1_, @Nullable World p_77624_2_, List<ITextComponent> list, ITooltipFlag p_77624_4_) {
        super.addInformation(p_77624_1_, p_77624_2_, (List) list, p_77624_4_);
        list.add(new TranslationTextComponent("item.whattheloot.supersilktool.desc"));
    }
}
