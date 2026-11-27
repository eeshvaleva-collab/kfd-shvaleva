### Монолит:
- #### [Коммит монолита](https://github.com/eeshvaleva-collab/kfd-shvaleva/commit/f7c47e0087c59c9e7684987597d5aaf692880d34#diff-0dccbab0e09594b2c371a073308737954f922a009d2efee326f757c6d484be30)
- #### [Успешный прогон CI](https://github.com/eeshvaleva-collab/kfd-shvaleva/actions/runs/45889029218)

### Разделение на два модуля: cli и core
- #### [Коммит многомодульной версии](https://github.com/eeshvaleva-collab/kfd-shvaleva/commit/a4ef93d45cd978f5145c1959509c96aad6a1485f)
- #### [Успешный прогон CI для этого коммита](https://github.com/eeshvaleva-collab/kfd-shvaleva/actions/runs/45913895775)

---

### Команда запуска тестов (генерируется временная директория с тестовыми файлами):
```bash
./gradlew :core:test
```
### Команда запуска (в качестве аргумента указан путь к тестовым файлам):
```bash
./gradlew :cli:run --args="src/test/resources"
```

**Вывод на примере из четырёх файлов с помощью команды выше:**
```console
Files sorted by extension:
kt: 2 files, 4 lines
md: 1 file, 0 lines

Top 3 longest files:
A.kt (2)
B.kt (2)
README.md (0)
```

---

### Результат эксперимента со сборкой:

**Прогноз:**
`:core:compileKotlin` выполнится заново (так как я поменяла код внутри :core), 
а `:cli:compileKotlin` получит статус UP-TO-DATE, потому что публичные функции и классы :core
не изменились


**Вывод для команды `./gradlew build --console=plain` до изменения**:
```console
> Task :core:compileKotlin
> Task :core:jar
> Task :cli:compileKotlin
> Task :cli:jar
...
BUILD SUCCESSFUL in 3s
12 actionable tasks: 10 executed, 2 up-to-date
...

BUILD SUCCESSFUL in 3s
12 actionable tasks: 10 executed, 2 up-to-date
```

**Вывод для команды `./gradlew build --console=plain` после изменения:**
```console
> Task :core:compileKotlin
> Task :core:jar
> Task :cli:compileKotlin
> Task :cli:jar UP-TO-DATE
...
BUILD SUCCESSFUL in 2s
12 actionable tasks: 10 executed, 2 up-to-date
```

**Фактический результат после правки:**

После повторного запуска ./gradlew build --console=plain без clean обе задачи (:core:compileKotlin, и :cli:compileKotlin) запустились без пометки UP-TO-DATE

**Объяснение:**
Модуль :cli при компиляции опирается на собранный архив :core:jar 

Когда :core пересобрался, обновился и сам :core:jar, и Gradle запустил задачу :cli:compileKotlin
компилятор проверил, что публичный интерфейс :core остался прежним и не стал перекомпилировать файлы :cli -
задача :cli:jar сразу получила статус UP-TO-DATE