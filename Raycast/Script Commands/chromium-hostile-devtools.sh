#!/bin/bash

# Required parameters:
# @raycast.schemaVersion 1
# @raycast.title Chromium Hostile DevTools
# @raycast.mode compact

# Optional parameters:
# @raycast.icon /Applications/Chromium.app/Contents/Resources/app.icns
# @raycast.packageName Browser
# @raycast.description Launch Chromium with the Hostile DevTools theme enabled.

set -euo pipefail

launcher="/Users/igor/Repositories/dotfiles/Chrome/launch-hostile-devtools.sh"

if "$launcher"; then
  echo "Chromium launched with Hostile DevTools."
else
  echo "Quit Chromium fully, then run this command again."
  exit 1
fi
