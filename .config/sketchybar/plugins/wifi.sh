#!/bin/sh
export PATH="/opt/homebrew/bin:/usr/local/bin:/usr/sbin:/usr/bin:/bin:/usr/sbin:/sbin"

# Detect Wi-Fi iface
IFACE=$(/usr/sbin/networksetup -listallhardwareports \
  | awk '/Wi-Fi|AirPort/{getline; print $2; exit}')
[ -z "$IFACE" ] && IFACE="en0"

# Power state (still useful)
PWR=$(/usr/sbin/networksetup -getairportpower "$IFACE" 2>/dev/null | awk '{print $NF}')
if [ "$PWR" = "Off" ] || [ "$PWR" = "off" ]; then
  sketchybar --set "$NAME" icon="󰖪" label="off"
  exit 0
fi

SSID=""

# 1) BEST: ioreg (CoreWLAN) -> usually returns real SSID even when networksetup lies
SSID=$(ioreg -r -n AirPort_BrcmNIC -k SSID_STR 2>/dev/null \
  | awk -F'="|"' '/SSID_STR/ {print $2; exit}')

# Some Macs use different node name
if [ -z "$SSID" ]; then
  SSID=$(ioreg -r -n IO80211Interface -k SSID_STR 2>/dev/null \
    | awk -F'="|"' '/SSID_STR/ {print $2; exit}')
fi

# 2) Fallback: wdutil (might be <redacted>)
if [ -z "$SSID" ]; then
  OUT=$(sudo -n "$HOME/.config/sketchybar/plugins/wdutil_wifi.sh" 2>/dev/null)
  SSID=$(printf "%s\n" "$OUT" | awk -F': ' '/SSID/ && $2 != "" {print $2; exit}')
  case "$SSID" in
    ""|*"<redacted>"*) SSID="";;
  esac
fi

# 3) Last fallback: networksetup (often wrong for you, but keep it)
if [ -z "$SSID" ]; then
  NS_OUT=$(/usr/sbin/networksetup -getairportnetwork "$IFACE" 2>/dev/null)
  SSID=$(printf "%s\n" "$NS_OUT" | sed -E 's/^.*: //')
  case "$SSID" in
    ""|*"not associated"*|*"Not associated"*|*"Nie jesteś"*|*"nie jesteś"*) SSID="";;
  esac
fi

# Output
if [ -n "$SSID" ]; then
  MAX=18
  if [ "${#SSID}" -gt "$MAX" ]; then SSID="${SSID:0:$MAX}…"; fi
  sketchybar --set "$NAME" icon="󰖩" label="$SSID"
else
  sketchybar --set "$NAME" icon="󰖩" label="on"
fi
