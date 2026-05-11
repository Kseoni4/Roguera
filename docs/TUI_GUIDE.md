# TUI Roguera 4.0 — гайд по архитектуре

Документ описывает UI-подсистему, которая была введена в ветке `dev-4.0_tui`.
Если ты читаешь это спустя время после рефакторинга — это карта по коду:
зачем каждый класс, как он связан с соседями, в каком порядке всё рендерится
и где не надо аллоцировать.

Документ устарел в одном месте только тогда, когда ты сам что-то поменяешь.
Если правишь архитектуру — обнови соответствующий раздел.

---

## 0. TL;DR

```
┌─────────────────────────────────────────────────────┐  ←── Header  (FPS/pos)
│ ┌─ Player ─┐ ┌─── Map ────────┐ ┌─── Events ─────┐ │
│ │ Name:    │ │                │ │ > событие 1    │ │
│ │ Floor:   │ │      ╭───╮     │ │ > событие 2    │ │
│ │ Room:    │ │      │ @ │     │ │                │ │
│ │ Local:   │ │      ╰───╯     │ │                │ │
│ │ World:   │ │                │ │                │ │
│ │ Seed:    │ │                │ │                │ │
│ └──────────┘ └────────────────┘ └────────────────┘ │
│ ──────────────────────────────────────────────────  │  ←── Footer  (подсказки)
│  ↑↓←→ move │ g regen │ r redraw │ q quit           │
└─────────────────────────────────────────────────────┘
```

- Экран бьётся на 5 регионов через `Layout`: header / sidebarLeft / main / sidebarRight / footer.
- Каждый регион рисуется своим компонентом, пишущим в `RenderLayer` (обёртка
  над `TextGraphics` Lanterna).
- Игрок и двери поверх стационарной карты пульсируют через `AnimationDrawer`.
- Карта скроллится вместе с игроком через `Camera` (edge-scroll deadzone).
- В hot loop (30 fps) перерисовываются только header и анимация. Sidebars и
  footer перерисовываются только когда событие или клавиша; иначе heap
  раздувался бы от `TextCharacter`-ов.

---

## 1. Что было до и что стало

### 1.1. Было

- `Window` создавал сетку терминала, грубо `210×30` (cols×rows).
- Под картой и UI был чёрный фон по умолчанию (Lanterna ANSI default).
- `HeaderDrawer` рисовал статус-строку, обходя обёртки `RenderLayer` —
  работал напрямую через `Window.getRawScreen().newTextGraphics()` и
  держал собственный набор RGB-литералов в стиле Catppuccin.
- `MapDrawer` рисовал карту в координатах `(x*2, y)` от `(0, 0)` экрана —
  при больших комнатах часть карты уходила за правый край окна и просто
  пропадала.
- `MapDrawer.clear()` дёргал `terminal.clear()` — стирал не только карту,
  но и шапку.
- В `graphics/ui/parts/` лежали пустые DTO (`UIPart`, `UIFrame`,
  `UIElement`) — заготовка под декларативную UI-систему, которую
  предполагалось грузить из YAML.

### 1.2. Стало

- Размер окна можно задавать в **пикселях** (`window.pixel.width/height`),
  Lanterna сама пересчитает их в колонки/строки через метрики шрифта.
- На всё окно лежит фон `Palette.BASE` (Catppuccin base) — больше нет
  чёрной пропасти под UI.
- Экран разбит на 5 фиксированных регионов через `Layout`.
- Все компоненты пишут в общую обёртку `RenderLayer` — никто больше не
  ходит в raw `TextGraphics`.
- Карта живёт внутри своего региона и обрезается по нему — за неё отвечает
  `Camera` со скроллингом-следованием за игроком.
- Цвета рамок и спрайты декларируются в `char-assets.rca` (один источник
  истины — тот же, что для стен карты).
- `UIFrame` стал реальным рендером рамки со встроенным заголовком; `Panel`
  даёт абстракцию «рамка + content-region».
- В hot loop минимум аллокаций: только то, что реально меняется.

### 1.3. Хронология коммитов в `dev-4.0_tui`

| коммит    | что          |
|-----------|--------------|
| `f091aab` | tui-1: backdrop `Palette.BASE` + окно 210×40 |
| `f606de5` | tui-1.1: `window.pixel.*` — размер в пикселях |
| `82c37be` | tui-2: Layout / Panel / BorderStyle / Region |
| `c618836` | tui-2.1: BorderStyle через AssetPool |
| `d618082` | PhaseThree: `createDoor` + bg=BASE для дверей |
| `80fceb8` | tui-3: HeaderDrawer на Region + RenderLayer |
| `b2c6528` | tui-4: рамка вокруг карты + region-clear |
| `038160b` | tui-4.1: Camera + edge-scroll viewport |
| `341db84` | fix: убрать промежуточный refresh из drawRoom |
| `39fc89c` | tui-5: PlayerSidebar + EventsSidebar |
| `e81c99d` | tui-6: Footer с подсказками клавиш |
| `a6f53ab` | tui-7: AnimationDrawer — pulse игрока и дверей |
| `29f35e3` | perf: убрать per-frame аллокации UI в idle |

