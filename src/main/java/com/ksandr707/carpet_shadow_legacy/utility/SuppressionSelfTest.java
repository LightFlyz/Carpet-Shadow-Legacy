package com.ksandr707.carpet_shadow_legacy.utility;

import com.ksandr707.carpet_shadow_legacy.CarpetShadowLegacy;
import com.ksandr707.carpet_shadow_legacy.CarpetShadowLegacySettings;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;

/** CI-only diagnostic: toggles a powered-rail line and reports neighbor-update recursion depth. */
public final class SuppressionSelfTest {
    public static void shadowTest(MinecraftServer server) {
        ServerLevel level = server.overworld();
        boolean prev = CarpetShadowLegacySettings.shadowSuppressionGeneration;
        CarpetShadowLegacySettings.shadowSuppressionGeneration = true;
        try {
            net.fabricmc.fabric.api.entity.FakePlayer player = net.fabricmc.fabric.api.entity.FakePlayer.get(level);
            net.minecraft.world.SimpleContainer box = new net.minecraft.world.SimpleContainer(9) {
                boolean armed = true;
                @Override public void setChanged() {
                    if (armed && !getItem(0).isEmpty()) { armed = false; throw new StackOverflowError("simulated suppressor"); }
                    super.setChanged();
                }
            };
            net.minecraft.world.inventory.ChestMenu menu = new net.minecraft.world.inventory.ChestMenu(net.minecraft.world.inventory.MenuType.GENERIC_9x1, 1, player.getInventory(), box, 1);
            net.minecraft.world.item.ItemStack diamonds = new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.DIAMOND, 5);
            player.getInventory().setItem(0, diamonds);
            try {
                menu.clicked(0, 0, net.minecraft.world.inventory.ContainerInput.SWAP, player);
            } catch (Throwable t) {
                CarpetShadowLegacy.LOGGER.info("CSL_SHADOWTEST click threw {}", t.toString());
            }
            net.minecraft.world.item.ItemStack inBox = box.getItem(0), inHotbar = player.getInventory().getItem(0);
            CarpetShadowLegacy.LOGGER.info("CSL_SHADOWTEST box={} hotbar={} sameStack={} shadowId={}", inBox, inHotbar, inBox == inHotbar,
                    ((com.ksandr707.carpet_shadow_legacy.interfaces.ShadowItem) (Object) inBox).getShadowId());
            player.getInventory().setItem(0, net.minecraft.world.item.ItemStack.EMPTY);
        } catch (Throwable t) {
            CarpetShadowLegacy.LOGGER.info("CSL_SHADOWTEST setup failed", t);
        } finally {
            CarpetShadowLegacySettings.shadowSuppressionGeneration = prev;
        }
    }

    public static void realChestTest(MinecraftServer server) {
        ServerLevel level = server.overworld();
        boolean prevGen = CarpetShadowLegacySettings.shadowSuppressionGeneration, prevClassic = CarpetShadowLegacySettings.classicUpdateSuppression;
        int prevDepth = CarpetShadowLegacySettings.classicUpdateSuppressionDepth;
        try {
            BlockPos chestPos = new BlockPos(20, 200, 20);
            BlockPos compPos = chestPos.west();
            level.setBlock(chestPos.below(), Blocks.STONE.defaultBlockState(), 2);
            level.setBlock(chestPos, Blocks.CHEST.defaultBlockState(), 2);
            level.setBlock(compPos.below(), Blocks.AIR.defaultBlockState(), 2);
            level.setBlock(compPos, Blocks.COMPARATOR.defaultBlockState().setValue(net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING, net.minecraft.core.Direction.EAST), 2);
            CarpetShadowLegacy.LOGGER.info("CSL_CHESTTEST comparator placed={} canSurvive={}", level.getBlockState(compPos).is(Blocks.COMPARATOR), level.getBlockState(compPos).canSurvive(level, compPos));
            net.minecraft.world.level.block.entity.ChestBlockEntity chest = (net.minecraft.world.level.block.entity.ChestBlockEntity) level.getBlockEntity(chestPos);
            net.fabricmc.fabric.api.entity.FakePlayer player = net.fabricmc.fabric.api.entity.FakePlayer.get(level);
            net.minecraft.world.inventory.ChestMenu menu = new net.minecraft.world.inventory.ChestMenu(net.minecraft.world.inventory.MenuType.GENERIC_9x3, 2, player.getInventory(), chest, 3);
            net.minecraft.world.item.ItemStack stick = new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.STICK, 3);
            player.getInventory().setItem(1, stick);
            CarpetShadowLegacySettings.shadowSuppressionGeneration = true;
            CarpetShadowLegacySettings.classicUpdateSuppression = true;
            CarpetShadowLegacySettings.classicUpdateSuppressionDepth = 1;
            try {
                menu.clicked(0, 1, net.minecraft.world.inventory.ContainerInput.SWAP, player);
                CarpetShadowLegacy.LOGGER.info("CSL_CHESTTEST click returned normally");
            } catch (Throwable t) {
                CarpetShadowLegacy.LOGGER.info("CSL_CHESTTEST click threw {}", t.toString());
            }
            net.minecraft.world.item.ItemStack inChest = chest.getItem(0), inHotbar = player.getInventory().getItem(1);
            CarpetShadowLegacy.LOGGER.info("CSL_CHESTTEST comparatorStillThere={} chest={} hotbar={} sameStack={}", level.getBlockState(compPos).is(Blocks.COMPARATOR), inChest, inHotbar, inChest == inHotbar);
        } catch (Throwable t) {
            CarpetShadowLegacy.LOGGER.info("CSL_CHESTTEST setup failed", t);
        } finally {
            CarpetShadowLegacySettings.shadowSuppressionGeneration = prevGen;
            CarpetShadowLegacySettings.classicUpdateSuppression = prevClassic;
            CarpetShadowLegacySettings.classicUpdateSuppressionDepth = prevDepth;
        }
    }

    public static void run(MinecraftServer server) {
        shadowTest(server);
        realChestTest(server);
        ServerLevel level = server.overworld();
        for (boolean classic : new boolean[]{false, true}) {
            CarpetShadowLegacySettings.classicUpdateSuppression = classic;
            for (int len : new int[]{8, 2000}) {
                BlockPos base = new BlockPos(0, 150, classic ? 0 : 10).offset(0, 0, len == 8 ? 0 : 4);
                try {
                    for (int x = 0; x < len; x++) {
                        level.setBlock(base.offset(x, -1, 0), x % 8 == 0 ? Blocks.REDSTONE_BLOCK.defaultBlockState() : Blocks.STONE.defaultBlockState(), 2);
                        level.setBlock(base.offset(x, 0, 0), Blocks.POWERED_RAIL.defaultBlockState(), 2);
                    }
                    for (int x = 0; x < len; x++) level.updateNeighborsAt(base.offset(x, 0, 0), Blocks.POWERED_RAIL);
                    UpdateDepth.DEPTH.get()[1] = 0;
                    // pull every power source at once by replacing them in one cascade
                    for (int x = 0; x < len; x += 8) level.setBlock(base.offset(x, -1, 0), Blocks.STONE.defaultBlockState(), 3);
                    CarpetShadowLegacy.LOGGER.info("CSL_SELFTEST classic={} len={} maxDepth={} ok", classic, len, UpdateDepth.DEPTH.get()[1]);
                } catch (Throwable t) {
                    Throwable c = t; boolean so = false;
                    for (int i = 0; c != null && i < 64; c = c.getCause(), i++) if (c instanceof StackOverflowError) so = true;
                    CarpetShadowLegacy.LOGGER.info("CSL_SELFTEST classic={} len={} maxDepth={} threw={} stackOverflow={}", classic, len, UpdateDepth.DEPTH.get()[1], t.getClass().getSimpleName(), so);
                } finally {
                    UpdateDepth.DEPTH.get()[0] = 0;
                }
            }
        }
        CarpetShadowLegacySettings.classicUpdateSuppression = true;
        CarpetShadowLegacySettings.classicUpdateSuppressionDepth = 100;
        BlockPos base = new BlockPos(0, 150, 40);
        try {
            for (int x = 0; x < 400; x++) {
                level.setBlock(base.offset(x, -1, 0), x % 8 == 0 ? Blocks.REDSTONE_BLOCK.defaultBlockState() : Blocks.STONE.defaultBlockState(), 2);
                level.setBlock(base.offset(x, 0, 0), Blocks.POWERED_RAIL.defaultBlockState(), 2);
            }
            for (int x = 0; x < 400; x++) level.updateNeighborsAt(base.offset(x, 0, 0), Blocks.POWERED_RAIL);
            for (int x = 0; x < 400; x += 8) level.setBlock(base.offset(x, -1, 0), Blocks.STONE.defaultBlockState(), 3);
            CarpetShadowLegacy.LOGGER.info("CSL_SELFTEST limit=100 len=400 no throw");
        } catch (Throwable t) {
            CarpetShadowLegacy.LOGGER.info("CSL_SELFTEST limit=100 len=400 threw={} depthNow={}", t.getClass().getSimpleName(), UpdateDepth.DEPTH.get()[0]);
        }
        CarpetShadowLegacySettings.classicUpdateSuppressionDepth = 0;
        CarpetShadowLegacySettings.classicUpdateSuppression = false;
    }
}
