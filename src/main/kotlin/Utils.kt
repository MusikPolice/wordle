import java.nio.file.Files
import java.nio.file.Path

class Utils {
    companion object {
        fun loadLinesFromFile(filename: String): List<String> {
            val path = getPathToFile(filename)
            return Files.readAllLines(path)
        }

        fun getPathToFile(filename:String): Path  =
            Path.of(Utils::class.java.classLoader.getResource(filename).toURI())
    }
}