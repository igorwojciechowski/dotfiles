# themer - theme installation instructions

## windows-terminal

1. Open the Windows Terminal settings (`Ctrl`-`,`)
2. Add the contents of 'windows-terminal/themer-dark.json' to the `schemes` array in `profile.json`
3. Set the `colorScheme` property to the desired scheme name ("Themer Dark") in the profiles section of `profile.json`, e.g.:

    "profiles": {
      "defaults": {
        "colorScheme": "Themer Dark"
      }
    }