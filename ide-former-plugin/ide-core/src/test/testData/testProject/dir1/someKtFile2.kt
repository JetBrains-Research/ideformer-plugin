class SomeClass(private val num: Int = 0) {
    fun decreaseNum(): Int = return ++num

    fun printSomePhrase() = println("some phrase")
}