import java.io.File;
import java.nio.file.FileSystems;

public class Utils {

    public static final String INFO_FILE_NAME = "INFO.txt";

    public static String getFileSeparator() {
        return FileSystems.getDefault().getSeparator();
    }

    public static String constructDataPath() {
        String fileSeparator = getFileSeparator();
        StringBuilder pathToDataBuilder = new StringBuilder(
                System.getProperty("java.class.path").split(File.pathSeparator)[0]
        );
        if (!System.getProperty("java.class.path").endsWith(fileSeparator)) {
            pathToDataBuilder.append(fileSeparator);
        }
        return pathToDataBuilder.append("data").toString();
    }
}
