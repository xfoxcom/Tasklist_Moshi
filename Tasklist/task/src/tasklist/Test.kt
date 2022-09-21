package tasklist


class Test(val number: Number, val line: String) {
    operator fun component1(): Number = number
    operator fun component2(): String = line
}

//  fun main() {
//    Main.taskList.add(mutableListOf("2000-01-01 14:23 H O", "Please, mind the spaces! Each Task line contains 44 characters. If the text is bigger, write the first 44 characters.", "CHIPS", "COLA"))
//    Main.taskList.add(mutableListOf("2020-11-11 10:25 L I", "BUY FISH", "COFFEE"))
//
//
//    for (i in Main.taskList.indices) {
//
//        val list = Main.taskList[i][0].split(" ")
//        val (date, time, priority, due) = list
//        val number = i + 1
//
//        for (j in Main.taskList[i].indices) {
//            val chunked = Main.taskList[i][j].chunked(43)
//            if (j == 0) continue
//
//            if (j == 1) {
//                for (k in chunked.indices) {
//                  if (k == 0) println(
//                        String.format(
//                            "| %s  | %s | %s | %s | %s |%-44s%c",
//                            number,
//                            date,
//                            time,
//                            priorityAndDue(priority),
//                            priorityAndDue(due),
//                            Main.taskList[i][j].chunked(43)[k],
//                            '|'
//                        )
//                    ) else println(
//                      String.format(
//                          "|    |            |       |   |   |%-44s%c",
//                          Main.taskList[i][j].chunked(43)[k],
//                          '|'
//                      ))
//                }
//            } else chunked.forEach { s ->  println(String.format("|    |            |       |   |   |%-44s%c", s, '|')) }
//        }
//    }
//}