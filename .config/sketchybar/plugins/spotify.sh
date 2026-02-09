#!/bin/sh
export PATH="/opt/homebrew/bin:/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin"

RUNNING=$(osascript -e 'tell application "System Events" to (name of processes) contains "Spotify"' 2>/dev/null)

if [ "$RUNNING" != "true" ]; then
  sketchybar --set "$NAME" drawing=off
  exit 0
fi

STATE=$(osascript -e 'tell application "Spotify" to player state as string' 2>/dev/null)

ARTIST=$(osascript -e 'tell application "Spotify" to artist of current track' 2>/dev/null)
TRACK=$(osascript -e 'tell application "Spotify" to name of current track' 2>/dev/null)

# fallback
[ -z "$TRACK" ] && TRACK="Spotify"

# skróć
MAX=35
TEXT="$ARTIST — $TRACK"
if [ "${#TEXT}" -gt "$MAX" ]; then
  TEXT="${TEXT:0:$MAX}…"
fi

ICON="󰓇"
[ "$STATE" = "paused" ] && ICON="󰏤"

sketchybar --set "$NAME" drawing=on icon="$ICON" label="$TEXT"

