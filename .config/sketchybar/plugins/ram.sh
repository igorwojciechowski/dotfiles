#!/bin/sh
export PATH="/opt/homebrew/bin:/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin"

PAGE=$(vm_stat | awk '/page size of/{gsub("\\.","",$8); print $8}')
FREE=$(vm_stat | awk '/Pages free/{gsub("\\.","",$3); print $3}')
INACT=$(vm_stat | awk '/Pages inactive/{gsub("\\.","",$3); print $3}')
SPEC=$(vm_stat | awk '/Pages speculative/{gsub("\\.","",$3); print $3}')

# Przybliżenie: wolne = free+inactive+spec
FREEP=$((FREE + INACT + SPEC))
FREEB=$((FREEP * PAGE))

TOTALB=$(sysctl -n hw.memsize)

USEB=$((TOTALB - FREEB))

# GB z 1 miejscem
USED_GB=$(awk "BEGIN{printf \"%.1f\", $USEB/1073741824}")
TOTAL_GB=$(awk "BEGIN{printf \"%.0f\", $TOTALB/1073741824}")

sketchybar --set "$NAME" label="${USED_GB}/${TOTAL_GB}G"

