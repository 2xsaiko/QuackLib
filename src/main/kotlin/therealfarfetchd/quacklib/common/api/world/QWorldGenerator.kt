package therealfarfetchd.quacklib.common.api.world

import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.chunk.IChunkProvider
import net.minecraft.world.gen.IChunkGenerator
import net.minecraft.world.gen.feature.WorldGenMinable
import net.minecraftforge.fml.common.IWorldGenerator
import therealfarfetchd.quacklib.QuackLib
import therealfarfetchd.quacklib.common.api.autoconf.DefaultFeatures
import therealfarfetchd.quacklib.common.api.autoconf.FeatureManager
import java.util.*


typealias Generator = (world: World, rnd: Random, chunkX: Int, chunkZ: Int) -> Unit

object QWorldGenerator : IWorldGenerator {
  private var generators: List<Generator> = emptyList()

  override fun generate(random: Random, chunkX: Int, chunkZ: Int, world: World, chunkGenerator: IChunkGenerator, chunkProvider: IChunkProvider) {
    generators.forEach { it(world, random, chunkX, chunkZ) }
  }

  fun registerGenerator(gen: Generator) {
    if (!FeatureManager.isRequired(DefaultFeatures.OreGeneration))
      QuackLib.Logger.warn("World generation is not enabled, not registering generator! Please enable it with the feature manager on startup.")
    else generators += gen
  }

  fun registerOreGenerator(ore: IBlockState, height: IntRange, veinSize: Int, veinsPerChunk: Int) {
    val gen = WorldGenMinable(ore, veinSize)
    registerGenerator { world, rnd, chunkX, chunkZ ->
      for (i in 0 until veinsPerChunk) {
        val x = chunkX * 16 + rnd.nextInt(16)
        val z = chunkZ * 16 + rnd.nextInt(16)
        val y = rnd.nextInt(height.count()) + height.first
        val blockPos = BlockPos(x, y, z)
        // if (QuackLib.debug) QuackLib.Logger.info("Generated $ore at $blockPos.")
        gen.generate(world, rnd, blockPos)
      }
    }
  }
}