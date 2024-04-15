package com.fastjack;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class FileHandler {

    public void processFiles() {
        System.out.println("Введите абсолютный путь к папке с файлами формата txt с форматированными словами:");
        Scanner input = new Scanner(System.in);
        String filePath = input.nextLine();
        input.close();
        System.out.println();

        File currentWorkingDirectory = new File(filePath);
        File[] txtFiles = currentWorkingDirectory.listFiles((dir, name) -> name.endsWith(".txt"));

        if (txtFiles == null || txtFiles.length == 0) {
            System.out.println(
                    "Не было найдено ни одного текстового файла в папке " + filePath + ", попробуйте ещё раз"
            );
            return;
        }

        for (File txtFile : txtFiles) {
            createTextBlocks(txtFile);
        }
        System.out.println("Завершена обработка текстовых файлов слов из директории " + filePath);
    }

    private void createTextBlocks(File txtFile) {
        /* Обработка пути к приложению и построение пути до разбитых файлов */
        String fileNameNoExtension = txtFile.getName().split("\\.")[0]; // Имя файла без расширения
        String separator = Utils.getFileSeparator();
        StringBuilder appDataDirectoryBuilder = new StringBuilder(Utils.constructDataPath());

        String dataDirectoryPath = appDataDirectoryBuilder.toString(); // ~/.cache/memorizing-words
        String pathToDirectory= appDataDirectoryBuilder.append(separator).append(fileNameNoExtension)
                .toString(); // ~/.cache/memorizing-words/DoctorStrange
        String pathToTextBlocks = appDataDirectoryBuilder.append(separator).append("%s")
                .toString(); // ~/.cache/memorizing-words/DoctorStrange/%s


        /* Чтение файла */
        LinkedList<ArrayList<String>> rows = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(txtFile), StandardCharsets.UTF_8)
        )) {
            String data = br.readLine();
            rows.add(new ArrayList<>());

            while (data != null) {
                if (!data.equals("-----")) {
                    rows.getLast().add(data.trim());
                    data = br.readLine();
                } else {
                    data = br.readLine();
                    rows.add(new ArrayList<>());
                }
            }
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(
                    "Не удалось найти файл с именем " + txtFile.getName() + " в указанной директории", e
            );
			/* Крайне маловероятная ошибка, так как до этого был проведен листинг файлов и он точно
		       существует за исключением ситуации когда пользователь успел удалить его до вызова метода */
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Произошла ошибка при чтении или закрытии файла с именем " + txtFile.getName() +
                            " в указанной директории, повторите попытку для этого файла", e
            );
        }


        /* Создание директорий и обработка данных. */
        createDirectoryRecursively(dataDirectoryPath); // Нужно убедиться, что папка для хранения предсоздана

        if (rows.size() > 1 || (rows.size() == 1 && !rows.getFirst().isEmpty())) {
            if (new File(pathToDirectory).mkdir()) {
                System.out.println(
                        "Создание директории для " + txtFile.getName() + " в data успешно завершено"
                );
            }
            else {
                System.out.println(
                        "Произошла ошибка создании папки для:" + txtFile + ". Возможно папка уже существует"
                );
            }
        }

        String fileNameWithBlock = "%s%s.txt"; // Строка для форматирования названия для блока
        int fileBlock = 1; // Номер блока
        SequencedMap<String, Integer> pathsINFOtoNumsINFO = new LinkedHashMap<>();
        String lineSeparator = System.lineSeparator();

        while (!rows.isEmpty()) {
            String textBlockPath = String.format(
                    pathToTextBlocks, String.format(fileNameWithBlock, fileNameNoExtension, fileBlock)
            );
            ArrayList<String> rowsInBlock = rows.pop();
            if (!rowsInBlock.isEmpty()) {
                try (FileWriter writer = new FileWriter(textBlockPath, StandardCharsets.UTF_8)) {
                    int nums = 0; // Счетчик слов
                    for (String word : rowsInBlock) {
                        writer.write(word);
                        writer.write(lineSeparator);
                        nums++;
                    }

                    pathsINFOtoNumsINFO.put(textBlockPath, nums);
                    fileBlock++;
                } catch (IOException e) {
                    throw new IllegalStateException(
                            "Произошла ошибка при написании или закрытии файла " +
                                    textBlockPath + ", удалите директорию " + dataDirectoryPath +
                                    " и повторите запись txt файла снова", e
                    );
                }
            }
        }
        System.out.println("Завершена обработка и формирование текстовых файлов блоков для " + txtFile + "\n");


        /* Создания файла для конфигурирования загрузки папки с блоками INFO.txt */
        if (!pathsINFOtoNumsINFO.isEmpty()) {
            try (FileWriter infoWriter = new FileWriter(
                    String.format(pathToTextBlocks, Utils.INFO_FILE_NAME),
                    StandardCharsets.UTF_8
            )) {
                Map.Entry<String, Integer> pathINFO = pathsINFOtoNumsINFO.pollFirstEntry();
                while (pathINFO != null) {
                    infoWriter.write(pathINFO.getKey());
                    infoWriter.write(lineSeparator);
                    infoWriter.write(pathINFO.getValue().toString());
                    infoWriter.write(lineSeparator);
                    pathINFO = pathsINFOtoNumsINFO.pollFirstEntry();
                }
                System.out.println(
                        "Завершено формирование файла " + String.format(pathToTextBlocks, "INFO.txt") + "\n"
                );
            } catch (IOException e) {
                throw new IllegalStateException(
                        "Произошла ошибка при написании или закрытии файла INFO.txt в директории " +
                                pathToDirectory + ", удалите эту директорию и повторите запись txt файла снова"
                );
            }
        }
    }

    private void createDirectoryRecursively(String directoryPath) {
        File directory = new File(directoryPath);
        if (!new File(directory.getParent()).exists()) {
            createDirectoryRecursively(directory.getParent());
        }
        if (!directory.exists()) {
            if (directory.mkdir()) {
                System.out.println(
                        "Директория для хранения ваших слов " + directoryPath + " была успешно создана\n"
                );
            } else {
                throw new IllegalStateException(
                        "Не удалось создать директорию " + directoryPath +
                                ", попробуйте выбрать другую папку для программы"
                );
            }
        }
    }

    public void cleanUp() {
        try (Stream<Path> dataPath = Files.walk(Path.of(Utils.constructDataPath()))) {
            dataPath.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    throw new IllegalStateException("Невозможно удалить файл " + path.getFileName().toString(), e);
                }
            });
        } catch (IOException e) {
            throw new IllegalStateException("Ошибка при попытке очистить директорию с данными", e);
        }
    }
}
