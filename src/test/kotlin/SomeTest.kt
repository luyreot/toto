import logicOld.data.Drawings
import org.junit.Test
import logicOld.util.Const.PATH_TXT
import logicOld.util.IO
import kotlin.test.assertEquals

class SomeTest {

    @Test
    fun `successful load of files contents into memory`() {
        val files = IO.getFiles(PATH_TXT)
        var totalLinesInFiles = 0
        files!!.forEach { file ->
            totalLinesInFiles += IO.getTxtFileContents(file).count()
        }

        Drawings.loadDrawings()
        val memoryDrawingsCount = Drawings.drawings.count()

        assertEquals(totalLinesInFiles, memoryDrawingsCount, "Something went wrong when loading drawings into memory!")
    }
}