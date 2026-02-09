#!/bin/sh
export PATH="/opt/homebrew/bin:/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin"

# top daje stabilny odczyt w % (user+sys)
# Format macOS: "CPU usage: 7.56% user, 15.09% sys, 77.34% idle"
# Po split na '[:, ]+' liczby są w $3 (user) i $5 (sys). Poprzednio było $4+$6 ("user"+"sys") => 0.
CPU=$(
  LC_ALL=C top -l 1 -n 0 2>/dev/null \
    | awk -F'[:, ]+' '/CPU usage/ {print $3+$5; exit}'
)

# CPU bywa np. 7.53 -> zaokrąglij; jak parsowanie nie wyszło, pokaż 0
CPU_INT=$(printf "%.0f" "${CPU:-0}" 2>/dev/null)

sketchybar --set "$NAME" label="${CPU_INT}%"
