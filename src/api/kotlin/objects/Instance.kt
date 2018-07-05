package therealfarfetchd.quacklib.api.objects

interface Instance<out T : Instantiable> {

  val type: T

}