Если что-то непонятно в текущем коде — `git log --oneline -- <файл>`
покажет в каком из этих коммитов оно появилось, а `git show <sha>` —
полный diff.

---

## 2. Архитектура

### 2.1. Слои Lanterna и наша обёртка

Lanterna 3.2.0-alpha1 у нас работает поверх Swing (`SwingTerminalFrame`).
Структура:

```
JFrame (Swing window)
  └── SwingTerminal (рисует знакоместа в Graphics2D)
        └── TerminalScreen (back-buffer + front-buffer; double buffering)
              └── TextGraphics (API для записи в back-buffer)
```

Ключевое — **double buffering**. Все наши `setCharacter(x, y, ch)` пишут в
back-buffer; `terminal.refresh()` сравнивает back с front и пушит на
SwingTerminal только реально изменившиеся ячейки. Поэтому промежуточные
состояния «полу-нарисованного» экрана пользователь не видит — но **только
если мы не вызываем refresh между этапами рендера**. Был баг
(`341db84`), где `drawRoom` зачем-то делал `refresh()` в цикле, и
пользователь видел поэтапную отрисовку. Теперь refresh — один в конце
кадра.

`Window` владеет тремя `TextGraphics`-ами (по числу логических слоёв),
обёрнутыми в `RenderLayer`:

| `TGLayer.BACKGROUND` | стационарная карта (стены, пол, статичные объекты) |
| `TGLayer.FOREGROUND` | сейчас не используется, зарезервирован |
| `TGLayer.UI`         | рамки панелей, header, sidebars, footer |

**Важный нюанс**: «слои» в Lanterna — это просто разные `TextGraphics`,
которые пишут в **один и тот же back-buffer**. Z-order не существует —
кто записал в ячейку последним, тот и виден. Поэтому порядок вызова
`render()` имеет значение.

### 2.2. Иерархия абстракций

```
Layout (раскладывает экран на 5 регионов)
   │
   ├── Region (4 числа — x/y/width/height)
   │     │
   │     └── BorderStyle (символы рамки из AssetPool)
   │           │
   │           └── UIFrame (рисует рамку + встроенный заголовок)
   │                 │
   │                 └── Panel (UIFrame + content-region для подкласса)
   │
   └── Camera (Region viewport + worldCenter; world↔screen)
         │
         └── MapDrawer (рисует ячейки карты через Camera)

RenderLayer (TextGraphics + удобные методы fill/putString/setChar)
   ↑
   │ используется всеми drawer-ами
   │
GameLoop (склеивает всё: Layout → drawers → render-цикл)
```

### 2.3. Источники истины

- **Цвета** — `graphics/Palette.java`. Catppuccin Mocha. Никто не должен
  хардкодить `new TextColor.RGB(...)` за пределами Palette.
- **Символы рамок** — `src/main/resources/char-assets.rca`, префикс
  `border.<style>.<part>`.
- **Размер окна и боковых панелей** — `src/main/resources/game-settings.properties`
  (только размер окна) и константы `Layout.HEADER_HEIGHT`/`FOOTER_HEIGHT`/`SIDEBAR_WIDTH`.

---

## 3. Компоненты — детальный разбор

### 3.1. `graphics.render.Window`

Файл: `src/main/java/kseoni/ch/roguera/graphics/render/Window.java`.

Singleton-обёртка над Lanterna `TerminalScreen`. Делает три вещи:

1. **Создаёт окно** (singleton, `Window.create(title)`).
2. **Резолвит размер сетки**. Lanterna меряет окно в колонках/строках, а
   нам удобно задать физический размер в пикселях. Метод
   `resolveGridSize(props, font)`:
   - читает `window.pixel.width/height` из properties;
   - если они заданы — берёт `font.getFontWidth()` и `getFontHeight()`
     (метрики моноширинного шрифта) и делит pixel-размеры на размер
     ячейки;
   - если pixel-поля пустые — fallback на `window.size.width/height`
     (колонки/строки напрямую).
   - в stdout печатает: `Window grid: COLSxROWS (cell WxHpx ≈ NNNxNNN px)`.
3. **Заливает backdrop**. После `terminal.startScreen()` весь экран
   заливается `Palette.BASE` через `fillBackdrop()` — иначе Lanterna
   оставила бы дефолтный ANSI-фон. Тот же метод вызывается из
   `clearScreen()`.

API наружу:

| метод | что |
|-------|-----|
| `Window.create(title)` | создать singleton |
| `Window.get()` | взять существующий |
| `getRenderLayer(TGLayer)` | дать `RenderLayer` слоя |
| `getWight()` / `getHeight()` | размер сетки в cols/rows |
| `clearScreen()` | очистить + перезалить `Palette.BASE` |
| `refresh()` | пушнуть back-buffer на SwingTerminal |
| `keyInput()` | дёрнуть `KeyStroke` неблокирующе |
| `getRawScreen()` | прямая ссылка на `TerminalScreen` (нужна нескольким legacy-местам) |

