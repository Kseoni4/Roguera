# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Нерушимые законы для ИИ-агента

1. **ИИ-агент абсолютно всегда подтверждает у пользователя любое необратимое изменение файлов из источника**, например удаление.
2. **ИИ-агент может принимать собственные решения по изменению файлов**, если они не противоречат пункту 1.
3. **ИИ-агент всегда рассматривает своё решение с точки зрения лучших практик**, применяемых в контексте технологического стека проекта, если эти практики не противоречат пунктам 1 и 2.

Парадоксальные ситуации, не имеющие решения за полиномиальное время, разрешаются через консультацию с пользователем.

---

## Проект

ASCII-рогалик на Java, отрисовка идёт через эмулятор терминала [Lanterna](https://github.com/mabe02/lanterna) (поверх Swing). Это ветка 4.0 — переписанная версия старой кодовой базы на Java 11. Комментарии и отладочный вывод преимущественно на русском.

## Сборка и запуск

```bash
mvn package           # компилирует и копирует зависимости в target/libs/
java -jar target/Roguera_4.0-1.0-SNAPSHOT.jar
```

- Java 21 (`maven.compiler.source/target`).
- Тестовых исходников и тест-раннера нет.
- Главный класс: `kseoni.ch.roguera.RogueraLauncher` (объявлен в `pom.xml` через `maven-jar-plugin`).
- В репозитории есть IntelliJ-конфигурация (`Roguera.iml`); из-за повсеместного использования `@Getter`/`@Setter`/`@Builder`/`@SneakyThrows` нужен IntelliJ с плагином Lombok.

## Конфигурация

`src/main/resources/game-settings.properties` — единственный источник runtime-настроек. `SettingsLoader` — process-wide синглтон, который читает файл лениво; смена значений требует пересборки (ресурсы читаются из classpath через `getResourceAsStream`, а не из рабочей директории).

Ключевые параметры:
- `game.random.seed` — пусто = seed из `SecureRandom`, либо `long` для воспроизводимой генерации. Активный seed выводится в stdout при старте.
- `dungeon.world.rooms.count` — количество комнат, которое получает `Floor`/генератор карты.
- `window.size.width/height`, `font.filepath`, `font.size` — параметры окна терминала.
- `debug.show.*` — подробный stdout по подсистемам (dungeon-generate, key-input, player-movement, draw-room, room-create, event-raise, system-info).
- `debug.dump.events` / `debug.dump.objects` — при выходе пишут `<сегодня>_events.txt` и `<сегодня>_objects.txt` в рабочую директорию (старые такие файлы лежат в корне репозитория).

## Архитектура

### Точка входа и жизненный цикл

`RogueraLauncher.main` → загрузка настроек → загрузка char-ассетов → создание `Window` → создание `GameLoop` → `gameLoop.start()` блокируется до закрытия окна → опциональный дамп `ObjectPool` / `EventLoop`.

### Игровой цикл (однопоточный)

`GameLoop.start` крутится, пока `Window.get().isNotClosed()`:
1. `EventLoop.get().pollEvents()` опрашивает Lanterna `KeyStroke` через `Window.keyInput()`, оборачивает в `Event` и кладёт в очередь.
2. `GameLoop.pollingEvents` разбирает очередь, диспатчит символьные клавиши через `Map<Character, Runnable>` (`g`=регенерация этажа, `r`=перерисовка, `q`=выход) и стрелки через `PlayerController.movePlayer`.
3. После обработки перерисовывает текущую `Room` (или весь `Floor`, если игрок прошёл через дверь).
4. `HeaderDrawer.draw()` и `Window.refresh()`, затем `Clock.tick()` усыпляет поток для ограничения 30 FPS.

В `EventLoop` есть `ExecutorService`, но его `init()` нигде не вызывается — события фактически обрабатываются синхронно в игровом потоке. Считай `EventLoop` фасадом для очереди и логирования, а не настоящим асинхронным шиной.

### Синглтоны (их много)

`Window`, `EventLoop`, `Dungeon`, `ObjectPool`, `AssetPool`, `SettingsLoader`, `Clock`, `RandomUtils` — все глобальные. Порядок инициализации важен: `Window.create(...)` обязан выполниться раньше всего, что зовёт `Window.get()` (например, статический инициализатор `KeyInput`, `HeaderDrawer`). `RandomUtils` инициализируется в статическом блоке и переинициализируется `RogueraLauncher`-ом, если задан `game.random.seed`.

### Модель карты

- `Dungeon` → `Floor`-ы → `Room`-ы → `Cell`-ы. Ячейка хранит `Deque<GameObject>` (стек, поэтому в одной ячейке может быть несколько объектов — для отрисовки и взаимодействия используется верхний).
- Координаты двойные: каждый `Room` хранит **локальные** позиции ячеек (начало координат — левый верхний угол комнаты) и `roomLeftTopPosition` для **глобального** смещения в подземелье. Канонический конвертер — `utils/Convert.toGlobalPosition(s)`; не повторяй арифметику смещения по месту.
- `MapDrawer.toScreen` удваивает ось X (`x * 2`), чтобы ячейки в терминале выглядели почти квадратными; вторая колонка заполняется символом стены или пробелом. Любой новый рендерер карты должен следовать этому правилу, иначе сломается выравнивание.
- В `Position` есть статические направления (`FRONT`/`BACK`/`LEFT`/`RIGHT` и `AroundPositions`). `FRONT` — это `+y` (юг на экране); `BACK` — `-y`. Не переопределяй их локально.

### Конвейер генерации карты

`map.generator.MapGenerate.generateFloor(roomCount)` последовательно прогоняет четыре фазы и проходит через `MapValidator`:

1. **PhaseOneCreateRooms** — расставляет `roomCount` комнат в случайных `(x,y)` в захардкоженных границах (`0..84`, `0..25`), размер каждой случайный 6–16.
2. **PhaseTwoMergeRooms** — кластеризует пересекающиеся комнаты (BFS по дистанции глобальных центров + проверка пересечений) и сливает каждый кластер в одну общую комнату через объединение множеств ячеек.
3. **PhaseThreeConnectClusters** — соединяет оставшиеся комнаты по возрастанию дистанции, ставит на обе стороны объект `Door` и связывает их через `Door.nextDoor`. Ось двери (горизонтальная или вертикальная) определяется тем, попадает ли центр второй комнаты в X- или Y-границы первой.
4. **MapValidator** — проверяет, что у каждой комнаты есть дверь и что граф дверей связный; при провале `MapGenerate.generateFloor` возвращает `null`.
5. **PhaseFourCreateRoomStructure** — растеризует спрайты стен (углы + горизонтальные/вертикальные кромки из `AssetPool`) по периметру множества ячеек каждой комнаты.

Конструктор `Floor` и `regenerate()` повторяют генерацию в `while (rooms == null)`, пока валидатор не пропустит — количество провалов логируется. `MapGenerate.MapGenerate_` (с подчёркиванием в конце) — устаревший прототип; новая работа идёт в `map.generator/`.

### Слои отрисовки

`Window` владеет тремя `RenderLayer`-ами по ключу `TGLayer` (`BACKGROUND`, `FOREGROUND`, `UI`), каждый оборачивает Lanterna `TextGraphics`. `MapDrawer` пишет в `BACKGROUND`; `UIDrawer` — в `UI`. `HeaderDrawer` обходит обёртки и рисует напрямую через `Window.getRawScreen().newTextGraphics()`, потому что ему нужны `SGR`-модификаторы и заливка прямоугольников.

Глифы стен/дверей грузятся из `src/main/resources/char-assets.rca` (формат строки: `name-<один символ>`) в `AssetPool` по имени (`wall_h`, `wall_v`, `wall_corner_top_l`, `door_h`, `door_v`, …). Доступ — `AssetPool.get().getAsset("...")`; в `PhaseFourCreateRoomStructure` эти символы сравниваются через `==` для определения ориентации углов.

Палитра цветов вынесена в `graphics.Palette` (Catppuccin Mocha). В новом UI-коде используй константы `Palette.*`, а не инлайновый `TextColor.RGB(...)`.

### Учёт объектов и событий

Каждый `GameObject` регистрируется в `ObjectPool` прямо в конструкторе (через автоинкремент `idCounter`). `Cell.replaceObject` удаляет вытесненный объект из пула. `EventLoop` отдельно ведёт строковый лог всех событий — он попадает в опциональный дамп при завершении.

### Модель взаимодействий

`Scriptable<T>` — функциональный интерфейс с единственным методом `doAction(T)`. `PlayerController.move` перед перемещением игрока проверяет `instanceof Scriptable` и вместо `placeObject` вызывает `doAction` — так `Door` телепортирует игрока между комнатами (и так же подключаются новые интерактивные сущности). Двери меняют `Floor.roomIdPointer`, поэтому `GameLoop` каждый кадр пере-сравнивает свою закешированную ссылку `room` с `Dungeon.get().currentFloor().currentRoom()`.

## Конвенции, которые стоит сохранять

- Lombok используется массово; на data-классах оставляй `@Getter`/`@Setter`/`@Builder`, а не пиши аксессоры руками.
- Отладочный вывод идёт через паттерн с `debug.show.*` (читаем флаг один раз в конструкторе в `final boolean`) — не логируй безусловно.
- `@SneakyThrows` используется везде, где I/O или `InterruptedException` иначе всплыли бы наверх — следуй соседнему стилю, а не добавляй `throws`.
- Пакеты `graphics/ui/parts/` и `graphics/ui/elements/`, плюс `Interactable.java`, — это незавершённая декларативная UI-система (см. `src/main/resources/ui/base-block.yml` и зависимость `snakeyaml`); она ещё не подключена к игровому циклу. Активный HUD — `HeaderDrawer`.
