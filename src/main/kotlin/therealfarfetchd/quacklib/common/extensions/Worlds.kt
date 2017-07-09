package therealfarfetchd.quacklib.common.extensions

import net.minecraft.world.World

/**
 * Created by marco on 09.07.17.
 */

val World.isClient
  get() = this.isRemote

val World.isServer
  get() = !this.isRemote