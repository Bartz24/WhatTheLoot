package com.bartz24.whatttheloot;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("whattheloot")
public class WhatTheLoot {
    private static final Logger LOGGER = LogManager.getLogger();
    public static IItemTier SuperSilk = new IItemTier() {
        public int getMaxUses() {
            return 0;
        }

        public float getEfficiency() {
            return 0.5f;
        }

        public float getAttackDamage() {
            return 2.0f;
        }

        public int getHarvestLevel() {
            return 99;
        }

        public int getEnchantability() {
            return 8;
        }

        public Ingredient getRepairMaterial() {
            return Ingredient.EMPTY;
        }
    };
    public static ItemGroup main = new ItemGroup("whattheloot.main") {
        public ItemStack createIcon() {
            return new ItemStack(WhatTheLoot.superSilkTool);
        }
    };
    ;
    public static Item superSilkTool;
    LootTableRandomizer randomizer;

    public WhatTheLoot() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        if (this.randomizer == null)
            this.randomizer = new LootTableRandomizer();
        event.getServer().getResourceManager().addReloadListener((IResourceManagerReloadListener) manager ->
                this.randomizer.randomize(event.getServer().getWorld(DimensionType.OVERWORLD), event.getServer().getLootTableManager())
        );
        event.getServer().reload();
    }

    @SubscribeEvent
    public void onLivingDrops(LivingDropsEvent evt) {
        if (this.randomizer != null && evt.getSource().getTrueSource() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) evt.getSource().getTrueSource();
            ResourceLocation resourcelocation = evt.getEntityLiving().func_213346_cF();
            if (player.getHeldItemMainhand().getItem() instanceof SuperSilkTool && resourcelocation != LootTables.EMPTY) {
                LootTable loottable = this.randomizer.getOriginalLootTable(resourcelocation);
                LootContext.Builder builder = this.buildLoot(evt.getEntityLiving(), evt.getSource());
                evt.getDrops().clear();
                loottable.generate(builder.build(LootParameterSets.ENTITY), item ->
                {
                    evt.getDrops().add(new ItemEntity(evt.getEntity().world, evt.getEntity().posX, evt.getEntity().posY, evt.getEntity().posZ, item));
                });
            }
        }
    }

    @SubscribeEvent
    public void onBreakBlock(BlockEvent.BreakEvent evt) {
        if (this.randomizer != null) {
            final ResourceLocation resourcelocation = evt.getState().getBlock().getLootTable();
            if (evt.getPlayer().getHeldItemMainhand().getItem() instanceof SuperSilkTool && resourcelocation != LootTables.EMPTY) {
                LootTable loottable = this.randomizer.getOriginalLootTable(resourcelocation);
                LootContext.Builder builder = new LootContext.Builder((ServerWorld) evt.getWorld()).withRandom(((ServerWorld) evt.getWorld()).rand).withParameter(LootParameters.POSITION, evt.getPos()).withParameter(LootParameters.TOOL, evt.getPlayer().getHeldItemMainhand()).withParameter(LootParameters.THIS_ENTITY, evt.getPlayer()).withNullableParameter(LootParameters.BLOCK_ENTITY, evt.getWorld().getTileEntity(evt.getPos()));
                LootContext lootcontext = builder.withParameter(LootParameters.BLOCK_STATE, evt.getState()).build(LootParameterSets.BLOCK);
                loottable.generate(lootcontext).forEach(item -> Block.spawnAsEntity((World) evt.getWorld(), evt.getPos(), item));
                final BlockState state = evt.getWorld().getBlockState(evt.getPos());
                final boolean removed = state.removedByPlayer((World) evt.getWorld(), evt.getPos(), evt.getPlayer(), true, evt.getWorld().getFluidState(evt.getPos()));
                if (removed) {
                    state.getBlock().onPlayerDestroy(evt.getWorld(), evt.getPos(), state);
                }
                evt.getPlayer().getHeldItemMainhand().attemptDamageItem(1, ((ServerWorld) evt.getWorld()).rand, (ServerPlayerEntity) evt.getPlayer());
                evt.setCanceled(true);
            }
        }
    }

    private LootContext.Builder buildLoot(LivingEntity entity, DamageSource source) {
        final LootContext.Builder lootcontext$builder = new LootContext.Builder((ServerWorld) entity.world.getWorld()).withRandom(entity.world.rand).withParameter(LootParameters.THIS_ENTITY, entity).withParameter(LootParameters.POSITION, new BlockPos(entity)).withParameter(LootParameters.DAMAGE_SOURCE, source).withNullableParameter(LootParameters.KILLER_ENTITY, source.getTrueSource()).withNullableParameter(LootParameters.DIRECT_KILLER_ENTITY, source.getImmediateSource());
        return lootcontext$builder;
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onItemsRegistry(RegistryEvent.Register<Item> event) {
            event.getRegistry().register((WhatTheLoot.superSilkTool = new SuperSilkTool()));
        }
    }
}
