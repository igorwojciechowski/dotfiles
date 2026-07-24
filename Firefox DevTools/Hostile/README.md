# Firefox DevTools Hostile

Custom Firefox DevTools CSS theme matched to the Hostile teal Firefox profile theme.

## Install

Copy this directory's `chrome` folder and `user.js` into the Firefox profile directory, then fully restart Firefox.

Profile directory examples on this machine:

```text
/Users/igor/Library/Application Support/Firefox/Profiles/bdxzy4f8.default-release
/Users/igor/Library/Application Support/Firefox/Profiles/piUlJqVJ.Profile 1
```

The `user.js` file enables:

```text
toolkit.legacyUserProfileCustomizations.stylesheets = true
devtools.theme = dark
```

To remove the theme, delete the copied `chrome` directory and `user.js` entries, or set `toolkit.legacyUserProfileCustomizations.stylesheets` back to `false`.

## Palette

- Main background: `#0f4243`
- Toolbar background: `#06292c`, `#0b383a`
- Borders: `#155158`, `#1b6368`
- Text: `#d9fbfb`, `#88cdd1`
- Accent: `#4bd7df`
- Secondary accent: `#cdc6ff`
