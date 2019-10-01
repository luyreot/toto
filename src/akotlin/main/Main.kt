package akotlin.main

import akotlin.service.Patterns

class Main {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
//            updateYearDrawings()

//            DataService.loadAllDrawings()
//            DataService.loadDrawingsForYears("2017", "2018", "2019")

            Patterns.loadDrawingsForYears("2019")
            Patterns.generatePatterns()


            println()
        }

    }

}