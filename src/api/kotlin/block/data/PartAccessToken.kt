package therealfarfetchd.quacklib.api.block.data

interface PartAccessToken<out T : BlockDataPart> {

  fun retrieve(data: BlockData): T

}

operator fun <T : BlockDataPart> BlockData.get(access: PartAccessToken<T>): T =
  access.retrieve(this)