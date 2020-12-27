import impl.data.Drawing
import impl.util.Const.PATH_TXT
import impl.util.IO
import org.junit.Test
import kotlin.test.assertEquals

class SomeTest {

    @Test
    fun `successful load of files contents into memory`() {
        val files = IO.getFiles(PATH_TXT)
        var totalLinesInFiles = 0
        files!!.forEach { file ->
            totalLinesInFiles += IO.getTxtFileContents(file).count()
        }

        Drawing.loadDrawings()
        val memoryDrawingsCount = Drawing.drawings.count()

        assertEquals(totalLinesInFiles, memoryDrawingsCount, "Something went wrong when loading drawings into memory!")
    }

}