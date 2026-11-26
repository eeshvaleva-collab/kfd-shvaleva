### Монолит:
- #### [Коммит монолита](https://github.com/eeshvaleva-collab/kfd-shvaleva/commit/f7c47e0087c59c9e7684987597d5aaf692880d34#diff-0dccbab0e09594b2c371a073308737954f922a009d2efee326f757c6d484be30)
- #### [Успешный прогон CI](https://github.com/eeshvaleva-collab/kfd-shvaleva/actions/runs/45889029218)

### Разделение на два модуля: cli и core
- #### [Коммит многомодульной версии]
- #### [Успешный прогон CI для этого коммита]

### Команда запуска тестов (генерируется временная директория с тестовыми файлами):
```bash
./gradlew :core:test
```
### Команда запуска (в качестве аргумента указан путь к тестовым файлам):
```bash
./gradlew :cli:run --args="src/test/resources"
```

Вывод на примере из четырёх файлов с помощью команды выше:
```
Files sorted by extension:
kt: 2 files, 4 lines
md: 1 file, 0 lines

Top 3 longest files:
A.kt (2)
B.kt (2)
README.md (0)
```