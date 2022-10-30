package tasklist

import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.File

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
            TaskList.taskList.addAll(loadedList)
        }
    }

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

    val taskListAdapter = moshi.adapter(List::class.java)
    if (TaskList.taskList.isNotEmpty())
        jsonFile.writeText(taskListAdapter.toJson(TaskList.taskList))

}

