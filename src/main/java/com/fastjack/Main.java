package com.fastjack;

/**
 * <h3>Запуск приложения возможен в трех режимах:</h3>
 * <p>1) Загрузка слов</p>
 * <p>{@code java -jar /path/to/jar/WordsRevise.jar 0}</p>
 * <p>2) Очистка данных</p>
 * <p>{@code java -jar /path/to/jar/WordsRevise.jar 1}</p>
 * <p>3) Обычный запуск приложения</p>
 * <p>{@code java -jar /path/to/jar/WordsRevise.jar }</p>
 */
public class Main {

    public static void main(String[] args) {
        if (args.length != 0 && args[0].equals("0")) { // Загрузка слов
            /* Обработка текстовых файлов, создание папок и разбиение блоков слов. */
            new FileHandler().processFiles();
            /* Выход из приложения. */
            System.exit(0);
        } else if (args.length != 0 && args[0].equals("1")) {
            /* Удаление директории с данными */
            new FileHandler().cleanUp();
            /* Выход из приложения. */
            System.exit(0);
        }

        new WordsHandler().repeatStart();
    }

}
