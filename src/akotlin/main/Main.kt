package akotlin.main

import akotlin.service.DataService

class Main {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
//            updateYearDrawings()

//            DataService.loadAllDrawings()
//            DataService.loadDrawingsForYears("2017", "2018", "2019")

            DataService.loadDrawingsForYears("2019")
            DataService.generatePatterns()

//            val arr = intArrayOf(1, 4, 15, 33, 44, 45)
//            val cArr = arr.map { number -> number / 10 }.toIntArray()

            println()
        }

    }

}