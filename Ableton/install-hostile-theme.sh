#!/usr/bin/env bash
set -euo pipefail

app="${ABLETON_APP:-/Applications/Ableton Live 12 Standard.app}"
theme_dir="$app/Contents/App-Resources/Themes"
script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source_theme="$script_dir/Hostile.ask"
target_theme="$theme_dir/Hostile.ask"

if [[ ! -f "$source_theme" ]]; then
  echo "Missing source theme: $source_theme" >&2
  exit 1
fi

if [[ ! -d "$theme_dir" ]]; then
  echo "Missing Ableton themes directory: $theme_dir" >&2
  exit 1
fi

rsync --inplace "$source_theme" "$target_theme"

if command -v xmllint >/dev/null 2>&1; then
  xmllint --noout "$target_theme"
fi

echo "Installed Hostile theme to: $target_theme"
