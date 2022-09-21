package tasklist
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeParseException
import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.File

object Main {

    val taskList: MutableList<MutableList<String>> = mutableListOf()

    fun addTask() {
        val task = mutableListOf<String>()
        var priority: String
        while (true) {
            println("Input the task priority (C, H, N, L):")
            priority = readln().lowercase()
            if (priority in listOf("c", "h", "n", "l")) break
        }

        var date: String
        while (true) {
            println("Input the date (yyyy-mm-dd):")
            date = toNormalDate(readLine()?: "no user input")
            if (checkDate(date)) break
        }

        val dueTag = calcDueTag(date)

        var time: String
        while (true) {
            println("Input the time (hh:mm):")
            time = readLine() ?: "no user input"
            if(time.matches("\\d:\\d".toRegex())) {
                time = "0$time" + 0
            }
            if(time.matches("\\d\\d:\\d".toRegex())) {
                time = time.split(":")[0] + ":0" + time.split(":")[1]
            }
            if(time.matches("\\d:\\d\\d".toRegex())) {
                time = "0$time"
            }
            if (checkTime(time)) break
        }

        println("Input a new task (enter a blank line to end):")
        while (true) {
            val enter = readLine() ?: "no user input"
            if (enter.isBlank()) break
            task.add(enter.trim())
        }
        if(task.isEmpty()) println("The task is blank") else {
            task.add(0, "$date $time ${priority.uppercase()} $dueTag")
            taskList.add(task)
        }
    }

    fun printList() {

        if (taskList.isEmpty()) println("No tasks have been input") else {
            println(
                "+----+------------+-------+---+---+--------------------------------------------+\n" +
                        "| N  |    Date    | Time  | P | D |                   Task                     |"
            )
            for (i in taskList.indices) {
                println("+----+------------+-------+---+---+--------------------------------------------+")
                val list = taskList[i][0].split(" ")
                val (date, time, priority, due) = list
                val number = i + 1

                for (j in taskList[i].indices) {
                    val chunked = taskList[i][j].chunked(44)
                    if (j == 0) continue

                    if (j == 1) {
                        for (k in chunked.indices) {
                            if (k == 0) println(
                                String.format(
                                    "| %s  | %s | %s | %s | %s |%-44s%c",
                                    number,
                                    date,
                                    time,
                                    priorityAndDue(priority),
                                    priorityAndDue(due),
                                    taskList[i][j].chunked(44)[k],
                                    '|'
                                )
                            ) else println(
                                String.format(
                                    "|    |            |       |   |   |%-44s%c",
                                    taskList[i][j].chunked(44)[k],
                                    '|'
                                )
                            )
                        }
                    } else chunked.forEach { s ->
                        println(
                            String.format(
                                "|    |            |       |   |   |%-44s%c",
                                s,
                                '|'
                            )
                        )
                    }
                }
            }
            println("+----+------------+-------+---+---+--------------------------------------------+")
        }
    }

    fun deleteTask() {
        printList()
        if (taskList.isNotEmpty()) {
            while (true) {
                println("Input the task number (1-${taskList.lastIndex + 1}):")
                val number = readln()
                if (!isDigit(number) || number.toInt() !in 1..taskList.lastIndex + 1) println("Invalid task number") else {
                    taskList.removeAt(number.toInt() - 1)
                    println("The task is deleted")
                    break
                }
            }
        }
    }

