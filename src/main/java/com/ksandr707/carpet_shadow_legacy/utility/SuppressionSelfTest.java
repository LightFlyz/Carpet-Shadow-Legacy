package com.ksandr707.carpet_shadow_legacy.utility;

import com.ksandr707.carpet_shadow_legacy.CarpetShadowLegacy;
import com.ksandr707.carpet_shadow_legacy.CarpetShadowLegacySettings;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;

/** CI-only diagnostic: toggles a powered-rail line and reports neighbor-update recursion depth. */
public final class SuppressionSelfTest {
    public static void run(MinecraftServer server) {
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
