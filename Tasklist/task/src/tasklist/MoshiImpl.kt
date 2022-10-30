package tasklist

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.File

class MoshiImpl: Memento {

    override fun load() {

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
    }

    override fun save() {

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val jsonFile = File("tasklist.json")

        val taskListAdapter = moshi.adapter(List::class.java)
        if (TaskList.taskList.isNotEmpty())
            jsonFile.writeText(taskListAdapter.toJson(TaskList.taskList))

    }

}