package com.fastjack;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class WordsHandler {

    private Scanner userInput;
    private boolean globalRepeatState;
    private boolean groupRepeatState;
    private boolean blockRepeatState;
    private List<String> blockPaths;
    private SequencedMap<String, String> wordCountByPaths;
    private int stateToRepeat;
    private int chosenBlockNum;

    public void repeatStart() {
        try (Scanner input = new Scanner(System.in)) {
            globalRepeatState = true;
            userInput = input;
            chooseYourGroup();
        } finally {
            userInput.close();
        }
    }

    private void chooseYourGroup() {
        while (globalRepeatState) {
            /* Сбор данных о группах там, где запущенно приложение */
            String pathToData = Utils.constructDataPath();
            File appDataDirectory = new File(pathToData);
            File[] allDir = appDataDirectory.listFiles((dir, name) -> new File(dir, name).isDirectory());
            if (allDir == null || allDir.length == 0) {
                throw new IllegalStateException(
                        "Не было найдено ни одной группы, ее можно создать, запустив программу в режиме загрузки"
                );
            }

            /* Выбор раздела повторения и его загрузка */
            System.out.println("===================\n");
            System.out.println("Группы для повторения:");
            for (int i = 0; i < allDir.length; i++) {
                System.out.println(String.format("%d. ", i + 1) + allDir[i].getName());
            }
            System.out.println();

            System.out.println("Введите номер группы:");
            int groupNumber = Integer.parseInt(userInput.nextLine());
            while (groupNumber > allDir.length || groupNumber < 0) {
                System.out.println("Не существует группы с номером " + groupNumber + ", введите группу снова:");
                groupNumber = Integer.parseInt(userInput.nextLine());
            }
            String section = allDir[groupNumber - 1].getAbsolutePath() +
                    Utils.getFileSeparator() +
                    Utils.INFO_FILE_NAME;
            System.out.println();

            blockPaths = new ArrayList<>();
            wordCountByPaths = new LinkedHashMap<>();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(section), StandardCharsets.UTF_8))
            ) {
                String data = br.readLine();
                while (data != null) {
                    blockPaths.add(data);
                    wordCountByPaths.put(data, br.readLine());
                    data = br.readLine();
                }
            } catch (FileNotFoundException e) {
                throw new IllegalStateException(
                        "Файл INFO.txt не был найден в разделе, попробуйте загрузить группу снова, " +
                        "запустив программу в режиме загрузки", e
                );
            } catch (IOException e) {
                throw new IllegalStateException("Произошла ошибка при чтении или закрытии файла с именем" + section, e);
            }

            groupRepeatState = true;
            chooseYourBlock();
        }
    }

    private void chooseYourBlock() {
        while (groupRepeatState) {
            /* Показ блоков в разделе */
            System.out.println("===================\n");
            String blockNumber = "Блок номер %d%n";
            String wordCounter = "Количество слов: %s%n";
            SequencedCollection<String> wordNumber = wordCountByPaths.sequencedValues();
            int i = 1;
            for (String count : wordNumber) {
                System.out.printf(blockNumber, i);
                System.out.printf(wordCounter, count);
                System.out.println();
                i++;
            }

            /* Выбор блока */
            System.out.println("Введите номер блока (буква r на конце - обратный режим повторения):");
            String userBlock = userInput.nextLine();
            System.out.println();
            System.out.println("===================\n");

            if (userBlock.endsWith("r")) { // Вводя номер блока с буквой r, выбирается обратный режим повторения
                chosenBlockNum = Integer.parseInt(userBlock.replaceAll("r", "")) - 1;
                stateToRepeat = 1;
            } else {
                chosenBlockNum = Integer.parseInt(userBlock) - 1;
                stateToRepeat = 0;
            }

            blockRepeatState = true;
            repeat();
        }
    }

    private void repeat() {
        while (blockRepeatState) {
            LinkedList<String> key = new LinkedList<>();
            HashMap<String, String> keyTranslation = new HashMap<>(); // Дефолтно перевод английский-русский

            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(blockPaths.get(chosenBlockNum)), StandardCharsets.UTF_8)
            )) {
                String data = br.readLine();
                String[] row;
                if (stateToRepeat == 0) { // Без модификатора r (английский - русский)
                    while (data != null) {
                        row = data.split(" - ");
                        keyTranslation.put(row[0], row[1]);
                        key.add(row[0]);
                        data = br.readLine();
                    }
                } else { // C модификатором r
                    while (data != null) {
                        row = data.split(" - ");
                        keyTranslation.put(row[1], row[0]);
                        key.add(row[1]);
                        data = br.readLine();
                    }
                }
            } catch (FileNotFoundException e) {
                throw new IllegalStateException(
                        "Файл с блоком больше не существует, попробуйте загрузить раздел заново", e
                );
            } catch (IOException e) {
                throw new IllegalStateException(
                        "Произошла ошибка при чтении или закрытии файла с именем" + blockPaths.get(chosenBlockNum), e
                );
            }

            Collections.shuffle(key);
            while (!key.isEmpty()) {
                System.out.println("----------");
                String word = key.pop();
                System.out.println(word);
                userInput.nextLine();
                System.out.println(keyTranslation.get(word));
            }
            System.out.println("----------");
            System.out.println();

            /* Выбор дальнейшего действия: */
            System.out.println("Введите что нужно делать дальше (b(к группам), c(к блокам), r(повтор блока), \n" +
                    "rr(повтор блока с другого языка), n(следующий блок), p(previous block)):");
            String whatNext = userInput.nextLine();
            System.out.println();
            switch (whatNext) {
                case "b" -> downStateFromGroup();
                case "c" -> downStateFromBlock();
                case "r" -> {}
                case "rr" -> {
                    if (stateToRepeat == 0) {
                        stateToRepeat = 1;
                    } else {
                        stateToRepeat = 0;
                    }
                }
                case "n" -> next();
                case "p" -> previous();
                default -> downStateFromGlobal();
            }
        }
    }

    private void next() {
        chosenBlockNum++;
        if (chosenBlockNum == blockPaths.size()) { chosenBlockNum = 0; }
    }

    private void previous() {
        chosenBlockNum--;
        if (chosenBlockNum < 0) { chosenBlockNum = blockPaths.size() - 1; }
    }

    private void downStateFromGlobal() {
        globalRepeatState = false;
        groupRepeatState = false;
        blockRepeatState = false;
    }

    private void downStateFromGroup() {
        groupRepeatState = false;
        blockRepeatState = false;
    }

    private void downStateFromBlock() {
        blockRepeatState = false;
    }
}
