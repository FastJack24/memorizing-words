import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FileHandler {
    private void createTextBlocks(File txtFile) {
        /* Обработка пути к приложению и построение пути до разбитых файлов*/
        String fileName = txtFile.getName().split("\\.")[0]; // Имя файла без расширения
        StringBuilder userDirectoryBuilder = new StringBuilder(WordsHandler.constructDataPath());

        String dataDirectoryPath = userDirectoryBuilder.toString(); // /home/.../appFolder/data
        String pathToDirectory= userDirectoryBuilder.append(System.getProperty("file.separator")).append(fileName)
                .toString(); // /home/.../appFolder/data/DoctorStrange
        String pathToTextBlocks = userDirectoryBuilder.append(System.getProperty("file.separator")).append("%s")
                .toString(); // /home/.../appFolder/data/DoctorStrange/%s

        /* Чтение файла */
        LinkedList<ArrayList<String>> rows = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(txtFile), StandardCharsets.UTF_8))) {
            String data = br.readLine().trim();
            int blockCounter = 0;
            rows.add(new ArrayList<>());

            while (data != null) {
                if (!data.equals("-----")) {
                    rows.get(blockCounter).add(data.trim());
                    data = br.readLine();
                } else {
                    data = br.readLine();
                    ++blockCounter;
                    rows.add(new ArrayList<>());
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Не удалось найти файл с именем " +
                    fileName + " в указанной директории.");
            e.printStackTrace();
            return;
			/* Крайне маловероятная ошибка, так как до этого был проведен листинг файлов и он точно
		       существует за исключением ситуации когда пользователь успел удалить его до вызова метода. */
        } catch (IOException e) {
            System.out.println("Произошла ошибка при чтении или закрытии файла с именем " +
                    fileName + " в указанной директории. Повторите попытку для этого файла.");
            e.printStackTrace();
            return;
        }


        /* Создание директорий и обработка данных. */
        File dataDirectory = new File(dataDirectoryPath);
        if (!dataDirectory.exists()) {
            if (dataDirectory.mkdir()) {
                System.out.println("Директория для хранения ваших слов " + dataDirectoryPath + " была успешно создана.\n");
            } else {
                System.out.println("Не удалось создать директорию " + dataDirectoryPath +
                        ", попробуйте выбрать другую папку для программы.");
                return;
            }
        }

        if (new File(pathToDirectory).mkdir()) {
            System.out.println("Создание директории для " + txtFile.getName() + " в data успешно завершено.");
        }
        else {
            System.out.println("Произошла ошибка создании папки для:" + txtFile + ". Возможно папка уже существует.");
        }
        // Вообще не уверен, что нужен этот код создания папки.

        String fileNameWithBlock = "%s%s.txt"; // Строка для форматирования названия для блока
        int fileBlock = 1; // Номер блока
        LinkedList<Integer> numsINFO = new LinkedList<>();
        LinkedList<String> pathsINFO = new LinkedList<>();


        while (!rows.isEmpty()) {
            try (FileWriter writer = new FileWriter(
                    String.format(pathToTextBlocks, String.format(fileNameWithBlock, fileName, fileBlock)),
                    StandardCharsets.UTF_8)
            ) {
                int nums = 0; // Счетчик слов
                ArrayList<String> row = rows.pop();
                for (String word : row) {
                    writer.write(word);
                    writer.write("\n");
                    nums++;
                }

                pathsINFO.add(String.format(pathToTextBlocks, String.format(fileNameWithBlock, fileName, fileBlock)));
                numsINFO.add(nums);
                fileBlock++;
            } catch (IOException e) {
                System.out.println("Произошла ошибка при написании или закрытии файла с именем " +
                        String.format(fileNameWithBlock, fileName, fileBlock) + " в директории " + pathToDirectory +
                        ". " + "Удалите эту директорию и повторите запись txt файла снова.");
                e.printStackTrace();
                return;
            }
        }
        System.out.println("Завершено создание файлов текстовых блоков.");

        // Создания файла для конфигурирования загрузки папки с блоками INFO.txt
        try (FileWriter infoWriter = new FileWriter(
                String.format(pathToTextBlocks, "INFO.txt"),
                StandardCharsets.UTF_8)
        ) {
            for (int i = 0; i < pathsINFO.size(); i++) {
                infoWriter.write(pathsINFO.get(i));
                infoWriter.write('\n');
                infoWriter.write(numsINFO.get(i).toString());
                infoWriter.write('\n');
            }
            infoWriter.close();
            System.out.println("Завершена запись файла " + String.format(pathToTextBlocks, "INFO.txt") + ".\n");
        } catch (IOException e) {
            System.out.println("Произошла ошибка при написании или закрытии файла INFO.txt в директории " +
                    pathToDirectory + ". Удалите эту директорию и повторите запись txt файла снова.");
            e.printStackTrace();
        }
    }

    public void processFiles(String filePath) {
        File currentWorkingDirectory = new File(filePath);
        File[] txtFiles = currentWorkingDirectory.listFiles((dir, name) -> name.endsWith(".txt"));

        if (txtFiles == null || txtFiles.length == 0) {
            System.out.println("Не было найдено ни одного текстового файла в папке " +
                    filePath + ", попробуйте ещё раз.");
            return;
        }
        
        for (int i = 0; i < txtFiles.length; i++) {
            createTextBlocks(txtFiles[i]);
        }
    }
}
