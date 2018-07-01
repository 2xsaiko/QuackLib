package therealfarfetchd.quacklib.render.vanilla

import therealfarfetchd.math.Mat4
import therealfarfetchd.math.Vec3
import therealfarfetchd.math.times

interface Transformation {

  fun transformPoint(type: TransformType, v: Vec3): Vec3

}

object IdentityTransformation : Transformation {

  override fun transformPoint(type: TransformType, v: Vec3): Vec3 = v

}

data class MatrixTransformation(val mat: Mat4) : Transformation {

  override fun transformPoint(type: TransformType, v: Vec3): Vec3 = mat * v

}

data class MultiTransformation(val components: Map<TransformType, Transformation>) : Transformation {

  override fun transformPoint(type: TransformType, v: Vec3): Vec3 {
    return components[type]?.transformPoint(type, v) ?: v
  }

}

enum class TransformType(val jname: String) {

  NONE("none"),
  THIRD_PERSON_LEFT_HAND("thirdperson_lefthand"),
  THIRD_PERSON_RIGHT_HAND("thirdperson_righthand"),
  FIRST_PERSON_LEFT_HAND("firstperson_lefthand"),
  FIRST_PERSON_RIGHT_HAND("firstperson_righthand"),
  HEAD("head"),
  GUI("gui"),
  GROUND("ground"),
  FIXED("fixed");

  companion object {
    val Values = values().toList()

    val byJname = Values.associateBy(TransformType::jname)
  }

}