    fun editTask() {
        printList()
        if (taskList.isNotEmpty()) {
            while (true) {
                println("Input the task number (1-${taskList.lastIndex + 1}):")
                val number = readln()
                if (!isDigit(number)) println("Invalid task number") else
                    if (number.toInt() !in 1..taskList.lastIndex + 1) println("Invalid task number") else {

                        while (true) {
                            println("Input a field to edit (priority, date, time, task):")
                            when (readln()) {
                                "priority" -> {
                                    println("Input the task priority (C, H, N, L):")
                                    val priority = readln()
                                    taskList[number.toInt() - 1][0] =
                                        taskList[number.toInt() - 1][0].replace("[CHNL]".toRegex(), priority)
                                    println("The task is changed")
                                    break
                                }
                                "date" -> {
                                    println("Input the date (yyyy-mm-dd):")
                                    val date = toNormalDate(readln())
                                    taskList[number.toInt() - 1][0] =
                                        taskList[number.toInt() - 1][0].replaceBefore(" ", date)
                                    println("The task is changed")
                                    break
                                }
                                "time" -> {
                                    println("Input the time (hh:mm):")
                                    val time = readln()
                                    taskList[number.toInt() - 1][0] =
                                        taskList[number.toInt() - 1][0].replace("\\d+:\\d+".toRegex(), time)
                                    println("The task is changed")
                                    break
                                }
                                "task" -> {
                                    val task = mutableListOf<String>()
                                    println("Input a new task (enter a blank line to end):")
                                    while (true) {
                                        val enter = readLine() ?: "no user input"
                                        if (enter.isBlank()) break
                                        task.add(enter.trim())
                                    }
                                    val info = taskList[number.toInt() - 1][0]
                                    taskList[number.toInt() - 1].clear()
                                    taskList[number.toInt() - 1].add(info)
                                    taskList[number.toInt() - 1].addAll(task)
                                    println("The task is changed")
                                    break
                                }
                                else -> println("Invalid field")
                            }
                        }
                        break
                    }
            }
        }
    }

}

fun main() {

    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val jsonFile = File("tasklist.json")

    val type = Types.newParameterizedType(MutableList::class.java, MutableList::class.java)

    val taskListOfListsAdapter = moshi.adapter<MutableList<MutableList<String>>>(type)


    if (jsonFile.exists()) {

        val text = jsonFile.readText()

        val loadedList = taskListOfListsAdapter.fromJson(text)

        if (loadedList != null) {
            Main.taskList.addAll(loadedList)
        }
    }

    while (true) {
        println("Input an action (add, print, edit, delete, end):")
        when (readLine()?.lowercase()) {
            "add" -> Main.addTask()
            "print" -> Main.printList()
            "delete" -> Main.deleteTask()
            "edit" -> Main.editTask()
            "end" -> {
                println("Tasklist exiting!")
                break
            }
            else -> println("The input action is invalid")
        }
    }

    val taskListAdapter = moshi.adapter(List::class.java)
    if (Main.taskList.isNotEmpty())
    jsonFile.writeText(taskListAdapter.toJson(Main.taskList))

}

fun checkDate(date: String) : Boolean {
   return try {
       LocalDate.parse(date)
       true
    } catch (e: DateTimeParseException) {
        println("The input date is invalid")
       false
    }
}

fun checkTime(time: String) : Boolean {
   return try {
        LocalTime.parse(time)
        true
    } catch (e: DateTimeParseException) {
        println("The input time is invalid")
        false
    }
}

fun calcDueTag(date: String): String {
    val taskDate = LocalDate.parse(date).toKotlinLocalDate()
    val currentDate = Clock.System.now().toLocalDateTime(TimeZone.of("UTC+0")).date
    val numberOfDays = currentDate.daysUntil(taskDate)
    if (numberOfDays > 0) return "I"
    if (numberOfDays < 0) return "O"
    return "T"
}

fun toNormalDate(date: String): String {
    var nDate = date
    if (date.matches("\\d{4}-\\d\\d-\\d".toRegex())) {
        nDate = date.split("-")[0] + "-" + date.split("-")[1] + "-0" + date.split("-")[2]
    }
    if (date.matches("\\d{4}-\\d-\\d\\d".toRegex())) {
        nDate = date.split("-")[0] + "-0" + date.split("-")[1] + "-" + date.split("-")[2]
    }
    if (date.matches("\\d{4}-\\d-\\d".toRegex())) {
        nDate = date.split("-")[0] + "-0" + date.split("-")[1] + "-0" + date.split("-")[2]
    }
    return nDate
}

fun isDigit(number: String): Boolean {
    return try {
        number.toInt()
        true
    }catch (e: NumberFormatException){
        false
    }
}

fun priorityAndDue(prio: String): String {
   return when(prio) {
        "C" -> "\u001B[101m \u001B[0m"
        "H" -> "\u001B[103m \u001B[0m"
        "N" -> "\u001B[102m \u001B[0m"
        "L" -> "\u001B[104m \u001B[0m"
        "I" -> "\u001B[102m \u001B[0m"
        "T" -> "\u001B[103m \u001B[0m"
        "O" -> "\u001B[101m \u001B[0m"
         else -> prio
    }
}