**Порядок инициализации важен**:
1. `font` → 2. `fontConfiguration` → 3. `resolveGridSize` (нужны метрики)
→ 4. `factory.setInitialTerminalSize(grid)` → 5. `setTerminalEmulatorFontConfiguration(font)`.

Если `setInitialTerminalSize` вызвать до создания `fontConfiguration` —
метрики недоступны, размер посчитать нельзя.

### 3.2. `graphics.render.RenderLayer`

Файл: `src/main/java/kseoni/ch/roguera/graphics/render/RenderLayer.java`.

Тонкая обёртка над `TextGraphics`. Все компоненты UI пишут только сюда,
никто не лезет в raw `TextGraphics`.

| метод | что |
|-------|-----|
| `setChar(x, y, ch, fg, bg)` | записать одиночный символ |
| `fill(region, ch, fg, bg)` | залить регион одним символом |
| `putString(x, y, text, fg, bg, sgr...)` | вывести строку (опционально с SGR-модификаторами вроде BOLD) |
| `drawSpriteOn(sprite, pos)` | legacy: рисовать `TextSprite` целиком |
| `drawSpriteLine(sprite, from, to)` | legacy: рисовать линию из спрайтов |

**Замечание про `putString`**: каждый вызов внутри создаёт `text.length()`
штук `TextCharacter`. Это критично для аллокаций — не вызывай его в hot
loop без необходимости.

### 3.3. `graphics.ui.layout.Region`

Java record (Java 21):

```java
public record Region(int x, int y, int width, int height) {
    public int right();
    public int bottom();
    public boolean contains(int px, int py);
    public Region inset(int by);
    public Region inset(int top, int right, int bottom, int left);
}
```

Не зависит от Lanterna и от чего бы то ни было. Используется везде, где
нужно «прямоугольник на экране». `inset(1)` — частый приём: получить
content-region из region рамки.

### 3.4. `graphics.ui.layout.BorderStyle`

Файл: `graphics/ui/layout/BorderStyle.java`.

Enum с пресетами рамок:

| `ROUNDED` | `╭╮╰╯─│` | пассивные блоки, помещения |
| `SHARP`   | `┌┐└┘─│` | статичный UI: статус, лог, footer |
| `HEAVY`   | `┏┓┗┛━┃` | активная/сфокусированная панель |
| `DOUBLE`  | `╔╗╚╝═║` | модальные диалоги |

**Важно**: символы захардкожены не в коде. Каждое значение enum хранит
только короткий ключ-префикс (`"rounded"`, `"sharp"` и т. д.), а в
`ensureLoaded()` (вызывается лениво при первом обращении) подтягивает
символы из `AssetPool`:

```
border.rounded.tl-╭
border.rounded.tr-╮
...
border.heavy.h-━
border.heavy.v-┃
```

Все 24 декларации лежат в `src/main/resources/char-assets.rca`. Если
захочешь поменять, скажем, угловые символы для retro-темы — это правка
одного файла без перекомпиляции.

Геттеры — методы (`topLeft()`, `horizontal()`), не поля. Поля
закешированы лениво один раз.

### 3.5. `graphics.ui.layout.Layout`

Файл: `graphics/ui/layout/Layout.java`.

Статическая раскладка экрана. Конструктор принимает `cols × rows`
(берётся из `Window.getWight()` / `getHeight()`), считает 5 регионов:

```java
HEADER_HEIGHT = 1;       // верхний статус-бар
FOOTER_HEIGHT = 2;       // separator + подсказки клавиш
SIDEBAR_WIDTH = 28;      // и левый, и правый

screen        = (0, 0, cols, rows)
header        = (0, 0, cols, 1)
sidebarLeft   = (0, 1, 28, rows-3)
main          = (28, 1, cols-56, rows-3)
sidebarRight  = (cols-28, 1, 28, rows-3)
footer        = (0, rows-2, cols, 2)
```

Если будешь править размеры — менять надо здесь (конкретные регионы
автоматически пересчитаются от констант). Если захочется тематизации
через settings — вытащи константы в `game-settings.properties` и читай
в конструкторе через `SettingsLoader`.

### 3.6. `graphics.ui.layout.Camera`

Файл: `graphics/ui/layout/Camera.java`.

Виртуальная камера, преобразует world-координаты карты в screen-координаты
внутри своего viewport.

Важные поля:
- `viewport` — `Region` на экране (= внутренность рамки карты, `main.inset(1)`).
- `scaleX` — сколько колонок терминала занимает одна world-ячейка по горизонтали.
  Для рогалика = 2 (квадратное знакоместо).
- `worldCenter` — текущая world-координата, которая отображается в центре viewport.
- `viewCellsW`, `viewCellsH` — размер viewport в **world-ячейках** (не в cols).
- `deadzoneRadiusX`, `deadzoneRadiusY` — четверть viewport. Размер «мёртвой
  зоны», в которой движение игрока **не** двигает камеру.

