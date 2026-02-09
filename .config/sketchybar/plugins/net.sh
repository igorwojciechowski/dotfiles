#!/bin/sh
export PATH="/opt/homebrew/bin:/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin"

# interfejs z default route
IFACE=$(route -n get default 2>/dev/null | awk '/interface:/{print $2; exit}')
if [ -z "$IFACE" ]; then
  # Fallback: active interface from scutil (works when route output is unavailable)
  IFACE=$(scutil --nwi 2>/dev/null | awk '/InterfaceName/ {print $3; exit}')
fi
[ -z "$IFACE" ] && IFACE="en0"

STATE="/tmp/sketchybar_net_${IFACE}"

# bierzemy Ibytes/Obytes z netstat dla interfejsu (wiersz link-layer)
LINE=$(
  netstat -ibn -I "$IFACE" 2>/dev/null \
    | awk 'NR>1 && tolower($3) ~ /link/ {print; exit}'
)
IB=$(echo "$LINE" | awk '{print $7}')
OB=$(echo "$LINE" | awk '{print $10}')

# Jak netstat nic nie zwrócił, nie aktualizuj state (unikamy liczenia na pustych danych).
if [ -z "$IB" ] || [ -z "$OB" ]; then
  sketchybar --set "$NAME" label="…"
  exit 0
fi

# Upewnij sie, ze liczymy na liczbach.
case "${IB}${OB}" in
  (*[!0-9]*) sketchybar --set "$NAME" label="…"; exit 0 ;;
esac

now=$(date +%s)

if [ -f "$STATE" ]; then
  read last_t last_ib last_ob < "$STATE"
  dt=$((now - last_t))
  if [ "$dt" -le 0 ]; then dt=1; fi

  din=$((IB - last_ib))
  dout=$((OB - last_ob))

  # bytes/s
  inps=$((din / dt))
  outps=$((dout / dt))

  fmt() {
    v=$1
    if [ "$v" -ge 1048576 ]; then
      awk "BEGIN{printf \"%.1fMB/s\", $v/1048576}"
    elif [ "$v" -ge 1024 ]; then
      awk "BEGIN{printf \"%.0fKB/s\", $v/1024}"
    else
      printf "%dB/s" "$v"
    fi
  }

  DOWN=$(fmt "$inps")
  UP=$(fmt "$outps")

  sketchybar --set "$NAME" label="↓ $DOWN  ↑ $UP"
else
  sketchybar --set "$NAME" label="…"
fi

echo "$now $IB $OB" > "$STATE"
