package therealfarfetchd.quacklib.tools

@Suppress("NOTHING_TO_INLINE")
inline fun getCallStack(): List<StackTraceElement> {
  return Throwable().stackTrace.toList()
}