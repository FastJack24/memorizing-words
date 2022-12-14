# Приложение для ускоренного запоминания слов
#### Консольное приложение, написанное на Java, призванное помочь в запоминании слов.
### Как начать использовать 
1) Скомпилируйте код в удобную вам директорию вручную или с помощью IDE (Java 11 или выше)
2) Запустите приложение в режиме загрузки, чтобы подгрузить файлы со словами, в первый 
запуск это обязательно (См. режим загрузки)
3) Запустите приложение в обычном режиме (См. использование приложения)

### Как устроено приложение
Обычно запоминать слова легче блоками по какому-либо материалу, поэтому часто задумываешься над
тем, чтобы оформлять их в большие группы по блокам, где в блоке будет по ~10-20 слов, чтобы было легче 
повторять. В пример можно привести разбор незнакомых слов, например, фильма, представляющего собой 
группу незнакомых слов, в которой есть ~4 блока по 10 слов в каждой. Приложение устроено таким 
образом, что слова собираются в группы, а группы в свою очередь состоят из блоков слов.

### Режим загрузки

Группы слов загружаются в виде текстовых файлов, блоки в которых разделены с помощью "-----", при 
этом путь к папке с файлами указывается в начале при запуске в режиме загрузки. Название группы 
формируется из имени файла без расширения txt. 
Пример текстового файла группы с двумя блоками текста по 3 слова:

    Слева от тирэ любой текст на изучаемом языке - справа перевод, допускаются предложения
    Dash - тирэ допускается только один раз в строке
    Elephant - Слон, но каждое слово на новой строке
    -----
    Dog - Собака или слон?
    Cat - Кошка
    Cloud - облако
    В конце и начале нет пустых строк
Для запуска приложения в режиме загрузки нужно подать аргумент при запуске равный 0, для запуска в 
обычном режиме аргумент подавать не нужно:
    
    java -classpath <абсолютный путь к папке с программой> Main 0
Если вы запускаете программу заново или хотите загрузить новые слова, то старые сохранятся.
### Использование приложения

При запуске программа предложит сделать выбор, какую группу сейчас нужно загрузить и далее 
даст возможность сделать выбор блока. Если подать **номер блока с буквой r**, например **2r**, то 
программа будет выводить слова **с вашего родного языка**, то есть запустится в обратном режиме 
(по умолчанию изучаемый язык - перевод на родной). После этого программа начинает вывод слов.
На каждое появление слова человек может думать сколько ему нужно и нажимает Enter, чтобы увидеть перевод.  
Когда список слов кончится, есть несколько следующих вариантов:
    
    b - возврат к выбору группы
    c - возврат к выбору блока
    r - повтор блока заново
    rr - повтор блока заново, но изменится порядок (e.g. с eng-rus на rus-eng)
    n - следующий блок
    p - предыдущий блок
    Запоминать не нужно, программа сама даст выбор.

### Пример работы программы
    andrew@andrew-Lenovo16ACH6:~$ java -classpath 
    /home/andrew/idea/IdeaProjects/MemorizingWords/target/classes/ Main 0
    
    Введите aбсолютный путь к папке с файлами формата txt с форматированными словами:
    /home/andrew/Desktop/Words
    
    Директория для хранения ваших слов 
    /home/andrew/idea/IdeaProjects/MemorizingWords/target/classes/data была успешно создана.

    Создание директории для Batman.txt в data успешно завершено.
    Завершено создание файлов текстовых блоков.
    Завершена запись файла /home/andrew/idea/IdeaProjects/MemorizingWords/target/classes/data/Batman/INFO.txt.

    Создание директории для Shawshank Redemtion.txt в data успешно завершено.
    Завершено создание файлов текстовых блоков.
    Завершена запись файла /home/andrew/idea/IdeaProjects/MemorizingWords/target/classes/data/Shawshank Redemtion/INFO.txt.

    andrew@andrew-Lenovo16ACH6:~$ java -classpath 
    /home/andrew/idea/IdeaProjects/MemorizingWords/target/classes/ Main
    
    ===================
    
    Группы для повторения:
    1. Batman
    2. Shawshank Redemtion

    Введите номер группы:
    2

    ===================

    Блок номер 1
    Количество слов: 22
    
    Блок номер 2
    Количество слов: 2
    
    Введите номер блока:
    2

    ===================

    ----------
    Scams
    
    мошенничество
    ----------
    Whatnot
    
    всякая всячина
    ----------
    
    Введите что нужно делать дальше (b(к группам), c(к блокам), r(повтор блока),
    rr(повтор блока в обратном направлении),n(следующий блок), p(previous block)):