API:

```java
camera.centerOn(worldPos)      // жёстко поставить центр (init / regen)
camera.follow(worldPos)        // edge-scroll: подвинуть только если worldPos вышел за deadzone
camera.worldToScreen(worldPos) // world → screen position
camera.isVisible(sx, sy)       // попадает ли точка в viewport
```

#### Edge-scroll подробно

Когда игрок ходит **внутри** deadzone (центральный квадрат viewport),
камера **не двигается**. При выходе за границу deadzone камера сдвигается
ровно настолько, чтобы вернуть игрока на границу deadzone:

```
если dx > +deadzoneRadiusX → cx = playerX - deadzoneRadiusX
если dx < -deadzoneRadiusX → cx = playerX + deadzoneRadiusX
(аналогично по Y)
```

Это даёт мягкий «следящий» эффект как в Brogue/ADOM: окрестности игрока
стоят на месте, пока он не подойдёт к краю viewport.

#### Преобразование world → screen

```java
sx = viewport.x + (world.x - center.x + viewCellsW/2) * scaleX
sy = viewport.y + (world.y - center.y + viewCellsH/2)
```

`viewCellsW/2` — это смещение «мира под центром камеры» в screen. Если
игрок находится в `worldCenter`, то его экранная позиция — точно центр
viewport.

### 3.7. `graphics.ui.parts.UIFrame`

Файл: `graphics/ui/parts/UIFrame.java`.

Рендерер прямоугольной рамки с встроенным заголовком (стиль lazygit:
`─ Title ─...`). Сам не знает о контенте — это чистая обвязка.

Поля:
- `region` — что обрисовать.
- `style` — `BorderStyle`.
- `title` — опционально, может быть `null`.
- `borderFg` / `titleFg` / `bg` — цвета по умолчанию из Palette.

Метод `render(layer)` в порядке:
1. **Заливает весь регион** пробелом с `bg` — гарантирует, что фон под
   рамкой соответствует UI-теме (а не «протекает» backdrop).
2. Рисует горизонтальные кромки (top + bottom).
3. Рисует вертикальные кромки (left + right).
4. Поверх кромок ставит четыре угла.
5. Если есть title — записывает `" Title "` поверх верхней кромки на
   позиции `(left+2, top)`. Если title не влезает — обрезается.

`hChar`/`vChar` (символы линий) кешируются в локальные переменные перед
циклом, потому что `style.horizontal()` теоретически может ходить через
HashMap (хоть AssetPool кеширует, но один раз — лучше и сами кешируем).

### 3.8. `graphics.ui.Panel`

Файл: `graphics/ui/Panel.java`.

Абстрактная база: «рамка + контент». Используется sidebars.

```java
public abstract class Panel {
    private final Region region;
    private final UIFrame frame;

    public final void render() {
        RenderLayer layer = Window.get().getRenderLayer(TGLayer.UI);
        frame.render(layer);
        renderContent(layer, contentRegion());
    }

    protected abstract void renderContent(RenderLayer layer, Region content);
}
```

Подклассу остаётся только `renderContent(layer, content)`: layer уже
взят, content-region уже посчитан как `region.inset(1)`. Подкласс не
заботится о рамке вообще.

**Не каждый UI-компонент должен наследовать `Panel`**. Header и Footer
концептуально не нуждаются в рамке (это плоские статусные строки), и
для одной строки рамка съела бы весь контент. Они написаны как
самостоятельные `*Drawer` без наследования.

### 3.9. `graphics.ui.HeaderDrawer`

Файл: `graphics/ui/HeaderDrawer.java`.

Однострочный header. Рисует:
- слева: `title` (Catppuccin MAUVE, BOLD) + `version` (OVERLAY);
- справа: `FPS NN` (цвет по порогу: GREEN / YELLOW / RED) + `@ X,Y`
  (GREEN, world-координаты игрока).

Конструктор берёт `Region` (= `layout.getHeader()`) и `Player`. В
constructorе разбирает строку `game.version` из settings, отделяя
название от версии (по подстроке `" build "`).

Метод `draw()` каждый раз заливает фон строки `Palette.SURFACE0`,
выводит куски putString-ами. Вызывается каждый кадр, потому что FPS
обновляется ~раз в полсекунды и координаты меняются при движении.

**Аллокации**: каждый кадр — два `String.format` (FPS + позиция) +
сколько-то `TextCharacter` через putString (~50 символов). Это
терпимо, но если будешь оптимизировать — можно кешировать строку и
обновлять только когда `currentFps` или `worldPos` реально изменились.

### 3.10. `graphics.ui.MapDrawer`

Файл: `graphics/ui/MapDrawer.java`.

Рисует ячейки карты на BACKGROUND-слое внутри переданного
viewport-региона через `Camera`.

Конструктор:
```java
new MapDrawer(viewport: Region, camera: Camera)
```

Где `viewport == layout.getMain().inset(1)` (внутренность рамки карты).

