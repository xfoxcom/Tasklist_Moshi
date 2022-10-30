package tasklist

fun main() {

   val moshiImpl = MoshiImpl()

    moshiImpl.load()

    while (true) {
        println("Input an action (add, print, edit, delete, end):")
        when (readLine()?.lowercase()) {
            "add" -> TaskList.addTask()
            "print" -> TaskList.printList()
            "delete" -> TaskList.deleteTask()
            "edit" -> TaskList.editTask()
            "end" -> {
                println("Tasklist exiting!")
                break
            }
            else -> println("The input action is invalid")
        }
    }

  moshiImpl.save()

}

