#!/bin/sh
export PATH="/opt/homebrew/bin:/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin"

osascript -e 'display dialog "Wylogowac sie?" buttons {"Anuluj","Wyloguj"} default button "Wyloguj" with icon caution' >/dev/null 2>&1 || exit 0
osascript -e 'tell application "System Events" to log out'

