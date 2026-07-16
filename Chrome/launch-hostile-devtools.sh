#!/usr/bin/env bash
set -euo pipefail

script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
chromium="/Applications/Chromium.app/Contents/MacOS/Chromium"
theme_extension="$script_dir/Hostile"
devtools_extension="$script_dir/HostileDevTools"

if pgrep -x Chromium >/dev/null 2>&1; then
  echo "Chromium is already running. Quit it fully, then launch this wrapper again."
  exit 1
fi

nohup "$chromium" \
  --profile-directory=Default \
  --devtools-flags=enabledExperiments=apply-custom-stylesheet \
  --load-extension="$theme_extension,$devtools_extension" \
  "$@" >/tmp/chromium-hostile-devtools.log 2>&1 &
