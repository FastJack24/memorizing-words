import java.io.File;
import java.nio.file.FileSystems;

public class Utils {

    public static String constructDataPath() {
        String fileSeparator = FileSystems.getDefault().getSeparator();
        StringBuilder pathToDataBuilder = new StringBuilder(
                System.getProperty("java.class.path").split(File.pathSeparator)[0]
        );
        if (!System.getProperty("java.class.path").endsWith(fileSeparator)) {
            pathToDataBuilder.append(fileSeparator);
        }
        return pathToDataBuilder.append("data").toString();
    }
}