Метод `draw(cell, relativePosition)`:
1. `world = cell.position + relativePosition` (relativePosition = `Room.roomLeftTop`).
2. `screen = camera.worldToScreen(world)`.
3. Если `(screen)` или `(screen + 1, screen.y)` выпадает за viewport —
   `return` (ячейка обрезана).
4. Рисует спрайт в `(screen)`.
5. Заполняет вторую колонку (`screen.x + 1`) пробелом или, если это
   стена `wall_h`, повтором того же символа — чтобы стены смотрелись
   сплошными.

`clear()` теперь **не** дёргает `Window.clearScreen()`, а заливает
свой viewport через `mapLayer.fill(...)`. Header и sidebars при regen
не страдают.

### 3.11. `graphics.ui.PlayerSidebar`

Файл: `graphics/ui/PlayerSidebar.java`.

Левый sidebar, наследник `Panel`. Title `"Player"`, `BorderStyle.SHARP`.

В `renderContent(layer, content)` выводит:
- `Name: <player.name>` (BOLD, цвет Palette.TEXT)
- (пустая строка)
- `Floor: <number>` / `Room: <id>` (GREEN)
- (пустая строка)
- `Local: x,y` / `World: X,Y` (SUBTEXT)
- (пустая строка)
- `Seed: <long>` (OVERLAY)

Хелпер `putLabeled` пишет `"<label>: "` цветом OVERLAY, потом значение
цветом-параметром BOLD.

### 3.12. `graphics.ui.EventsSidebar`

Файл: `graphics/ui/EventsSidebar.java`.

Правый sidebar, наследник `Panel`. Title `"Events"`, `BorderStyle.SHARP`.

В `renderContent` показывает последние N строк из `EventLoop.getEventLog()`,
где N = `content.height()`. Если строк больше, чем влазит — показываются
последние.

`compact(s)` сжимает verbose-формат `[2026-04-28T10:20:00]EVENT Event(value=...)`
до значения внутри `Event(value=...)` (у `Event` `@ToString` через Lombok
выдаёт строго такой вид).

`colorOf(s)` — простая эвристика по содержимому:
- содержит `"Door"` → MAUVE;
- содержит `"Window"` → BLUE;
- содержит `"Regenerate"` → YELLOW;
- содержит `"removed"`/`"cleared"` → OVERLAY;
- иначе → SUBTEXT.

Длинные строки обрезаются и кончаются на `…`.

### 3.13. `graphics.ui.FooterDrawer`

Файл: `graphics/ui/FooterDrawer.java`.

Двухстрочный footer **без рамки** (рамка съела бы оба ряда). Стиль:
- Верхняя строка — горизонтальная линия `─` цветом SURFACE1
  (визуально отделяет footer от main).
- Нижняя — фон Palette.BASE + последовательность сегментов.

Сегменты: `↑↓←→ move │ g regen │ r redraw │ q quit`. Имена клавиш —
MAUVE+BOLD, описания — OVERLAY, разделители `  │  ` — SURFACE1.

Метод `putKey/putLabel/putSep` вычисляют позицию x для следующего
сегмента, возвращая новый x.

Если в шрифте нет `↑↓←→` (Lanterna покажет квадратик) — заменить
на ASCII-строку `<>v^` или слово `Arrows`.

### 3.14. `graphics.ui.AnimationDrawer`

Файл: `graphics/ui/AnimationDrawer.java`.

Поверх стационарной карты каждый кадр перерисовывает только анимированные
ячейки на BACKGROUND-слое:

- **Игрок** `@`: GREEN ↔ GREEN+50 brightness, период 1.5 сек.
- **Двери** `▢`/`▣`: MAUVE ↔ PINK, период 2.0 сек.

Периоды разные, чтобы фазы не совпадали — выглядит «живо».

#### Как работает pulse

```java
double phase = (sin(now * 2π / periodMs) + 1) / 2;   // 0..1
return interpolate(c1, c2, phase);
```

`interpolate(a, b, t)` — линейная интерполяция RGB:
`r = round(a.r * (1 - t) + b.r * t)` (и так для G, B). Возвращает новый
`TextColor.RGB`.

`now` — `System.currentTimeMillis()`. Привязка к старту приложения не
нужна, sin сама создаёт периодичность.

#### Где это рисуется

На BACKGROUND-слой, не на UI. Карта тоже на BACKGROUND, поэтому когда
после `redrawFloor` карты MapDrawer ставит статичный спрайт игрока,
`AnimationDrawer.draw` сразу следом перепишет ту же координату
анимированным цветом — пользователь увидит уже анимацию. Между этими
двумя записями `refresh()` не происходит.

#### Производительность

На каждой анимированной ячейке — один `setChar` (один `TextCharacter`).
Раньше было `new TextSprite + drawSpriteOn` × 2, что давало 4
аллокации. Сейчас 4 `TextCharacter` на игрока + 4 на каждую дверь, что
в типичном этаже = ~20 `TextCharacter` × 30 fps = ~600/сек. Несущественно.

