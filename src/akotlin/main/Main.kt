package akotlin.main

import akotlin.service.DataService
import akotlin.utils.updateYearDrawings

class Main {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
//            updateYearDrawings()

            DataService.loadAllDrawings()
//            DataService.loadDrawingsForYears("2017", "2018", "2019")
            
            DataService.calculatePatterns()

            println()
        }

    }

}