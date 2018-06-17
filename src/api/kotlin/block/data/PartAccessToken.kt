package therealfarfetchd.quacklib.api.block.data

interface PartAccessToken<out T : BlockDataPart> {

  fun retrieve(data: BlockDataRO): T

}

operator fun <T : BlockDataPart> BlockDataRO.get(access: PartAccessToken<T>): T =
  access.retrieve(this)