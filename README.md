# Carpet Shadow Legacy

## ENG

Carpet Addon for bringing back Item Shadowing in modern Minecraft versions.

Originally, Carpet-Shadow was developed by [MattyMatty97](https://github.com/mattymatty97), but his project is abandoned now.

**For Minecraft versions below 1.21 please use original [Carpet-Shadow](https://github.com/mattymatty97/carpet-shadow). There is no support for original Carpet-Shadow.**

## RU

Carpet-Аддон для возвращаения механики Линковки Предметов в новые версии Майнкрафта.

Изначально, Carpet-Shadow был разработан [MattyMatty97](https://github.com/mattymatty97), но сейчас его проект заброшен.

**Для версий Майнкрафта ниже 1.21, пожалуйста, используйте оригинальный [Carpet-Shadow](https://github.com/mattymatty97/carpet-shadow). Я не оказываю помощь с Carpet-Shadow.**

# Settings/Параметры

## ENG

Settings are set using the /carpet command.

- **shadowItemMode** 
  
  The behavior of Shadow Items after a restart/reload from disk.

  - PERSIST (Shadow Items will be relinked)
  - UNLINK (Shadow Items will be duplicated)
  - VANISH (Shadow Items will disappear)

- **shadowItemTooltip**

  Showing unique identifier of Shadow Item when hovering it with a mouse.

- **shadowItemIdSize**

  Changes the displayed length of unique Shadow Item identifier.

  *Works only if **shadowItemTooltip** activated.*

- **shadowItemFragilityFixes**

  Different changes that fix problems with Shadow Items unlink/dupe.

- **shadowItemUpdateFix**

  Shadow Item update will produce another stacks update, even if they are in other inventories.

- **shadowItemPreventCombine**

  Preventing combinate of different Shadow Items.

  *Works only if **shadowItemFragilityFixes** activated.*

- **shadowItemUseFix**

  Preventing different problems with use of the Shadow Items.

- **shadowCraftingGeneration**

  Adds craft of Shadow Items in crafting grid with ender-chest.

- **shadowSuppressionGeneration**

  Reintroduces the ability to obtain Shadow Items using various suppressors, including the update suppressor, update skipper, CCE suppressor, and sound suppressor.

## RU

Настройки устанавливаются с помощью команды /carpet.

- **shadowItemMode**

  Поведение Шедоу Айтемов после перезапуска/перезагруски с диска.

  - PERSIST (Шедоу Айтемы будут перелинкованы)
  - UNLINK (Шедоу Айтемы будут дюпнуты)
  - VANISH (Шедоу Айтемы пропадут)

- **shadowItemTooltip**

  Отображение уникального идентификатора Шедоу Айтема при наведении мыши на стак.

- **shadowItemIdSize**

  Изменяет отображаемую длину уникального идентификатора Шедоу Айтема.

  *Работает только если активен **shadowItemTooltip**.*

- **shadowItemFragilityFixes**

  Различные исправления, предотвращающие дюпы/разлинковки Шедоу Айтема.

- **shadowItemUpdateFix**

  Обновление Шедоу Айтемов будет также обновлять другие стаки, даже если они находятся в других инвенатрях.

- **shadowItemPreventCombine**

  Предотвращает возможные совмещения Шедоу Айтемов между собой.

  *Работает только если активен **shadowItemFragilityFixes**.*

- **shadowItemUseFix**

  Предотвращает различные ошибки, связанные с использованием Шедоу Айтемов.

- **shadowCraftingGeneration**

  Добавляет возможность создания Шедоу Айтемов внутри сетки крафта с помощью эндер-сундуков.

- **shadowSuppressionGeneration**

  Возвращает возможность получения Шедоу Айтемов с использованием саппрессоров, включая апдейт саппрессор, апдейт скиппер, CCE саппрессор и саунд саппрессор.

# Known Bugs/Известные Ошибки

## ENG
If you are encountering any bugs, feel free to open an [issue](https://github.com/Ksandr707-yt/Carpet-Shadow-Legacy/issues).

- Actions with Shadow Items in creative causing unlink.
- The unique identifier of Shadow Item is not displayed if the mod is not installed on the client.

## RU
Если вы нашли ошибку, не стесняйтесь писать в [issue](https://github.com/Ksandr707-yt/Carpet-Shadow-Legacy/issues) проекта (можно на русском).

- Действия с Шедоу Айтемами в креативе приводят к разлинковке.
- Уникальный идентификатор Шедоу Айтема не отображается, если мод не установлен на клиент.
