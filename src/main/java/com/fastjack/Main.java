package com.fastjack;

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
            Scanner input = new Scanner(System.in);
            String pathToTxtFiles = input.nextLine();
            input.close();
            System.out.println();
            /* Обработка текстового файла, создание папки и разбиение блоков слов. */
            FileHandler.processFiles(pathToTxtFiles);
            /* Выход из приложения. */
            System.exit(0);
        } else if (args.length != 0 && args[0].equals("1")) {
            /* Удаление директории с данными */
            FileHandler.cleanUp();
            /* Выход из приложения. */
            System.exit(0);
        }

        new WordsHandler().repeatStart(); // Запуск
    }

}
