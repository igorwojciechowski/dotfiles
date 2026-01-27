#!/bin/bash

# Hostile Theme Installer for Spicetify
# This script automatically installs the Hostile theme for Spotify

set -e

# Colors for output
CYAN='\033[0;36m'
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${CYAN}"
echo "╔═══════════════════════════════════════════╗"
echo "║   Hostile Theme Installer for Spicetify  ║"
echo "╚═══════════════════════════════════════════╝"
echo -e "${NC}"

# Check if Spicetify is installed
if ! command -v spicetify &> /dev/null; then
    echo -e "${RED}Error: Spicetify is not installed!${NC}"
    echo -e "${YELLOW}Install it from: https://spicetify.app/${NC}"
    echo ""
    echo "Install command:"
    echo "  curl -fsSL https://raw.githubusercontent.com/spicetify/spicetify-cli/master/install.sh | sh"
    exit 1
fi

echo -e "${GREEN}✓ Spicetify found${NC}"

# Get Spicetify config directory
SPICETIFY_CONFIG=$(spicetify -c)
THEMES_DIR=$(dirname "$SPICETIFY_CONFIG")/Themes

echo -e "${CYAN}→ Spicetify config: $SPICETIFY_CONFIG${NC}"
echo -e "${CYAN}→ Themes directory: $THEMES_DIR${NC}"

# Create themes directory if it doesn't exist
mkdir -p "$THEMES_DIR"

# Get the directory where this script is located
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
THEME_SOURCE="$SCRIPT_DIR/Hostile"

# Check if theme source exists
if [ ! -d "$THEME_SOURCE" ]; then
    echo -e "${RED}Error: Theme source directory not found at $THEME_SOURCE${NC}"
    exit 1
fi

# Copy theme to Spicetify themes directory
echo -e "${CYAN}→ Installing Hostile theme...${NC}"
cp -r "$THEME_SOURCE" "$THEMES_DIR/"

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Theme files copied successfully${NC}"
else
    echo -e "${RED}Error: Failed to copy theme files${NC}"
    exit 1
fi

# Apply the theme
echo -e "${CYAN}→ Applying Hostile theme...${NC}"

# Backup current Spotify
spicetify backup apply &> /dev/null || true

# Set the theme
spicetify config current_theme Hostile
spicetify config color_scheme hostile

# Apply changes
spicetify apply

if [ $? -eq 0 ]; then
    echo ""
    echo -e "${GREEN}╔═══════════════════════════════════════════╗${NC}"
    echo -e "${GREEN}║   ✓ Hostile theme installed successfully! ║${NC}"
    echo -e "${GREEN}╚═══════════════════════════════════════════╝${NC}"
    echo ""
    echo -e "${CYAN}The theme has been applied to Spotify.${NC}"
    echo -e "${CYAN}Restart Spotify if it's currently running.${NC}"
    echo ""
    echo -e "${YELLOW}Useful commands:${NC}"
    echo -e "  ${CYAN}spicetify apply${NC}         - Reapply theme after changes"
    echo -e "  ${CYAN}spicetify restore${NC}       - Remove theme and restore default"
    echo -e "  ${CYAN}spicetify update${NC}        - Update Spicetify"
    echo ""
else
    echo -e "${RED}Error: Failed to apply theme${NC}"
    echo -e "${YELLOW}Try running manually:${NC}"
    echo "  spicetify config current_theme Hostile"
    echo "  spicetify config color_scheme hostile"
    echo "  spicetify apply"
    exit 1
fi
