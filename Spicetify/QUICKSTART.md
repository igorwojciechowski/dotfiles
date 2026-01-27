# Hostile Theme for Spotify - Quick Start Guide

## 🚀 Quick Installation

### Option 1: Automatic (Recommended)

```bash
cd /Users/igor/Repositories/dotfiles/Spicetify
./install.sh
```

### Option 2: Manual

```bash
# 1. Install Spicetify (if not installed)
curl -fsSL https://raw.githubusercontent.com/spicetify/spicetify-cli/master/install.sh | sh

# 2. Copy theme
cp -r Spicetify/Hostile ~/.config/spicetify/Themes/

# 3. Apply theme
spicetify config current_theme Hostile
spicetify config color_scheme hostile
spicetify apply
```

## 🎨 What's Included

- **color.ini** - Color definitions matching your Hostile theme
- **user.css** - Custom styles with modern effects
- **README.md** - Full documentation
- **install.sh** - Automated installer

## 🔥 Features

- Teal/Cyan color scheme matching JetBrains & Burp themes
- Smooth animations and transitions
- Glow effects on interactive elements
- Custom scrollbars
- Rounded corners (7px)
- Enhanced hover states

## ⚡ Usage

After installation, just open Spotify and the theme will be active!

To modify colors, edit:
```
~/.config/spicetify/Themes/Hostile/color.ini
```

Then run:
```bash
spicetify apply
```

## 🔧 Troubleshooting

**Theme not showing?**
```bash
spicetify backup apply
```

**Spotify updated?**
```bash
spicetify restore backup apply
```

**Remove theme:**
```bash
spicetify restore
```

## 📝 Color Reference

| Element | Color | Hex |
|---------|-------|-----|
| Background | Deep Teal | `#042024` |
| Text | Teal | `#52b5b7` |
| Accent | Bright Cyan | `#30f3d9` |
| Secondary | Mint Green | `#69ddce` |
| Selection | Dark Teal | `#0e3a40` |
| Like/Error | Pink | `#ff5c8a` |
| Surface | Mid Teal | `#2c666c` |

Enjoy your themed Spotify! 🎵
