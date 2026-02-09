#!/bin/sh
export PATH="/opt/homebrew/bin:/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin"

osascript -e 'display dialog "Wylaczyc komputer?" buttons {"Anuluj","Wylacz"} default button "Wylacz" with icon caution' >/dev/null 2>&1 || exit 0
osascript -e 'tell application "System Events" to shut down'

