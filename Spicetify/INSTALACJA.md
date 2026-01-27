# ✅ Motyw Hostile dla Spotify został stworzony!

## 📁 Struktura

```
Spicetify/
├── Hostile/                    # Folder motywu
│   ├── color.ini              # Definicje kolorów
│   ├── user.css               # Niestandardowe style CSS
│   └── manifest.json          # Metadata motywu
├── install.sh                 # Skrypt instalacyjny
├── README.md                  # Pełna dokumentacja
├── QUICKSTART.md              # Szybki start
└── preview.png                # Podgląd motywu
```

## 🎨 Schemat Kolorów

Motyw używa tych samych kolorów co Twój motyw Hostile w JetBrains i Burp Suite:

| Element | Kolor | Hex |
|---------|-------|-----|
| Tło główne | Deep Teal | `#042024` |
| Tekst | Teal | `#52b5b7` |
| Akcent główny | Bright Cyan | `#30f3d9` |
| Akcent dodatkowy | Mint Green | `#69ddce` |
| Zaznaczenie | Dark Teal | `#0e3a40` |
| Serca/Błędy | Pink | `#ff5c8a` |
| Powierzchnie | Mid Teal | `#2c666c` |

## 🚀 Instalacja

### Krok 1: Zainstaluj Spicetify

```bash
curl -fsSL https://raw.githubusercontent.com/spicetify/spicetify-cli/master/install.sh | sh
```

### Krok 2: Zainstaluj motyw

**Opcja A - Automatyczna (zalecana):**
```bash
cd ~/Repositories/dotfiles/Spicetify
./install.sh
```

**Opcja B - Manualna:**
```bash
# Skopiuj motyw
cp -r ~/Repositories/dotfiles/Spicetify/Hostile ~/.config/spicetify/Themes/

# Zastosuj motyw
spicetify config current_theme Hostile
spicetify config color_scheme hostile
spicetify apply
```

### Krok 3: Zrestartuj Spotify

Gotowe! 🎉

## ✨ Funkcje Motywu

- ✅ **Spójny design** - Te same kolory co w JetBrains i Burp
- ✅ **Zaokrąglone rogi** - 7px radius (jak w innych motywach)
- ✅ **Płynne animacje** - Smooth transitions na wszystkich elementach
- ✅ **Efekty świetlne** - Glow na przyciskach i interaktywnych elementach
- ✅ **Niestandardowe scrollbary** - Dopasowane do kolorystyki
- ✅ **Enhanced hover states** - Lepsze wizualne feedback
- ✅ **Cienie na kartach** - Subtlne efekty głębi
- ✅ **Styled progress bars** - Cyan progress bar
- ✅ **Custom focus states** - Accessibility friendly
- ✅ **Brak szarych tła** - Wszystkie szare elementy zastąpione kolorami teal/cyan

## 🔧 Personalizacja

### Zmiana kolorów
Edytuj plik:
```
~/.config/spicetify/Themes/Hostile/color.ini
```

Następnie:
```bash
spicetify apply
```

### Zmiana stylów
Edytuj plik:
```
~/.config/spicetify/Themes/Hostile/user.css
```

Zmienne CSS do dostosowania:
- `--border-radius`: Zaokrąglenie rogów (domyślnie 7px)
- `--transition-duration`: Szybkość animacji (domyślnie 0.2s)
- `--overlay-opacity`: Przezroczystość nakładek (domyślnie 0.95)

## 📖 Przydatne Komendy

```bash
# Ponowne zastosowanie motywu (po zmianach)
spicetify apply

# Przywrócenie domyślnego wyglądu Spotify
spicetify restore

# Backup obecnej konfiguracji
spicetify backup

# Update Spicetify
spicetify update

# Wymuszenie pełnego update
spicetify backup
spicetify restore
spicetify apply
```

## 🐛 Troubleshooting

**Motyw się nie pojawia?**
```bash
spicetify backup apply
```

**Spotify się zaktualizował i motyw przestał działać?**
```bash
spicetify restore backup apply
```

**Kolory wyglądają źle?**
```bash
spicetify config color_scheme hostile
spicetify apply
```

## 📝 Notatki

- Motyw został przetestowany z najnowszą wersją Spicetify
- Kompatybilny z Spotify Desktop (macOS, Windows, Linux)
- Nie działa z Spotify Web Player (ograniczenie Spicetify)
- Może wymagać ponownego zastosowania po aktualizacjach Spotify

## 🌐 Dodatkowe Zasoby

- [Spicetify Documentation](https://spicetify.app/docs/getting-started)
- [Spicetify Themes Repository](https://github.com/spicetify/spicetify-themes)
- [Spicetify Discord](https://discord.gg/VnevqPp2Rr)

---

**Autor:** Igor Wojciechowski  
**Wersja:** 1.0.0  
**Licencja:** MIT  
**Data utworzenia:** 2026-01-27

Miłego słuchania z nowym motywem! 🎵✨