### 3.15. `graphics.Palette`

Файл: `graphics/Palette.java`.

Статические константы цветов (Catppuccin Mocha). Используется
**везде**, никто другой не должен делать `new TextColor.RGB(...)` без
особой причины (например, интерполяция в AnimationDrawer).

Полезные группы:
- `BASE`/`MANTLE`/`SURFACE0`/`SURFACE1` — оттенки фона от тёмного к
  светлому;
- `TEXT`/`SUBTEXT`/`OVERLAY` — оттенки текста от яркого к приглушённому;
- `RED`/`PEACH`/`YELLOW`/`GREEN`/`TEAL`/`BLUE`/`MAUVE`/`PINK` —
  акценты.

---

## 4. Жизненный цикл рендера

### 4.1. Инициализация

В `RogueraLauncher.main`:

1. `SettingsLoader.load(GAME_SETTINGS)` — читает settings.
2. `AssetPool.get().loadAssets("/char-assets.rca")` — грузит все
   символы (стены, двери, рамки UI).
3. `Window window = Window.create(version)` — создаёт окно (внутри
   `resolveGridSize` + `fillBackdrop`).
4. `RandomUtils.setSeed(...)` если в settings задан seed.
5. `gameLoop = new GameLoop()` — конструктор:
   - `layout = new Layout(cols, rows)`;
   - `camera = new Camera(layout.getMain().inset(1), 2, (0,0))`;
   - `mapDrawer = new MapDrawer(layout.getMain().inset(1), camera)`;
   - `mapFrame = new UIFrame(layout.getMain(), SHARP, "Map")`;
   - создаются `Player`, `PlayerController`;
   - `dungeon`, `floor`, `room` берутся из `Dungeon.get()` (singleton);
   - `headerDrawer`, `playerSidebar`, `eventsSidebar`, `footerDrawer`,
     `animationDrawer`.
6. `gameLoop.init()`:
   - игрок ставится в `(1, 2)` локальной координаты комнаты;
   - `camera.centerOn(playerWorldPos())`;
   - `mapFrame.render(...)` — рисует рамку карты на UI-слое **один раз**;
   - `redrawFloor(floor)` — заливает viewport BASE и рисует все комнаты;
   - `footerDrawer.draw()` — рисует footer **один раз**;
   - `playerSidebar.render()` и `eventsSidebar.render()` — первичная
     отрисовка sidebars.
7. `gameLoop.start()` — входит в hot loop.

### 4.2. Hot loop (каждый кадр, 30 fps)

```java
while (Window.get().isNotClosed()) {
    pollingEvents();                           // обработка input — может перерисовать sidebars
    animationDrawer.draw(now, floor, room);    // pulse игрока + дверей
    headerDrawer.draw();                       // FPS + позиция
    Window.get().refresh();                    // diff back vs front, push на screen
    Clock.tick(frameStart);                    // sleep до 30 fps
}
```

**Что перерисовывается каждый кадр:**
- ~10 ячеек анимации (игрок + двери);
- header (`SURFACE0` фон + ~50 символов).

**Что не перерисовывается каждый кадр:**
- Карта в целом (рисуется только в `redrawFloor`, который вызывается
  при init / regen / move).
- Sidebars и footer (см. ниже).

### 4.3. `pollingEvents` — реакция на input

Внутри одного «события» (нажатие клавиши):

```java
char-key — ищется в keyBindings, выполняется (g/r/q).
arrow-key — playerController.movePlayer(keyType).

if (currentRoom != room) room = currentRoom;   // прошли через дверь
camera.follow(playerWorldPos());
redrawFloor(floor);                             // полный перерендер карты
playerSidebar.render();                         // обновить статус
eventsSidebar.render();                         // обновить ленту событий
```

То есть после **каждого реального действия** игрока:
- камера может сдвинуться (если он вышел из deadzone);
- карта перерисовывается полностью (на BACKGROUND);
- sidebars перерисовываются (на UI).

В idle (нет input) ни sidebar, ни footer, ни карта не пишутся в
back-buffer. Только `animationDrawer` и `headerDrawer`.

### 4.4. `redrawFloor`

```java
mapDrawer.clear();                  // fill viewport на BACKGROUND
for (Room room : floor.getRooms())
    drawRoom(room);                 // НЕ вызывает refresh
mapDrawer.refresh();                // один refresh в конце
```

Раньше внутри `drawRoom` был `mapDrawer.refresh()` после каждой
комнаты — это пушило на screen промежуточные кадры (комната 1, потом
1+2, потом 1+2+3) → видимое мерцание. Теперь refresh один и в конце.

---

## 5. Производительность

### 5.1. Цепочка аллокаций

В Java каждый `new SomeObject(...)` → молодое поколение → когда заполнится
→ minor GC. Если allocation rate высок — GC чаще, видны «пилы» в
профайлере (heap пиково растёт, потом проседает).

