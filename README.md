# themer - My Color Set

## Vim

Copy or symlink `Vim/ThemerMyColorSet.vim` to `~/.vim/colors/`.

Then set the colorscheme in `.vimrc`:

```
" The background option must be set before running this command.
colorscheme ThemerMyColorSet
```

## Vim lightline

1. Make sure that the `background` option is set in `.vimrc`.
2. Copy or symlink `Vim lightline/ThemerMyColorSetLightline.vim` to `~/.vim/autoload/lightline/colorscheme/`.
3. Set the colorscheme in `.vimrc`: `let g:lightline = { 'colorscheme': 'ThemerMyColorSet' }`
4. Restart Vim.

## Visual Studio

1. Select Tools > Import and Export Settings...
2. Choose the "Import selected environment settings" option
3. Choose whether or not to save a backup of current settings
4. Click "Browse..." and choose the generated theme file ('Visual Studio/themer-my-color-set-dark.vssettings')
5. Click "Finish"

## VS Code

Copy (or symlink) the generated package directory into the VS Code extensions directory:

```
cp -R 'VS Code/themer-my-color-set' ~/.vscode/extensions/
```

Then reload or restart VS Code. The generated theme package should be in the list of installed extensions, and "Themer My Color Set Dark" will be available in the list of themes.

## Alacritty

1. Paste the contents of `Alacritty/Themer My Color Set.yml` into your Alacritty config file.
2. Select the desired theme by setting the `colors` config key to reference the scheme's anchor (i.e., `colors: *light` or `colors: *dark`).

## Hyper

First, copy (or symlink) the outputted package directory to the Hyper local plugins directory:

```
cp -R 'Hyper/hyper-themer-my-color-set-dark' ~/.hyper_plugins/local/
```

Then edit `~/.hyper.js` and ad the package to the `localPlugins` array:

```
...
localPlugins: [
  'hyper-themer-my-color-set-dark'
],
...
```

## Windows Terminal

1. Open the Windows Terminal settings (`Ctrl`-`,`)
2. Add the contents of 'Windows Terminal/themer-my-color-set-dark.json' to the `schemes` array in `profile.json`
3. Set the `colorScheme` property to the desired scheme name ("Themer My Color Set Dark") in the profiles section of `profile.json`, e.g.:

```
"profiles": {
  "defaults": {
    "colorScheme": "Themer My Color Set Dark"
  }
}
```

## Brave

1. Launch Brave and go to `brave://extensions`.
2. Check the "Developer mode" checkbox at the top.
3. Click the "Load unpacked extension..." button and choose the desired theme directory (`Brave/Themer My Color Set Dark`).

(To reset or remove the theme, visit `brave://settings` and click "Reset to Default" in the "Appearance" section.)

## Chrome

1. Launch Chrome and go to `chrome://extensions`.
2. Check the "Developer mode" checkbox at the top.
3. Click the "Load unpacked extension..." button and choose the desired theme directory (`Chrome/Themer My Color Set Dark`).

(To reset or remove the theme, visit `chrome://settings` and click "Reset to Default" in the "Appearance" section.)

## Firefox Add-on

To use the generated extension package, the code will need to be packaged up and signed by Mozilla.

To package the code in preparation for submission, the `web-ext` tool can be used:

```
npx web-ext build --source-dir 'Firefox Add-on/Themer My Color Set Dark'
```

Then the package can be submitted to Mozilla for review in the [Add-on Developer Hub](https://addons.mozilla.org/en-US/developers/addon/submit/distribution).

Learn more about Firefox themes from [extensionworkshop.com](https://extensionworkshop.com/documentation/themes/)

To theme Firefox without the need to create a developer account and go through the extension review process, see themer's integration with [Firefox Color](https://color.firefox.com).

## Wox

1. Copy 'Wox/Themer My Color Set Dark.xaml' into Wox's theme directory (for example, `C:\Users\<username>\AppData\Local\Wox\app-<version>\Themes`).
2. Open Wox and type "settings" to launch Wox settings.
3. On the "Themes" tab, select the generated theme from the list.