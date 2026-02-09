#!/bin/sh
export PATH="/opt/homebrew/bin:/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin"

APP=$(osascript -e 'tell application "System Events" to get name of first application process whose frontmost is true' 2>/dev/null)

TITLE=$(osascript -e 'tell application "System Events" to tell (first process whose frontmost is true) to get value of attribute "AXTitle" of front window' 2>/dev/null)

trim() {
  s="$1"
  # Keep this conservative so it always fits in the 300px center capsule.
  # Can be overridden via env var FRONT_APP_MAX_CHARS.
  max="${FRONT_APP_MAX_CHARS:-34}"
  if [ "${#s}" -gt "$max" ]; then
    printf "%s…" "${s:0:$max}"
  else
    printf "%s" "$s"
  fi
}

if [ -n "$TITLE" ] && [ "$TITLE" != "missing value" ]; then
  sketchybar --set "$NAME" label="$(trim "$APP • $TITLE")"
else
  sketchybar --set "$NAME" label="$(trim "$APP")"
fi