В Lanterna каждое «знакоместо» — это `TextCharacter`, неизменяемый
объект. `setCharacter(x, y, ch)` принимает либо уже готовый
`TextCharacter`, либо распакует его из `Symbol`. Каждый вызов
`putString(text, fg, bg, ...)` — это **`text.length()` штук
`TextCharacter`**.

**Что мы оптимизировали:**

| компонент    | до                                            | после                                 |
|--------------|-----------------------------------------------|---------------------------------------|
| Sidebars     | `render()` каждый кадр (~1000 TextChar)       | `render()` только при input           |
| Footer       | `draw()` каждый кадр (~70 TextChar)           | `draw()` один раз в init              |
| Animation    | 2 `new TextSprite` per ячейка → drawSpriteOn  | 1 `setChar` per ячейка                |
| MapDrawer    | каждый refresh после каждой комнаты           | один refresh в конце redrawFloor      |

### 5.2. Что аллоцируется в idle сейчас

Только `animationDrawer.draw` + `headerDrawer.draw`:
- `pulse` создаёт два новых `TextColor.RGB` (для игрока и для дверей)
  → ~2 объекта/кадр × 30 = 60/сек;
- `setChar` на анимации → ~10 `TextCharacter` × 30 = 300/сек;
- header putString → ~50 `TextCharacter` × 30 = 1500/сек;
- header `String.format` → 2 String / кадр = 60/сек.

Итого порядка 2000 объектов/сек, ~100 KB/сек. При размере young gen в
несколько MB minor GC случается раз в десятки секунд — не «пилы», а
плавный прирост.

### 5.3. Если хочешь оптимизировать ещё

- **Header**: кешировать `fpsText` и `posText`, обновлять только при
  изменении значения.
- **Animation**: вычислять `pulse` через `int`-арифметику (без `Math.sin`)
  через таблицу значений или треугольную волну.
- **Setchar/putString**: добавить в `RenderLayer` пул `TextCharacter`-ов
  и переиспользовать. Эффективно, но усложняет код.

В обычной работе (Roguera 4.0) этого не нужно. Текущий уровень
аллокаций — норма для 30 fps Java.

---

## 6. Конфигурация

### 6.1. `game-settings.properties`

Размер окна:

```properties
# Если pixel.* заданы — окно будет этого размера, cols/rows посчитаются
# из метрик шрифта.
window.pixel.width=1600
window.pixel.height=900
# Fallback: если pixel.* пустые, эти значения берутся как cols/rows.
window.size.width=210
window.size.height=40
```

Все остальные debug-флаги остались как раньше — управление логированием
подсистем.

### 6.2. `char-assets.rca`

Формат: `<имя>-<один символ>` per line.

```
# Стены (для map-генератора, MapDrawer)
wall_h-─
wall_v-│
wall_corner_top_l-╭
...
# Двери
door_v-▢
door_h-▣
# UI-рамки (для BorderStyle)
border.rounded.tl-╭
border.rounded.tr-╮
border.rounded.bl-╰
border.rounded.br-╯
border.rounded.h-─
border.rounded.v-│
border.sharp.tl-┌
...
border.heavy...
border.double...
```

Подгружается в `AssetPool.get()` при старте через
`AssetPool.loadAssets("/char-assets.rca")` в `RogueraLauncher.main`.

### 6.3. Layout-константы

В `graphics/ui/layout/Layout.java`:

```java
private static final int HEADER_HEIGHT = 1;
private static final int FOOTER_HEIGHT = 2;
private static final int SIDEBAR_WIDTH = 28;
```

Если хочешь подвинуть — менять здесь. Если захочется в settings —
добавь properties и читай через `SettingsLoader.getSettingValue`.

### 6.4. Цветовая палитра

`graphics/Palette.java`. Все цвета — публичные static final TextColor.RGB.
Если хочешь сменить тему — меняй RGB в этом файле; никуда больше лезть
не надо.

### 6.5. Параметры анимации

В `graphics/ui/AnimationDrawer.java`:

```java
private static final TextColor PLAYER_C1 = Palette.GREEN;
private static final TextColor PLAYER_C2 = lighter(Palette.GREEN, 50);
private static final TextColor DOOR_C1 = Palette.MAUVE;
private static final TextColor DOOR_C2 = Palette.PINK;
private static final long PLAYER_PERIOD_MS = 1500;
private static final long DOOR_PERIOD_MS   = 2000;
```

---

## 7. Куда дальше

### 7.1. Ближайшие технически очевидные расширения

- **Map-generator не вылезает за viewport**. Сейчас в
  `PhaseOneCreateRooms` диапазон случайных позиций задан как
  `rnd.nextInt(84)` и `rnd.nextInt(25)`, а размер комнат — `5..15`.
  Это значит world-карта может быть до 99×40 ячеек (после `*2 = 198`
  cols). Camera эту проблему обрезает, но визуально комнаты могут
  «застревать» по диагонали. Чище — читать диапазоны из
  `dungeon.world.size.*` и опираться на them.
