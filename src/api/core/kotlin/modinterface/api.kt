package therealfarfetchd.quacklib.api.modinterface

import net.minecraftforge.fml.common.Loader

internal fun currentMod() = Loader.instance().activeModContainer()