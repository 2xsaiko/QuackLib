package therealfarfetchd.quacklib.common.api.util.math

import java.util.Random

/**
 * Created by marco on 10.07.17.
 */
object Random : Random() {
  fun nextShort(): Short = nextInt(65535).toShort()
  fun nextByte(): Byte = nextInt(255).toByte()
}