- **HP/MP/XP в `Player`**. Сейчас `PlayerSidebar` показывает только
  имена/координаты/seed. Когда появятся stats — добавь bars через
  `Palette.GREEN/YELLOW/RED` с порогами, символы блоков `█▉▊▋▌▍▎▏`.
- **Ключи footer-а с эффектом нажатия**. При нажатии клавиши на 100мс
  подсветить соответствующий сегмент ярким цветом (через тот же
  механизм anim-таймера, что у игрока).

### 7.2. Архитектурные шаги

- **YAML-тематизация**. У тебя есть `snakeyaml` в зависимостях и
  пустой `src/main/resources/ui/base-block.yml`. Заведи `theme.yml` с
  цветовой палитрой и border-style для каждого Panel-типа, грузи в
  `Theme` singleton, передавай в `UIFrame`/`Panel`. Это и был «вариант
  (а)» — доводим UI-каркас до декларативного описания тем.
- **Dialog/Modal Panel**. `BorderStyle.DOUBLE` уже добавлен — для
  модальных окон (инвентарь, торговец). Сейчас ничего ими не пользуется.
- **Виджеты в content-region**. Сейчас sidebar пишет голые putString.
  При желании можно ввести понятие `Widget` (label, progress-bar,
  list-item) и собирать из них content. Полезно когда контент станет
  богаче.

### 7.3. Что НЕ стоит делать

- Не возвращай `Window.clearScreen()` обратно к `terminal.clear()` без
  fillBackdrop — backdrop пропадёт.
- Не зови `terminal.refresh()` или `mapDrawer.refresh()` посреди
  отрисовки кадра — будет промежуточный flicker.
- Не пиши в одну экранную ячейку с нескольких слоёв подряд (BACKGROUND
  и UI), не подумав — кто записал последним, того и видно. Слои Lanterna
  не складываются.
- Не ставь sidebars обратно в hot loop — heap раздуется.

---

## 8. Если что-то отвалилось

### Симптом → подозреваемый

| симптом                                       | смотреть |
|-----------------------------------------------|----------|
| Чёрный фон под UI                              | `Window.fillBackdrop`, не сбили `clearScreen` |
| Окно слишком широкое / не помещается           | `window.pixel.*` или `window.size.*`, метрики шрифта |
| Header не на месте                             | `layout.getHeader()` и порядок инициализации в `Window` |
| Карта обрезается / не видна                    | `Camera.viewport` и `Camera.isVisible`; что в `centerOn` |
| Карта прыгает на каждом шаге                   | `Camera.follow` deadzone; должно быть в `pollingEvents` |
| Header / sidebars стираются при regen          | `mapDrawer.clear()` должен быть `fill(region, ...)`, не `Window.clearScreen` |
| Мерцание при движении                          | где-то промежуточный `refresh()`; должен быть один в конце |
| Пиковый heap большой                           | sidebar или footer попал в hot loop |
| Не видно символа угла / стрелок                | шрифт не имеет глифа; см. `char-assets.rca` |

### Полезные команды

```bash
mvn -q compile                                  # быстрая проверка компиляции
git log --oneline -- src/main/java/<file>       # история файла
git show <sha>                                  # diff конкретного коммита
git diff dev-4.0..dev-4.0_tui -- <file>         # diff с базовой веткой
```

---

## 9. Cheat-sheet — где какой класс

```
graphics/Palette.java                                 ← цвета Catppuccin Mocha
graphics/render/Window.java                           ← окно, backdrop, resolveGridSize
graphics/render/RenderLayer.java                      ← обёртка TextGraphics (fill/putString/setChar)
graphics/render/TGLayer.java                          ← enum BACKGROUND/FOREGROUND/UI
graphics/sprites/AssetPool.java                       ← .rca-загрузка символов
graphics/sprites/TextSprite.java                      ← (legacy) char + fg + bg

graphics/ui/layout/Region.java                        ← record (x, y, w, h)
graphics/ui/layout/BorderStyle.java                   ← ROUNDED/SHARP/HEAVY/DOUBLE через AssetPool
graphics/ui/layout/Layout.java                        ← раскладка экрана на 5 регионов
graphics/ui/layout/Camera.java                        ← world↔screen, edge-scroll follow

graphics/ui/parts/UIFrame.java                        ← рамка + встроенный заголовок
graphics/ui/Panel.java                                ← abstract base (рамка + content)

graphics/ui/HeaderDrawer.java                         ← однострочный header (FPS/pos)
graphics/ui/MapDrawer.java                            ← рендер карты через Camera
graphics/ui/PlayerSidebar.java                        ← Panel слева (статус игрока)
graphics/ui/EventsSidebar.java                        ← Panel справа (лента событий)
graphics/ui/FooterDrawer.java                         ← двустрочный footer (подсказки клавиш)
graphics/ui/AnimationDrawer.java                      ← pulse игрока + двери

game/GameLoop.java                                    ← склейка всего
game/EventLoop.java                                   ← очередь + лог событий (теперь с @Getter)
```

Когда забудешь, что и где — открой этот раздел, найди компонент,
посмотри секцию выше с подробностями.
