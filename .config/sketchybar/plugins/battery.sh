#!/bin/sh
# pmset output: "InternalBattery-0 (id=...) 89%; discharging; ..."
BATT=$(pmset -g batt | tail -n 1)
PCT=$(echo "$BATT" | grep -oE '[0-9]+%' | head -n 1 | tr -d '%')
STATE=$(echo "$BATT" | awk -F'; ' '{print $2}' | tr -d ';')

# Icons
ICON="󰁹"
if echo "$STATE" | grep -qi "charging"; then ICON="󰂄"; fi
if [ -n "$PCT" ]; then
  if [ "$PCT" -le 15 ]; then ICON="󰂃"; fi
  if [ "$PCT" -ge 95 ] && echo "$STATE" | grep -qi "charged"; then ICON="󰁹"; fi
fi

sketchybar --set "$NAME" icon="$ICON" label="${PCT}%"

