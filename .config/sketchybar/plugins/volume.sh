#!/bin/sh
VOL=$(osascript -e 'output volume of (get volume settings)' 2>/dev/null)
MUTED=$(osascript -e 'output muted of (get volume settings)' 2>/dev/null)

if [ "$MUTED" = "true" ]; then
  sketchybar --set "$NAME" icon="󰝟" label="mute"
else
  sketchybar --set "$NAME" icon="󰕾" label="${VOL}%"
fi

