package therealfarfetchd.quacklib.common.api.util

class TreeBuilder<T : Any>(start: T) {
  private var stack: List<T> = listOf(start)

  var finished: List<T> = emptyList()
    private set

  private var _currentEntry: T? = null
  var currentEntry: T
    get() = _currentEntry!!
    set(value) {
      _currentEntry = value
    }

  fun pushStack() {
    if (_currentEntry == null) createEntry()
    stack += _currentEntry!!
    _currentEntry = null
  }

  fun popStack() {
    createEntry()
    stack = stack.dropLast(1)
  }

  fun createEntry() {
    finishEntry()
    _currentEntry = stack.last()
  }

  fun finishEntry() {
    _currentEntry?.also {
      finished += it
      _currentEntry = null
    }
  }
}