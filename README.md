# JStickyGo

Порт JSticky с JavaFX на Go и Wails 3.

В приложении используются отдельные безрамочные окна для заметки, настроек и захвата экрана. Иконки панели перенесены из оригинального JavaFX-проекта.

## Разработка

```powershell
wails3 dev
```

Если `wails3` отсутствует в `PATH`:

```powershell
go install github.com/wailsapp/wails/v3/cmd/wails3@v3.0.0-alpha2.119
```

## Сборка

```powershell
wails3 build
```

Исполняемый файл будет создан в `bin/JStickyGo.exe`.

Настройки сохраняются в `%AppData%/JStickyGo/jSticky.json`. При первом запуске поддерживается импорт `../JSticky/jSticky.ini`.
