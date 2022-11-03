import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class WordsHandler {
    private final Scanner userInput;
    private LinkedList<String> blockPaths;
    private int stateToRepeat;
    private String pathToTheBlock;
    private int chosenBlockNum;

    public WordsHandler() {
        this.userInput = new Scanner(System.in);
    }

    public void repeatStart() {
        /* Сбор данных о группах там, где запущенно приложение */
        String pathToData = constructDataPath();
        File appDirectory = new File(pathToData);
        File[] allDir = appDirectory.listFiles((dir, name) -> new File(dir, name).isDirectory());
        if (allDir == null || allDir.length == 0) {
            crashReport("Не было найдено ни одной группы, ее можно создать, " +
                    "запустив программу в режиме загрузки.", null);
            return;
        }

        /* Выбор раздела повторения и его загрузка. */
        System.out.println("===================\n");
        System.out.println("Группы для повторения:");
        for (int i = 0; i < allDir.length; i++) {
            System.out.println(String.format("%d. ", i+1) + allDir[i].getName());
        }
        System.out.println();

        System.out.println("Введите номер группы:");
        String section = allDir[Integer.parseInt(userInput.nextLine()) - 1].getAbsolutePath() +
                System.getProperty("file.separator") + "INFO.txt";
        System.out.println();

        blockPaths = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(section), StandardCharsets.UTF_8))) {
            String data = br.readLine();
            while (data != null) {
                blockPaths.add(data);
                data = br.readLine();
            }
        } catch (FileNotFoundException e) {
            crashReport("Файл INFO.txt не был найден в разделе, попробуйте загрузить группу снова, " +
                    "запустив программу в режиме загрузки.", e);
            return;
        } catch (IOException e) {
            crashReport("Произошла ошибка при чтении или закрытии файла с именем" +
                    section + ".", e);
            return;
        }
        chooseYourBlock();
    }

    private void chooseYourBlock() {
        /* Показ блоков в разделе. */
        System.out.println("===================\n");
        String blockNumber = "Блок номер %d%n";
        String wordCounter = "Количество слов: %s%n";
        for (int i = 0; i < blockPaths.size(); i++) {
            if (i % 2 == 0) {
                System.out.printf(blockNumber, i / 2 + 1);
            } else {
                System.out.printf(wordCounter, blockPaths.get(i));
                System.out.println();
            }
        }

        /* Выбор блока */
        System.out.println("Введите номер блока:");
        String userBlock = userInput.nextLine();
        System.out.println();
        System.out.println("===================\n");

        if (userBlock.endsWith("r")) { // Вводя номер блока с буквой r, выбирается обратный режим повторения.
            chosenBlockNum = Integer.parseInt(userBlock.replaceAll("r", ""));
            stateToRepeat = 1;
        } else {
            chosenBlockNum = Integer.parseInt(userBlock);
            stateToRepeat = 0;
        }
        pathToTheBlock = blockPaths.get((chosenBlockNum - 1) * 2);
        repeat();
    }

    private void repeat() {
        LinkedList<String> key = new LinkedList<>();
        HashMap<String, String> keyTranslation = new HashMap<>(); // Дефолтно перевод английский-русский

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(pathToTheBlock), StandardCharsets.UTF_8))) {
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
            crashReport("Файл с блоком больше не существует, попробуйте загрузить раздел заново.", e);
            return;
        } catch (IOException e) {
            crashReport("Произошла ошибка при чтении или закрытии файла с именем" + pathToTheBlock + ".", e);
            return;
        }

        Collections.shuffle(key);
        while (key.size() != 0) {
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
                "rr(повтор блока в обратном направлении),n(следующий блок), p(previous block)):");
        String whatNext = userInput.nextLine();
        System.out.println();
        switch (whatNext) {
            case "b":
                repeatStart();
                break;
            case "c":
                chooseYourBlock();
                break;
            case "r":
                repeat();
                break;
            case "rr":
                if (stateToRepeat == 0) {
                    stateToRepeat = 1;
                    repeat();
                } else {
                    stateToRepeat = 0;
                    repeat();
                }
                break;
            case "n":
                previousNext(0);
                break;
            case "p":
                previousNext(1);
                break;
            case "e":
            default:
                userInput.close();
        }
    }

    private void previousNext(int state) {
        if (state == 0) { // Следующий блок.
            chosenBlockNum++;
            if ((chosenBlockNum - 1) * 2 == blockPaths.size()) { chosenBlockNum = 1; }
        }
        else { // Предыдущий блок.
            chosenBlockNum--;
            if (chosenBlockNum == 0) { chosenBlockNum = blockPaths.size() - 2; }
        }
        pathToTheBlock = blockPaths.get((chosenBlockNum - 1) * 2);
        repeat();
    }

    public void crashReport(String message, Throwable e) {
        System.out.println(message);
        if (e !=null) {
            e.printStackTrace();
        }
        System.out.println("Нажмите Enter чтобы выйти.");
        userInput.nextLine();
        userInput.close();
    }

    public static String constructDataPath() {
        String fileSeparator = System.getProperty("file.separator");
        StringBuilder pathToDataBuilder = new StringBuilder(
                System.getProperty("java.class.path").
                        split(System.getProperty("path.separator"))[0]
        );
        if (!System.getProperty("java.class.path").endsWith(fileSeparator)) {
            pathToDataBuilder.append(fileSeparator);
        }
        return pathToDataBuilder.append("data").toString();
    }
}
