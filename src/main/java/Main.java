import java.util.Scanner;

/**
 * <h3>Запуск приложения возможен в двух режимах:</h3>
 * <p>1) Загрузка слов </p>
 * <p>{@code java -jar WordsRevise.jar 0}</p>
 * <p>2) Обычный запуск приложения</p>
 * <p>{@code java -jar WordsRevise.jar }</p>
 */
public class Main {

    public static void main(String[] args) {
        if (args.length != 0 && args[0].equals("0")) { // Загрузка слов
            System.out.println("Введите абсолютный путь к папке с файлами формата txt с форматированными словами:");
            Scanner checkPathToFiles = new Scanner(System.in);
            String pathToTxtS = checkPathToFiles.nextLine();
            System.out.println();
            checkPathToFiles.close();
            /* Обработка текстового файла, создание папки и разбиение блоков слов. */
            new FileHandler().processFiles(pathToTxtS);
            /* Выход из приложения. */
            System.exit(0);
        }

        new WordsHandler().repeatStart(); // Запуск
    }

}
