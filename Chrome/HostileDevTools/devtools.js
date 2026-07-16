(async () => {
  const applyHostileTheme = async () => {
    if (!chrome.devtools.panels.applyStyleSheet) {
      console.warn(
        'Hostile DevTools needs the DevTools experiment ' +
          '"Allow extensions to load custom stylesheets" enabled.'
      );
      return;
    }

    const response = await fetch(chrome.runtime.getURL('hostile-devtools.css'));
    const css = await response.text();
    chrome.devtools.panels.applyStyleSheet(css);
  };

  await applyHostileTheme();

  if (chrome.devtools.panels.setThemeChangeHandler) {
    chrome.devtools.panels.setThemeChangeHandler(() => {
      void applyHostileTheme();
    });
  }
})();
