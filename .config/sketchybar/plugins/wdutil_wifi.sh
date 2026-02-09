#!/bin/sh
export PATH="/usr/sbin:/usr/bin:/bin:/sbin"

WDUTIL="/usr/bin/wdutil"

OUT="$($WDUTIL info 2>/dev/null || true)"
[ -z "$OUT" ] && OUT="$($WDUTIL status 2>/dev/null || true)"

printf "%s\n" "$OUT"
