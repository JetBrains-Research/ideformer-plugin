class SimpleClass(private val num: Int = 0) {
    fun decreaseNum(): Int = return ++num

    fun printSomePhrase(phrase: String = "some phrase") = println(phrase)
}

class ComplexClass(
    val prettyPhrase: String = "This is a pretty phrase",
    private val simpleClass: SimpleClass = SimpleClass(num = 2)
) {
    fun delegatePrinting() = simpleClass.printSomePhrase(prettyPhrase)
}