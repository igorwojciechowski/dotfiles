package com.burp.extension;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.ContainerEvent;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ColorExtension implements BurpExtension {
    private MontoyaApi api;
    private Map<String, Color> colorPalette = new HashMap<>();
    private boolean componentHookInstalled = false;

    @Override
    public void initialize(MontoyaApi api) {
        this.api = api;
        api.extension().setName("Burp Color Extension");

        try {
            loadAndApplyTheme();
            api.logging().logToOutput("Hostile Theme applied successfully");
        } catch (Exception e) {
            api.logging().logToError("Failed to apply theme: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadAndApplyTheme() {
        try (Reader reader = new InputStreamReader(
                Objects.requireNonNull(getClass().getResourceAsStream("/theme.json")))) {
            Gson gson = new Gson();
            JsonObject themeObj = gson.fromJson(reader, JsonObject.class);

            // 1. Parse Colors
            JsonObject colorsObj = themeObj.getAsJsonObject("colors");
            for (Map.Entry<String, JsonElement> entry : colorsObj.entrySet()) {
                colorPalette.put(entry.getKey(), parseColor(entry.getValue().getAsString()));
            }

            // 2. Parse and Apply UI Defaults
            JsonObject uiObj = themeObj.getAsJsonObject("ui");

            if (uiObj.has("*") && uiObj.getAsJsonObject("*").has("background")) {
                Object bg = resolveValue(uiObj.getAsJsonObject("*").get("background").getAsString());
                Object borderColor = null;
                // Try to get border color or derived from bg
                if (colorPalette.containsKey("borderColor")) {
                    borderColor = colorPalette.get("borderColor");
                }

                if (bg instanceof Color) {
                    Color fg = Color.WHITE; // Default fallback
                    if (uiObj.getAsJsonObject("*").has("foreground")) {
                        Object fgObj = resolveValue(uiObj.getAsJsonObject("*").get("foreground").getAsString());
                        if (fgObj instanceof Color) {
                            fg = (Color) fgObj;
                        }
                    } else if (colorPalette.containsKey("primaryForeground")) {
                        fg = colorPalette.get("primaryForeground");
                    }

                    bruteForceOverwrites((Color) bg,
                            (borderColor instanceof Color) ? (Color) borderColor : ((Color) bg).darker(),
                            fg);
                }
            }

            SwingUtilities.invokeLater(() -> {
                applyUiDefaults(uiObj);
                applyModernLineAccents();
                applySyntaxColors();
                applyEditorTypography();
                applyButtonTextColors();
                normalizeButtonBackgrounds();
                applyBadgeColors();
                applySelectionColors();
                applyScrollBarColors();
                applyContextMenuColors();
                installDynamicButtonHook();
                forceRefresh();
            });

        } catch (Exception e) {
            throw new RuntimeException("Error loading theme", e);
        }
    }

    private void bruteForceOverwrites(Color bg, Color borderColor, Color fg) {
        // Flatten 3D effects - map to border color for visibility
        UIManager.put("controlHighlight", borderColor);
        UIManager.put("controlLtHighlight", borderColor);
        UIManager.put("controlShadow", borderColor);
        UIManager.put("controlDkShadow", borderColor);
        UIManager.put("scrollbar", bg);

        // Iterate over all defaults
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object keyObj = keys.nextElement();
            if (!(keyObj instanceof String))
                continue;

            String key = (String) keyObj;
            Object value = UIManager.get(key);

            String lowerKey = key.toLowerCase();

            // FORCE EXPLICIT OVERWRITES FOR EDITORS (Remove Cyan/Accent)
            // We ignore checking the current value - we just enforce our theme
            if (lowerKey.contains("editor") || lowerKey.contains("table") || lowerKey.contains("tree")
                    || lowerKey.contains("list") || lowerKey.contains("textarea") || lowerKey.contains("textfield")) {
                if (lowerKey.contains("background") && !lowerKey.contains("selection")) {
                    UIManager.put(key, bg); // Primary Background
                    continue;
                }
            }

            if (value instanceof Color) {
                Color c = (Color) value;
                boolean isGray = isGray(c);
                boolean isEditorRelated = lowerKey.contains("editor")
                        || lowerKey.contains("texteditor")
                        || lowerKey.contains("request")
                        || lowerKey.contains("response")
                        || lowerKey.contains("http")
                        || lowerKey.contains("table")
                        || lowerKey.contains("tree")
                        || lowerKey.contains("list")
                        || lowerKey.contains("textarea")
                        || lowerKey.contains("textfield")
                        || lowerKey.contains("textpane")
                        || lowerKey.contains("editorpane");

                // FORCE HIGH CONTRAST TEXT
                if (lowerKey.contains("text") || lowerKey.contains("foreground") || lowerKey.contains("label")
                        || lowerKey.contains("font")) {
                    // Editor/HTTP keys should always match the theme foreground even if defaults are already bright.
                    if (isEditorRelated || calculateLuma(c) < 160) {
                        UIManager.put(key, fg);
                    }
                    continue;
                }

                if (isGray) {
                    // BORDERS / SEPARATORS -> borderColor
                    if (lowerKey.contains("border") || lowerKey.contains("separator") || lowerKey.contains("split")
                            || lowerKey.contains("line") || lowerKey.contains("shadow")) {
                        UIManager.put(key, borderColor);
                        continue;
                    }

                    // BACKGROUNDS / PANELS -> Map to Background if currently gray
                    if (lowerKey.contains("background") || lowerKey.contains("panel") || lowerKey.contains("viewport")
                            || lowerKey.contains("control") || lowerKey.contains("scroll")) {
                        if (calculateLuma(c) > 40 && calculateLuma(c) < 240) {
                            UIManager.put(key, bg);
                        }
                    }
                }
            }
        }
    }

    private boolean isGray(Color c) {
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();
        int tolerance = 10;
        return Math.abs(r - g) <= tolerance && Math.abs(g - b) <= tolerance && Math.abs(r - b) <= tolerance;
    }

    private boolean isSimilarColor(Color a, Color b, int tolerance) {
        if (a == null || b == null) {
            return false;
        }
        return Math.abs(a.getRed() - b.getRed()) <= tolerance
                && Math.abs(a.getGreen() - b.getGreen()) <= tolerance
                && Math.abs(a.getBlue() - b.getBlue()) <= tolerance;
    }

    private boolean isWarmAccent(Color c) {
        return c.getRed() >= 180 && c.getGreen() >= 90 && c.getBlue() <= 90;
    }

    private boolean isGreenAccent(Color c) {
        return c.getGreen() >= 160 && c.getRed() <= 120 && c.getBlue() <= 160;
    }

    private Color mapBadgeColor(String lowerKey, Color current, Color error, Color warning, Color info,
            Color success, Color neutral) {
        if (lowerKey.contains("error") || lowerKey.contains("danger") || lowerKey.contains("critical")) {
            return error;
        }
        if (lowerKey.contains("warn")) {
            return warning;
        }
        if (lowerKey.contains("success") || lowerKey.contains("ok") || lowerKey.contains("passed")) {
            return success;
        }
        if (lowerKey.contains("info") || lowerKey.contains("notice")) {
            return info;
        }
        if (lowerKey.contains("neutral") || lowerKey.contains("default") || lowerKey.contains("inactive")
                || lowerKey.contains("disabled")) {
            return neutral;
        }

        if (isRedDominant(current)) {
            return error;
        }
        if (isOrangeDominant(current)) {
            return warning;
        }
        if (isBlueDominant(current)) {
            return info;
        }
        if (isGreenDominant(current)) {
            return success;
        }
        if (isGray(current)) {
            return neutral;
        }

        return null;
    }

    private boolean isRedDominant(Color c) {
        return c.getRed() > c.getGreen() + 30 && c.getRed() > c.getBlue() + 30;
    }

    private boolean isOrangeDominant(Color c) {
        return c.getRed() >= 160 && c.getGreen() >= 90 && c.getBlue() <= 120;
    }

    private boolean isBlueDominant(Color c) {
        return c.getBlue() > c.getRed() + 30 && c.getBlue() > c.getGreen() + 30;
    }

    private boolean isGreenDominant(Color c) {
        return c.getGreen() > c.getRed() + 30 && c.getGreen() > c.getBlue() + 20;
    }

    private double calculateLuma(Color color) {
        return 0.2126 * color.getRed() + 0.7152 * color.getGreen() + 0.0722 * color.getBlue();
    }

    private void applyUiDefaults(JsonObject uiObj) {
        // 0. Handle Global Defaults (*) first
        if (uiObj.has("*")) {
            JsonObject globalDefaults = uiObj.getAsJsonObject("*");
            if (globalDefaults.has("background")) {
                Object bgInfo = resolveValue(globalDefaults.get("background").getAsString());
                if (bgInfo instanceof Color) {
                    Color bgColor = (Color) bgInfo;
                    // Apply to common global keys that cause "gray" look
                    UIManager.put("control", bgColor);
                    UIManager.put("window", bgColor);
                    UIManager.put("info", bgColor);
                    UIManager.put("nimbusBase", bgColor);
                    UIManager.put("nimbusLightBackground", bgColor);
                    UIManager.put("Viewport.background", bgColor);
                    UIManager.put("ScrollPane.background", bgColor);
                    UIManager.put("SplitPane.background", bgColor);
                    UIManager.put("Panel.background", bgColor);
                    UIManager.put("OptionPane.background", bgColor);
                }
            }
            if (globalDefaults.has("foreground")) {
                Object fgInfo = resolveValue(globalDefaults.get("foreground").getAsString());
                if (fgInfo instanceof Color) {
                    Color fgColor = (Color) fgInfo;
                    UIManager.put("controlText", fgColor);
                    UIManager.put("text", fgColor);
                    UIManager.put("infoText", fgColor);
                    UIManager.put("Label.foreground", fgColor);
                    UIManager.put("Panel.foreground", fgColor);
                }
            }
        }

        for (Map.Entry<String, JsonElement> entry : uiObj.entrySet()) {
            String primaryKey = entry.getKey();
            JsonElement value = entry.getValue();

            if (primaryKey.equals("*"))
                continue; // Already handled

            if (value.isJsonObject()) {
                // Nested object, e.g. "Button": { "background": ... }
                // Swing UIManager keys are often "Button.background"
                processNestedUiObject(primaryKey, value.getAsJsonObject());
            } else {
                // Direct value, e.g. "*": { ... } - skipping wildcard for now for simplicity or
                // handling separately
                // or simple keys if any
            }
        }

    }

    private void applySyntaxColors() {
        Color fg = colorPalette.getOrDefault("primaryForeground", Color.WHITE);
        Color bg = colorPalette.getOrDefault("primaryBackground", Color.BLACK);
        Color keyword = colorPalette.getOrDefault("syntaxKeyword", colorPalette.getOrDefault("cyan", fg));
        Color type = colorPalette.getOrDefault("syntaxType", colorPalette.getOrDefault("green", fg));
        Color function = colorPalette.getOrDefault("syntaxFunction", keyword);
        Color stringColor = colorPalette.getOrDefault("syntaxString", colorPalette.getOrDefault("cyan", fg));
        Color number = colorPalette.getOrDefault("syntaxNumber", colorPalette.getOrDefault("red", fg));
        Color comment = colorPalette.getOrDefault("syntaxComment", colorPalette.getOrDefault("overlay1", fg));
        Color operator = colorPalette.getOrDefault("syntaxOperator", keyword);
        Color variable = colorPalette.getOrDefault("syntaxVariable", fg);
        Color lineNumber = colorPalette.getOrDefault("syntaxLineNumber", comment);
        Color tag = colorPalette.getOrDefault("syntaxTag", keyword);
        Color attribute = colorPalette.getOrDefault("syntaxAttribute", colorPalette.getOrDefault("yellow", fg));
        Color httpFirstLine = colorPalette.getOrDefault("syntaxHttpFirstLine",
                colorPalette.getOrDefault("accentColor", keyword));
        Color headerName = colorPalette.getOrDefault("syntaxHeaderName",
                colorPalette.getOrDefault("secondaryAccentColor", attribute));
        Color headerValue = colorPalette.getOrDefault("syntaxHeaderValue", fg);

        UIManager.put("Burp.textEditorText", fg);
        UIManager.put("Burp.textEditorHttpFirstLine", httpFirstLine);
        UIManager.put("Burp.textEditorSeparator", lineNumber);
        UIManager.put("Burp.textEditorLineNumbers", lineNumber);
        UIManager.put("Burp.textEditorReservedWord", keyword);
        UIManager.put("Burp.textEditorReservedWord2", keyword);
        UIManager.put("Burp.textEditorFunction", function);
        UIManager.put("Burp.textEditorOperator", operator);
        UIManager.put("Burp.textEditorVariable", variable);
        UIManager.put("Burp.textEditorAnnotation", keyword);
        UIManager.put("Burp.textEditorDataType", type);
        UIManager.put("Burp.textEditorLiteralString", stringColor);
        UIManager.put("Burp.textEditorLiteralQuote", stringColor);
        UIManager.put("Burp.textEditorLiteralNumber", number);
        UIManager.put("Burp.textEditorLiteralBoolean", number);
        UIManager.put("Burp.textEditorRegex", stringColor);
        UIManager.put("Burp.textEditorComment", comment);
        UIManager.put("Burp.textEditorCdata", stringColor);
        UIManager.put("Burp.textEditorCdataDelimiter", comment);
        UIManager.put("Burp.textEditorTagName", tag);
        UIManager.put("Burp.textEditorTagDelimiter", tag);
        UIManager.put("Burp.textEditorEntityReference", stringColor);
        UIManager.put("Burp.textEditorParamName", headerName);
        UIManager.put("Burp.textEditorParamValue", headerValue);
        UIManager.put("Burp.textEditorHeaderName", headerName);
        UIManager.put("Burp.textEditorHeaderValue", headerValue);
        UIManager.put("Burp.textEditorCookieName", headerName);
        UIManager.put("Burp.textEditorCookieValue", headerValue);

        // Baseline Swing text components used by Burp views/tabs in different contexts.
        UIManager.put("TextArea.foreground", fg);
        UIManager.put("TextField.foreground", fg);
        UIManager.put("TextPane.foreground", fg);
        UIManager.put("EditorPane.foreground", fg);
        UIManager.put("FormattedTextField.foreground", fg);
        UIManager.put("PasswordField.foreground", fg);
        UIManager.put("List.foreground", fg);
        UIManager.put("Table.foreground", fg);
        UIManager.put("Tree.foreground", fg);
        UIManager.put("TextArea.background", bg);
        UIManager.put("TextField.background", bg);
        UIManager.put("TextPane.background", bg);
        UIManager.put("EditorPane.background", bg);
        UIManager.put("FormattedTextField.background", bg);
        UIManager.put("PasswordField.background", bg);
        UIManager.put("TextArea.caretForeground", fg);
        UIManager.put("TextField.caretForeground", fg);
        UIManager.put("TextPane.caretForeground", fg);
        UIManager.put("EditorPane.caretForeground", fg);
        UIManager.put("FormattedTextField.caretForeground", fg);
        UIManager.put("PasswordField.caretForeground", fg);

        enforceEditorTextFallbacks(fg, keyword, stringColor, number, comment, attribute, lineNumber, httpFirstLine,
                headerName, headerValue);
    }

    private void enforceEditorTextFallbacks(Color fg, Color keyword, Color stringColor, Color number, Color comment,
            Color attribute, Color lineNumber, Color httpFirstLine, Color headerName, Color headerValue) {
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object keyObj = keys.nextElement();
            if (!(keyObj instanceof String)) {
                continue;
            }

            String key = (String) keyObj;
            String lowerKey = key.toLowerCase();
            boolean isEditorKey = lowerKey.contains("texteditor")
                    || lowerKey.contains("editor")
                    || lowerKey.contains("http")
                    || lowerKey.contains("request")
                    || lowerKey.contains("response");
            if (!isEditorKey) {
                continue;
            }

            Object value = UIManager.get(key);
            if (!(value instanceof Color)) {
                continue;
            }

            Color mapped = null;
            if (lowerKey.contains("firstline") || lowerKey.contains("requestline") || lowerKey.contains("statusline")
                    || (lowerKey.contains("http") && lowerKey.contains("line"))) {
                mapped = httpFirstLine;
            } else if ((lowerKey.contains("line") && lowerKey.contains("number")) || lowerKey.contains("separator")
                    || lowerKey.contains("gutter")) {
                mapped = lineNumber;
            } else if (lowerKey.contains("comment")) {
                mapped = comment;
            } else if (lowerKey.contains("header") && lowerKey.contains("name")) {
                mapped = headerName;
            } else if (lowerKey.contains("header") && lowerKey.contains("value")) {
                mapped = headerValue;
            } else if (lowerKey.contains("string") || lowerKey.contains("quote") || lowerKey.contains("value")) {
                mapped = stringColor;
            } else if (lowerKey.contains("number") || lowerKey.contains("boolean") || lowerKey.contains("digit")) {
                mapped = number;
            } else if (lowerKey.contains("keyword") || lowerKey.contains("reserved") || lowerKey.contains("operator")
                    || lowerKey.contains("function") || lowerKey.contains("tag")) {
                mapped = keyword;
            } else if (lowerKey.contains("header") || lowerKey.contains("param") || lowerKey.contains("cookie")
                    || lowerKey.contains("attribute") || lowerKey.contains("name")) {
                mapped = attribute;
            } else if (lowerKey.contains("text") || lowerKey.contains("foreground") || lowerKey.contains("default")
                    || lowerKey.contains("plain")) {
                mapped = fg;
            }

            if (mapped != null) {
                UIManager.put(key, mapped);
            }
        }
    }

    private void applyEditorTypography() {
        Font base = resolveBaseEditorFont();
        Font normal = base.deriveFont(Font.PLAIN);
        Font strong = base.deriveFont(Font.BOLD);
        Font strongLarge = base.deriveFont(Font.BOLD, base.getSize2D() + 1.0f);

        UIManager.put("Burp.textEditorFont", normal);
        UIManager.put("TextArea.font", normal);
        UIManager.put("TextField.font", normal);
        UIManager.put("TextPane.font", normal);
        UIManager.put("EditorPane.font", normal);

        UIManager.put("Burp.textEditorHttpFirstLineFont", strongLarge);
        UIManager.put("Burp.textEditorHeaderNameFont", strong);
        UIManager.put("Burp.textEditorParamNameFont", strong);
        UIManager.put("Burp.textEditorCookieNameFont", strong);

        enforceEditorFontFallbacks(normal, strongLarge, strong);
    }

    private Font resolveBaseEditorFont() {
        Object[] preferredKeys = {
                UIManager.get("Burp.textEditorFont"),
                UIManager.get("TextArea.font"),
                UIManager.get("EditorPane.font"),
                UIManager.get("TextPane.font"),
                UIManager.get("Label.font")
        };
        for (Object candidate : preferredKeys) {
            if (candidate instanceof Font) {
                return (Font) candidate;
            }
        }
        return new Font(Font.MONOSPACED, Font.PLAIN, 13);
    }

    private Font resolveButtonFont() {
        Object[] candidates = {
                UIManager.get("Button.font"),
                UIManager.get("Label.font"),
                UIManager.get("TextField.font")
        };
        for (Object candidate : candidates) {
            if (candidate instanceof Font) {
                Font font = (Font) candidate;
                return font.deriveFont(Font.BOLD, Math.max(font.getSize2D(), 13.0f));
            }
        }
        return new Font(Font.SANS_SERIF, Font.BOLD, 13);
    }

    private void enforceEditorFontFallbacks(Font normal, Font strongLarge, Font strong) {
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object keyObj = keys.nextElement();
            if (!(keyObj instanceof String)) {
                continue;
            }

            String key = (String) keyObj;
            String lowerKey = key.toLowerCase();
            boolean isEditorKey = lowerKey.contains("texteditor")
                    || lowerKey.contains("editor")
                    || lowerKey.contains("http")
                    || lowerKey.contains("request")
                    || lowerKey.contains("response");
            if (!isEditorKey) {
                continue;
            }

            Object value = UIManager.get(key);
            if (!(value instanceof Font) || !lowerKey.contains("font")) {
                continue;
            }

            if (lowerKey.contains("firstline") || lowerKey.contains("requestline") || lowerKey.contains("statusline")) {
                UIManager.put(key, strongLarge);
            } else if ((lowerKey.contains("header") && lowerKey.contains("name"))
                    || (lowerKey.contains("param") && lowerKey.contains("name"))
                    || (lowerKey.contains("cookie") && lowerKey.contains("name"))) {
                UIManager.put(key, strong);
            } else {
                UIManager.put(key, normal);
            }
        }
    }

    private void applyButtonTextColors() {
        Color secondaryFg = colorPalette.getOrDefault("buttonSecondaryForeground",
                colorPalette.getOrDefault("buttonForeground",
                        colorPalette.getOrDefault("primaryForeground", Color.WHITE)));
        Color primaryFg = colorPalette.getOrDefault("buttonPrimaryForeground",
                colorPalette.getOrDefault("primaryBackground", secondaryFg));
        Color action = colorPalette.getOrDefault("actionCyan",
                colorPalette.getOrDefault("accentColor", secondaryFg));
        Color actionHover = colorPalette.getOrDefault("actionCyanHover",
                colorPalette.getOrDefault("secondaryAccentColor", action));
        Color actionPressed = colorPalette.getOrDefault("actionCyanPressed",
                colorPalette.getOrDefault("selectionBackground", action));
        Color secondaryBg = colorPalette.getOrDefault("buttonSecondaryBackground",
                colorPalette.getOrDefault("secondaryBackground",
                        colorPalette.getOrDefault("primaryBackground", Color.DARK_GRAY)));
        Color secondaryHover = colorPalette.getOrDefault("buttonSecondaryHover",
                colorPalette.getOrDefault("hoverBackground", secondaryBg));
        Color secondaryPressed = colorPalette.getOrDefault("buttonSecondaryPressed",
                colorPalette.getOrDefault("selectionBackground", secondaryHover));
        Color secondaryBorder = colorPalette.getOrDefault("buttonSecondaryBorder",
                colorPalette.getOrDefault("separatorBright",
                        colorPalette.getOrDefault("separatorColor", action)));
        Color focus = colorPalette.getOrDefault("focusRing", actionHover);
        Color primaryReadable = ensureReadableForeground(action, primaryFg);
        Color secondaryReadable = ensureReadableForeground(secondaryBg, secondaryFg);
        Color primaryDisabledBg = colorPalette.getOrDefault("buttonPrimaryDisabledBackground",
                colorPalette.getOrDefault("selectionBackground",
                        colorPalette.getOrDefault("buttonSecondaryBackground", actionPressed)));
        Color disabledReadable = primaryReadable;

        // Secondary / ghost buttons (e.g. Cancel)
        UIManager.put("Button.foreground", secondaryReadable);
        UIManager.put("Button.disabledText", disabledReadable);
        UIManager.put("Button.disabledForeground", disabledReadable);
        UIManager.put("Button.background", secondaryBg);
        UIManager.put("Button.startBackground", secondaryBg);
        UIManager.put("Button.endBackground", secondaryBg);
        UIManager.put("Button.startBorderColor", secondaryBorder);
        UIManager.put("Button.endBorderColor", secondaryBorder);
        UIManager.put("Button.hoverBackground", secondaryHover);
        UIManager.put("Button.pressedBackground", secondaryPressed);
        UIManager.put("Button.focusColor", focus);
        UIManager.put("Button.focusedBorderColor", focus);
        UIManager.put("Button.margin", new Insets(5, 12, 5, 12));
        UIManager.put("Button.default.margin", new Insets(5, 12, 5, 12));
        UIManager.put("Button.minimumHeight", 26);
        UIManager.put("Button.default.minimumHeight", 26);
        UIManager.put("Button.primary.minimumHeight", 26);
        UIManager.put("OptionPane.buttonPadding", 10);
        UIManager.put("OptionPane.sameSizeButtons", Boolean.TRUE);
        UIManager.put("OptionPane.buttonMinimumWidth", 72);

        // Keep default/primary neutral globally; action cyan is applied only in runtime-targeted buttons.
        UIManager.put("Button.default.foreground", secondaryReadable);
        UIManager.put("Button.defaultFocused.foreground", secondaryReadable);
        UIManager.put("Button.default.disabledText", secondaryReadable);
        UIManager.put("Button.default.disabledForeground", secondaryReadable);
        UIManager.put("Button.default.disabledBackground", primaryDisabledBg);
        UIManager.put("Button.primary.foreground", secondaryReadable);
        UIManager.put("Button.primary.disabledText", secondaryReadable);
        UIManager.put("Button.primary.disabledForeground", secondaryReadable);
        UIManager.put("Button.primary.disabledBackground", primaryDisabledBg);
        UIManager.put("Button.default.background", secondaryBg);
        UIManager.put("Button.default.startBackground", secondaryBg);
        UIManager.put("Button.default.endBackground", secondaryBg);
        UIManager.put("Button.default.startBorderColor", secondaryBorder);
        UIManager.put("Button.default.endBorderColor", secondaryBorder);
        UIManager.put("Button.default.hoverBackground", secondaryHover);
        UIManager.put("Button.default.pressedBackground", secondaryPressed);
        UIManager.put("Button.default.focusColor", focus);
        UIManager.put("Button.default.focusedBorderColor", focus);
        UIManager.put("Button.primary.background", secondaryBg);
        UIManager.put("Button.primary.startBackground", secondaryBg);
        UIManager.put("Button.primary.endBackground", secondaryBg);
        UIManager.put("Button.primary.startBorderColor", secondaryBorder);
        UIManager.put("Button.primary.endBorderColor", secondaryBorder);
        UIManager.put("Button.primary.hoverBackground", secondaryHover);
        UIManager.put("Button.primary.pressedBackground", secondaryPressed);
        UIManager.put("Button.primary.focusColor", focus);
        UIManager.put("Button.primary.focusedBorderColor", focus);

        // Keep generic ActionButtonWithText neutral; action cyan is applied only to explicit runtime targets.
        UIManager.put("ActionButtonWithText.background", secondaryBg);
        UIManager.put("ActionButtonWithText.borderColor", secondaryBorder);
        UIManager.put("ActionButtonWithText.foreground", secondaryReadable);
        UIManager.put("ActionButtonWithText.disabledForeground", disabledReadable);
        UIManager.put("ActionButtonWithText.disabledText", disabledReadable);
        UIManager.put("ActionButtonWithText.disabledBackground", secondaryBg);
        UIManager.put("ActionButtonWithText.hoverBackground", secondaryHover);
        UIManager.put("ActionButtonWithText.hoverBorderColor", secondaryBorder);
        UIManager.put("ActionButtonWithText.pressedBackground", secondaryPressed);
        UIManager.put("ActionButtonWithText.pressedBorderColor", secondaryBorder);
        UIManager.put("ActionButtonWithText.margin", new Insets(5, 12, 5, 12));
        UIManager.put("ActionButtonWithText.minimumHeight", 26);

        UIManager.put("Component.focusColor", focus);
        UIManager.put("Component.focusedBorderColor", focus);

        Font buttonFont = resolveButtonFont();
        UIManager.put("Button.font", buttonFont);
        UIManager.put("Button.default.font", buttonFont);
        UIManager.put("Button.primary.font", buttonFont);
        UIManager.put("ActionButtonWithText.font", buttonFont);

        UIManager.put("Burp.buttonBackground", secondaryBg);
        UIManager.put("Burp.buttonHoverBackground", secondaryHover);
        UIManager.put("Burp.buttonPressedBackground", secondaryPressed);
        UIManager.put("Burp.buttonDisabledBackground", primaryDisabledBg);
        UIManager.put("Burp.buttonForeground", secondaryReadable);
        UIManager.put("Burp.buttonPrimaryForeground", secondaryReadable);
        UIManager.put("Burp.buttonHoverForeground", secondaryReadable);
        UIManager.put("Burp.buttonDisabledForeground", secondaryReadable);

        enforceButtonAccentFallbacks(action, actionHover, actionPressed, secondaryBg, secondaryHover, secondaryPressed,
                secondaryBorder, secondaryReadable, primaryReadable, focus);
    }

    private void enforceButtonAccentFallbacks(Color action, Color actionHover, Color actionPressed, Color secondaryBg,
            Color secondaryHover, Color secondaryPressed, Color secondaryBorder, Color secondaryFg, Color primaryFg,
            Color focus) {
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object keyObj = keys.nextElement();
            if (!(keyObj instanceof String)) {
                continue;
            }

            String key = (String) keyObj;
            String lowerKey = key.toLowerCase();
            boolean buttonKey = lowerKey.contains("button")
                    || lowerKey.contains("actionbutton")
                    || lowerKey.contains("burp.button");
            if (!buttonKey) {
                continue;
            }

            Object value = UIManager.get(key);
            if (!(value instanceof Color)) {
                continue;
            }

            boolean primaryActionKey = lowerKey.contains("send")
                    || lowerKey.contains("forward")
                    || lowerKey.contains("burp.buttonprimary");

            if (lowerKey.contains("focus")) {
                UIManager.put(key, focus);
            } else if (lowerKey.contains("foreground") || lowerKey.contains("text")) {
                UIManager.put(key, primaryActionKey ? primaryFg : secondaryFg);
            } else if (lowerKey.contains("disabled") && lowerKey.contains("background")) {
                UIManager.put(key, primaryActionKey
                        ? colorPalette.getOrDefault("buttonPrimaryDisabledBackground",
                                colorPalette.getOrDefault("selectionBackground", actionPressed))
                        : colorPalette.getOrDefault("buttonSecondaryDisabledBackground",
                                colorPalette.getOrDefault("primaryBackground", secondaryBg)));
            } else if (lowerKey.contains("hover") && lowerKey.contains("background")) {
                UIManager.put(key, primaryActionKey ? actionHover : secondaryHover);
            } else if (lowerKey.contains("pressed") && lowerKey.contains("background")) {
                UIManager.put(key, primaryActionKey ? actionPressed : secondaryPressed);
            } else if (lowerKey.contains("border")) {
                UIManager.put(key, primaryActionKey ? action : secondaryBorder);
            } else if (lowerKey.contains("background")) {
                UIManager.put(key, primaryActionKey ? action : secondaryBg);
            }
        }
    }

    private void applyModernLineAccents() {
        Color line = colorPalette.getOrDefault("separatorBright",
                colorPalette.getOrDefault("separatorColor",
                        colorPalette.getOrDefault("accentColor", Color.CYAN)));
        Color lineSoft = colorPalette.getOrDefault("separatorColor", line);

        // Frequently-used divider/separator keys.
        UIManager.put("Separator.separatorColor", line);
        UIManager.put("Separator.foreground", line);
        UIManager.put("Separator.background", lineSoft);
        UIManager.put("SplitPane.dividerFocusColor", line);
        UIManager.put("SplitPaneDivider.draggingColor", line);
        UIManager.put("Table.gridColor", line);
        UIManager.put("TableHeader.bottomSeparatorColor", line);
        UIManager.put("ToolBar.separatorColor", line);
        UIManager.put("Popup.borderColor", line);
        UIManager.put("TabbedPane.contentAreaColor", lineSoft);
        UIManager.put("ScrollPane.borderColor", lineSoft);
        UIManager.put("Component.borderColor", lineSoft);
        UIManager.put("Borders.color", lineSoft);

        // Fallback pass for platform/LAF-specific naming differences.
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object keyObj = keys.nextElement();
            if (!(keyObj instanceof String)) {
                continue;
            }

            String key = (String) keyObj;
            String lowerKey = key.toLowerCase();
            boolean lineKey = lowerKey.contains("separator")
                    || lowerKey.contains("divider")
                    || lowerKey.contains("split")
                    || lowerKey.contains("grid")
                    || lowerKey.contains("border");
            if (!lineKey) {
                continue;
            }

            // Keep semantic status/focus borders untouched.
            if (lowerKey.contains("error") || lowerKey.contains("warning") || lowerKey.contains("focus")
                    || lowerKey.contains("selection") || lowerKey.contains("active")
                    || lowerKey.contains("pressed")) {
                continue;
            }

            Object value = UIManager.get(key);
            if (value instanceof Color) {
                UIManager.put(key, lowerKey.contains("separator") || lowerKey.contains("divider") ? line : lineSoft);
            }
        }
    }

    private void normalizeButtonBackgrounds() {
        Color accent = colorPalette.getOrDefault("accentColor",
                colorPalette.getOrDefault("primaryForeground", Color.WHITE));
        Color accentAlt = colorPalette.getOrDefault("secondaryAccentColor", accent);

        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object keyObj = keys.nextElement();
            if (!(keyObj instanceof String)) {
                continue;
            }

            String key = (String) keyObj;
            String lowerKey = key.toLowerCase();
            if (!lowerKey.contains("button") || !lowerKey.contains("background")
                    || lowerKey.contains("selection")) {
                continue;
            }

            Object value = UIManager.get(key);
            if (!(value instanceof Color)) {
                continue;
            }

            Color c = (Color) value;
            if (isSimilarColor(c, accent, 6) || isSimilarColor(c, accentAlt, 6)) {
                continue;
            }

            if (isWarmAccent(c)) {
                UIManager.put(key, accent);
            } else if (isGreenAccent(c)) {
                UIManager.put(key, accentAlt);
            }
        }
    }

    private void applyBadgeColors() {
        Color fg = colorPalette.getOrDefault("badgeForeground",
                colorPalette.getOrDefault("primaryBackground", Color.BLACK));
        Color error = colorPalette.getOrDefault("badgeError", colorPalette.getOrDefault("red", Color.RED));
        Color warning = colorPalette.getOrDefault("badgeWarning", colorPalette.getOrDefault("yellow", error));
        Color info = colorPalette.getOrDefault("badgeInfo", colorPalette.getOrDefault("blue", error));
        Color success = colorPalette.getOrDefault("badgeSuccess", colorPalette.getOrDefault("green", error));
        Color neutral = colorPalette.getOrDefault("badgeNeutral",
                colorPalette.getOrDefault("surface1", colorPalette.getOrDefault("borderColor", error)));

        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object keyObj = keys.nextElement();
            if (!(keyObj instanceof String)) {
                continue;
            }

            String key = (String) keyObj;
            String lowerKey = key.toLowerCase();
            if (!(lowerKey.contains("badge") || lowerKey.contains("pill") || lowerKey.contains("issue")
                    || lowerKey.contains("counter") || lowerKey.contains("tag"))) {
                continue;
            }

            Object value = UIManager.get(key);
            if (!(value instanceof Color)) {
                continue;
            }

            if (lowerKey.contains("foreground") || lowerKey.contains("text")) {
                UIManager.put(key, fg);
                continue;
            }

            Color current = (Color) value;
            Color mapped = mapBadgeColor(lowerKey, current, error, warning, info, success, neutral);
            if (mapped != null) {
                UIManager.put(key, mapped);
            }
        }
    }

    private void applySelectionColors() {
        Color selBg = colorPalette.getOrDefault("selectionBackground",
                colorPalette.getOrDefault("accentColor", Color.DARK_GRAY));
        Color selFg = colorPalette.getOrDefault("selectionForeground",
                colorPalette.getOrDefault("primaryForeground", Color.WHITE));
        Color selInactiveBg = colorPalette.getOrDefault("selectionInactiveBackground", selBg);
        Color selInactiveFg = colorPalette.getOrDefault("selectionInactiveForeground", selFg);

        UIManager.put("TextArea.selectionBackground", selBg);
        UIManager.put("TextArea.selectionForeground", selFg);
        UIManager.put("TextField.selectionBackground", selBg);
        UIManager.put("TextField.selectionForeground", selFg);
        UIManager.put("TextPane.selectionBackground", selBg);
        UIManager.put("TextPane.selectionForeground", selFg);
        UIManager.put("EditorPane.selectionBackground", selBg);
        UIManager.put("EditorPane.selectionForeground", selFg);
        UIManager.put("List.selectionBackground", selBg);
        UIManager.put("List.selectionForeground", selFg);
        UIManager.put("Table.selectionBackground", selBg);
        UIManager.put("Table.selectionForeground", selFg);
        UIManager.put("Tree.selectionBackground", selBg);
        UIManager.put("Tree.selectionForeground", selFg);

        UIManager.put("TextArea.inactiveSelectionBackground", selInactiveBg);
        UIManager.put("TextArea.inactiveSelectionForeground", selInactiveFg);
        UIManager.put("TextField.inactiveSelectionBackground", selInactiveBg);
        UIManager.put("TextField.inactiveSelectionForeground", selInactiveFg);
        UIManager.put("TextPane.inactiveSelectionBackground", selInactiveBg);
        UIManager.put("TextPane.inactiveSelectionForeground", selInactiveFg);
        UIManager.put("EditorPane.inactiveSelectionBackground", selInactiveBg);
        UIManager.put("EditorPane.inactiveSelectionForeground", selInactiveFg);

        UIManager.put("Burp.textEditorSelectionBackground", selBg);
        UIManager.put("Burp.textEditorSelectionForeground", selFg);
        UIManager.put("Burp.textEditorSelectionInactiveBackground", selInactiveBg);
        UIManager.put("Burp.textEditorSelectionInactiveForeground", selInactiveFg);

        // Keep context menus readable; do not use bright action cyan for menu selection.
        UIManager.put("Menu.selectionBackground", selBg);
        UIManager.put("Menu.selectionForeground", selFg);
        UIManager.put("MenuItem.selectionBackground", selBg);
        UIManager.put("MenuItem.selectionForeground", selFg);
        UIManager.put("CheckBoxMenuItem.selectionBackground", selBg);
        UIManager.put("CheckBoxMenuItem.selectionForeground", selFg);
        UIManager.put("RadioButtonMenuItem.selectionBackground", selBg);
        UIManager.put("RadioButtonMenuItem.selectionForeground", selFg);
        UIManager.put("PopupMenu.background", colorPalette.getOrDefault("panelBackground", selBg));
    }

    private void applyScrollBarColors() {
        // Burp/FlatLaf dark themes often end up with ScrollBar.* colors matching the background
        // (especially after brute-force UIManager rewrites), which makes the thumb effectively invisible.
        Color track = colorPalette.getOrDefault("primaryBackground",
                colorPalette.getOrDefault("panelBackground", Color.DARK_GRAY));
        Color thumb = colorPalette.getOrDefault("borderColor",
                colorPalette.getOrDefault("surface1", Color.GRAY));
        Color hoverThumb = colorPalette.getOrDefault("secondaryAccentColor",
                colorPalette.getOrDefault("accentColor", thumb));
        Color pressedThumb = colorPalette.getOrDefault("accentColor", hoverThumb);

        // Cross-LAF baseline keys
        UIManager.put("ScrollBar.track", track);
        UIManager.put("ScrollBar.trackHighlight", track);
        UIManager.put("ScrollBar.background", track);
        UIManager.put("ScrollBar.foreground", thumb);

        UIManager.put("ScrollBar.thumb", thumb);
        UIManager.put("ScrollBar.thumbHighlight", thumb);
        UIManager.put("ScrollBar.thumbShadow", thumb.darker());
        UIManager.put("ScrollBar.thumbDarkShadow", thumb.darker().darker());

        // FlatLaf-specific extras (safe no-ops on other LAFs)
        UIManager.put("ScrollBar.hoverThumbColor", hoverThumb);
        UIManager.put("ScrollBar.pressedThumbColor", pressedThumb);
        UIManager.put("ScrollBar.hoverTrackColor", track);
        UIManager.put("ScrollBar.pressedTrackColor", track);
        UIManager.put("ScrollBar.thumbBorderColor", thumb.darker());
        UIManager.put("ScrollBar.buttonArrowColor", hoverThumb);
        UIManager.put("ScrollBar.buttonHoverArrowColor", pressedThumb);
        UIManager.put("ScrollBar.buttonPressedArrowColor", pressedThumb);
        UIManager.put("ScrollBar.buttonBackground", track);
        UIManager.put("ScrollBar.buttonHoverBackground", track);
        UIManager.put("ScrollBar.buttonPressedBackground", track);
        UIManager.put("ScrollBar.trackArc", 999);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.trackInsets", new java.awt.Insets(0, 0, 0, 0));
        UIManager.put("ScrollBar.thumbInsets", new java.awt.Insets(2, 2, 2, 2));
        UIManager.put("ScrollBar.width", 12);
        UIManager.put("ScrollBar.minimumThumbSize", new java.awt.Dimension(24, 24));

        enforceScrollBarFallbacks(track, thumb, hoverThumb, pressedThumb);

        // Windows often needs a slightly wider, non-overlay thumb to stay visible.
        if (isWindows()) {
            UIManager.put("ScrollBar.width", 14);
            UIManager.put("ScrollBar.thumbInsets", new java.awt.Insets(1, 1, 1, 1));
            UIManager.put("ScrollBar.showButtons", true);
        }
    }

    private void enforceScrollBarFallbacks(Color track, Color thumb, Color hoverThumb, Color pressedThumb) {
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object keyObj = keys.nextElement();
            if (!(keyObj instanceof String)) {
                continue;
            }

            String key = (String) keyObj;
            String lowerKey = key.toLowerCase();
            if (!lowerKey.contains("scrollbar")) {
                continue;
            }

            Object value = UIManager.get(key);
            if (!(value instanceof Color)) {
                continue;
            }

            if (lowerKey.contains("pressed") && lowerKey.contains("thumb")) {
                UIManager.put(key, pressedThumb);
            } else if (lowerKey.contains("hover") && lowerKey.contains("thumb")) {
                UIManager.put(key, hoverThumb);
            } else if (lowerKey.contains("thumb")) {
                UIManager.put(key, thumb);
            } else if (lowerKey.contains("track") || lowerKey.contains("background")) {
                UIManager.put(key, track);
            } else if (lowerKey.contains("foreground")) {
                UIManager.put(key, thumb);
            } else if (lowerKey.contains("border") || lowerKey.contains("shadow")) {
                UIManager.put(key, thumb.darker());
            }
        }
    }

    private boolean isWindows() {
        return System.getProperty("os.name", "").toLowerCase().contains("win");
    }

    private void installDynamicButtonHook() {
        if (componentHookInstalled) {
            return;
        }
        componentHookInstalled = true;
        Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
            if (!(event instanceof ContainerEvent)) {
                return;
            }
            ContainerEvent ce = (ContainerEvent) event;
            if (ce.getID() != ContainerEvent.COMPONENT_ADDED) {
                return;
            }
            Component child = ce.getChild();
            if (child == null) {
                return;
            }
            SwingUtilities.invokeLater(() -> applyRuntimeButtonOverrides(child));
        }, AWTEvent.CONTAINER_EVENT_MASK);
    }

    private void forceRefresh() {
        // Force repaint/revalidate of all existing windows
        for (java.awt.Window window : java.awt.Window.getWindows()) {
            SwingUtilities.updateComponentTreeUI(window);
            applyRuntimeButtonOverrides(window);
            if (window.isDisplayable()) {
                window.pack(); // Optional: might preserve layout better
                window.repaint();
            }
        }

        // Also explicitly handle Frames if getWindows missed them (though getWindows
        // usually covers it)
        for (java.awt.Frame frame : java.awt.Frame.getFrames()) {
            SwingUtilities.updateComponentTreeUI(frame);
            applyRuntimeButtonOverrides(frame);
            if (frame.isDisplayable()) {
                frame.repaint();
            }
        }
    }

    private void applyRuntimeButtonOverrides(Component root) {
        if (root == null) {
            return;
        }

        boolean menuComponent = isMenuComponent(root);
        if (menuComponent) {
            styleRuntimeMenuComponent(root);
        } else {
            ensureReadableComponentText(root);
        }

        if (root instanceof AbstractButton && !menuComponent) {
            ensureReadableButtonText((AbstractButton) root);
            styleRuntimeButton((AbstractButton) root);
        } else if (!menuComponent) {
            styleRuntimeTextActionComponent(root);
            styleRuntimeSendComponent(root);
        }

        if (root instanceof Container) {
            for (Component child : ((Container) root).getComponents()) {
                applyRuntimeButtonOverrides(child);
            }
        }
    }

    private void styleRuntimeButton(AbstractButton button) {
        String text = normalizeLabel(button.getText());
        String action = normalizeLabel(button.getActionCommand());
        String toolTip = normalizeLabel(button.getToolTipText());
        String name = normalizeLabel(button.getName());
        boolean isPrimaryAction = text.contains("send") || action.contains("send") || toolTip.contains("send")
                || name.contains("send") || text.contains("forward") || action.contains("forward")
                || toolTip.contains("forward") || name.contains("forward");
        boolean isCancel = text.contains("cancel") || action.contains("cancel") || toolTip.contains("cancel")
                || name.contains("cancel") || text.contains("drop") || action.contains("drop");
        if (!isPrimaryAction && !isCancel) {
            boolean textualButton = !text.isEmpty() || !action.isEmpty() || !toolTip.isEmpty();
            if (textualButton) {
                applyStandardTextButtonStyle(button);
            } else {
                applyNeutralButtonIfNeeded(button);
            }
            return;
        }

        Color actionBg = colorPalette.getOrDefault("actionCyan",
                colorPalette.getOrDefault("accentColor", new Color(0x35f0ea)));
        Color actionHover = colorPalette.getOrDefault("actionCyanHover",
                colorPalette.getOrDefault("secondaryAccentColor", actionBg));
        Color actionPressed = colorPalette.getOrDefault("actionCyanPressed",
                colorPalette.getOrDefault("selectionBackground", actionBg));
        Color actionFg = ensureReadableForeground(actionBg,
                colorPalette.getOrDefault("buttonPrimaryForeground",
                        colorPalette.getOrDefault("primaryBackground", Color.BLACK)));
        Color actionDisabledBg = colorPalette.getOrDefault("buttonPrimaryDisabledBackground",
                colorPalette.getOrDefault("selectionBackground", actionPressed));
        Color secondaryBg = colorPalette.getOrDefault("buttonSecondaryBackground",
                colorPalette.getOrDefault("secondaryBackground", Color.DARK_GRAY));
        Color secondaryHover = colorPalette.getOrDefault("buttonSecondaryHover",
                colorPalette.getOrDefault("hoverBackground", secondaryBg));
        Color secondaryPressed = colorPalette.getOrDefault("buttonSecondaryPressed",
                colorPalette.getOrDefault("selectionBackground", secondaryHover));
        Color secondaryFg = ensureReadableForeground(secondaryBg,
                colorPalette.getOrDefault("buttonSecondaryForeground",
                        colorPalette.getOrDefault("buttonForeground", Color.WHITE)));
        Color secondaryBorder = colorPalette.getOrDefault("buttonSecondaryBorder",
                colorPalette.getOrDefault("separatorColor", secondaryFg));
        Color secondaryDisabledBg = colorPalette.getOrDefault("buttonSecondaryDisabledBackground",
                colorPalette.getOrDefault("primaryBackground", secondaryBg));
        Color focus = colorPalette.getOrDefault("focusRing", actionHover);

        if (isPrimaryAction) {
            button.putClientProperty("FlatLaf.style", toFlatStyle(actionBg, actionFg, actionBg, actionHover,
                    actionPressed, focus, new Insets(5, 14, 5, 14), actionDisabledBg, actionFg));
            button.setBackground(actionBg);
            button.setForeground(actionFg);
            button.setBorder(BorderFactory.createLineBorder(actionBg, 1, true));
            ensureButtonVisualSync(button, actionBg, actionBg, actionHover, actionPressed, actionHover, actionPressed,
                    actionFg, actionDisabledBg);
            enforceModernButtonSizing(button, 82, 30, true);
        } else {
            button.putClientProperty("FlatLaf.style", toFlatStyle(secondaryBg, secondaryFg, secondaryBorder,
                    secondaryHover, secondaryPressed, focus, new Insets(5, 12, 5, 12), secondaryDisabledBg,
                    secondaryFg));
            button.setBackground(secondaryBg);
            button.setForeground(secondaryFg);
            button.setBorder(BorderFactory.createLineBorder(secondaryBorder, 1, true));
            ensureButtonVisualSync(button, secondaryBg, secondaryBorder, secondaryHover, secondaryPressed,
                    secondaryBorder, secondaryBorder, secondaryFg, secondaryDisabledBg);
            enforceModernButtonSizing(button, 72, 30, true);
        }

        button.putClientProperty("JComponent.outline", null);
        button.putClientProperty("JComponent.sizeVariant", "regular");
        button.putClientProperty("JButton.buttonType", null);
        button.setMargin(isPrimaryAction ? new Insets(5, 14, 5, 14) : new Insets(5, 12, 5, 12));
        button.setOpaque(true);
        button.setBorderPainted(true);
        button.setContentAreaFilled(true);
        button.setFocusPainted(false);
        button.setRolloverEnabled(true);
        button.repaint();
    }

    private void applyStandardTextButtonStyle(AbstractButton button) {
        if (button instanceof JCheckBox || button instanceof JRadioButton || button instanceof JToggleButton) {
            return;
        }
        if (isMenuComponent(button)) {
            return;
        }

        Color bg = colorPalette.getOrDefault("buttonSecondaryBackground",
                colorPalette.getOrDefault("secondaryBackground", new Color(0x042024)));
        Color hover = colorPalette.getOrDefault("buttonSecondaryHover",
                colorPalette.getOrDefault("hoverBackground", bg));
        Color pressed = colorPalette.getOrDefault("buttonSecondaryPressed",
                colorPalette.getOrDefault("selectionBackground", hover));
        Color border = colorPalette.getOrDefault("buttonSecondaryBorder",
                colorPalette.getOrDefault("separatorColor", hover));
        Color fg = ensureReadableForeground(bg,
                colorPalette.getOrDefault("buttonSecondaryForeground",
                        colorPalette.getOrDefault("buttonForeground", Color.WHITE)));
        Color disabledBg = colorPalette.getOrDefault("buttonSecondaryDisabledBackground",
                colorPalette.getOrDefault("primaryBackground", bg));
        Color focus = colorPalette.getOrDefault("focusRing", hover);

        button.putClientProperty("FlatLaf.style", toFlatStyle(bg, fg, border, hover, pressed, focus,
                new Insets(5, 12, 5, 12), disabledBg, fg));
        button.setBackground(bg);
        button.setForeground(fg);
        button.setBorder(BorderFactory.createLineBorder(border, 1, true));
        ensureButtonVisualSync(button, bg, border, hover, pressed, border, border, fg, disabledBg);
        enforceModernButtonSizing(button, 72, 30, true);
        button.putClientProperty("JComponent.sizeVariant", "regular");
        button.putClientProperty("JButton.buttonType", null);
        button.setMargin(new Insets(5, 12, 5, 12));
        button.setOpaque(true);
        button.setBorderPainted(true);
        button.setContentAreaFilled(true);
        button.setFocusPainted(false);
        button.setRolloverEnabled(true);
        button.repaint();
    }

    private void styleRuntimeSendComponent(Component component) {
        String name = normalizeLabel(component.getName());
        String text = normalizeLabel(extractText(component, "getText"));
        String toolTip = normalizeLabel(extractText(component, "getToolTipText"));
        String action = normalizeLabel(extractText(component, "getActionCommand"));
        boolean isPrimaryAction = text.contains("send") || action.contains("send") || toolTip.contains("send")
                || name.contains("send") || text.contains("forward") || action.contains("forward")
                || toolTip.contains("forward") || name.contains("forward");
        if (!isPrimaryAction) {
            return;
        }

        Color actionBg = colorPalette.getOrDefault("actionCyan",
                colorPalette.getOrDefault("accentColor", new Color(0x35f0ea)));
        Color actionHover = colorPalette.getOrDefault("actionCyanHover",
                colorPalette.getOrDefault("secondaryAccentColor", actionBg));
        Color actionPressed = colorPalette.getOrDefault("actionCyanPressed",
                colorPalette.getOrDefault("selectionBackground", actionBg));
        Color actionFg = ensureReadableForeground(actionBg,
                colorPalette.getOrDefault("buttonPrimaryForeground",
                        colorPalette.getOrDefault("primaryBackground", Color.BLACK)));
        Color focus = colorPalette.getOrDefault("focusRing", actionHover);
        Color actionDisabledBg = colorPalette.getOrDefault("buttonPrimaryDisabledBackground",
                colorPalette.getOrDefault("selectionBackground", actionPressed));

        if (component instanceof javax.swing.JComponent) {
            ((javax.swing.JComponent) component).putClientProperty("FlatLaf.style",
                    toFlatStyle(actionBg, actionFg, actionBg, actionHover, actionPressed, focus,
                            new Insets(5, 14, 5, 14), actionDisabledBg, actionFg));
        }
        component.setBackground(actionBg);
        component.setForeground(actionFg);
        component.repaint();
    }

    private void styleRuntimeTextActionComponent(Component component) {
        if (!(component instanceof javax.swing.JComponent) || component instanceof AbstractButton) {
            return;
        }
        javax.swing.JComponent jc = (javax.swing.JComponent) component;
        if (isMenuComponent(jc)) {
            return;
        }

        String className = component.getClass().getName().toLowerCase(Locale.ROOT);
        boolean buttonLikeClass = className.contains("button")
                || className.contains("actionbutton")
                || className.contains("actionlink")
                || className.contains("optionbutton")
                || className.contains("linkbutton");
        if (!buttonLikeClass) {
            return;
        }

        String text = normalizeLabel(extractText(component, "getText"));
        String action = normalizeLabel(extractText(component, "getActionCommand"));
        String toolTip = normalizeLabel(extractText(component, "getToolTipText"));
        if (text.isEmpty() && action.isEmpty() && toolTip.isEmpty()) {
            return;
        }

        Color bg = colorPalette.getOrDefault("buttonSecondaryBackground",
                colorPalette.getOrDefault("secondaryBackground", new Color(0x042024)));
        Color hover = colorPalette.getOrDefault("buttonSecondaryHover",
                colorPalette.getOrDefault("hoverBackground", bg));
        Color pressed = colorPalette.getOrDefault("buttonSecondaryPressed",
                colorPalette.getOrDefault("selectionBackground", hover));
        Color border = colorPalette.getOrDefault("buttonSecondaryBorder",
                colorPalette.getOrDefault("separatorColor", hover));
        Color fg = ensureReadableForeground(bg,
                colorPalette.getOrDefault("buttonSecondaryForeground",
                        colorPalette.getOrDefault("buttonForeground", Color.WHITE)));
        Color focus = colorPalette.getOrDefault("focusRing", hover);

        jc.putClientProperty("FlatLaf.style", toFlatStyle(bg, fg, border, hover, pressed, focus,
                new Insets(5, 12, 5, 12), bg, fg));
        jc.putClientProperty("JComponent.sizeVariant", "regular");
        jc.putClientProperty("JButton.buttonType", null);
        jc.setBackground(bg);
        jc.setForeground(fg);
        jc.setBorder(BorderFactory.createLineBorder(border, 1, true));
        Dimension preferred = jc.getPreferredSize();
        int width = Math.max(preferred != null ? preferred.width : 0, 72);
        int height = Math.max(preferred != null ? preferred.height : 0, 30);
        jc.setMinimumSize(new Dimension(72, 30));
        jc.setPreferredSize(new Dimension(width, height));
        jc.setOpaque(true);
        jc.repaint();
    }

    private void applyNeutralButtonIfNeeded(AbstractButton button) {
        if (button instanceof JCheckBox || button instanceof JRadioButton || button instanceof JToggleButton) {
            return;
        }
        if (isMenuComponent(button)) {
            return;
        }
        Color neutralBg = colorPalette.getOrDefault("buttonSecondaryBackground",
                colorPalette.getOrDefault("secondaryBackground", new Color(0x042024)));
        Color neutralHover = colorPalette.getOrDefault("buttonSecondaryHover",
                colorPalette.getOrDefault("hoverBackground", neutralBg));
        Color neutralPressed = colorPalette.getOrDefault("buttonSecondaryPressed",
                colorPalette.getOrDefault("selectionBackground", neutralHover));
        Color neutralBorder = colorPalette.getOrDefault("buttonSecondaryBorder",
                colorPalette.getOrDefault("separatorColor", neutralHover));
        Color neutralFg = ensureReadableForeground(neutralBg,
                colorPalette.getOrDefault("buttonSecondaryForeground",
                        colorPalette.getOrDefault("buttonForeground", Color.WHITE)));
        Color focus = colorPalette.getOrDefault("focusRing", neutralHover);

        Color currentBg = button.getBackground();
        boolean tooBright = currentBg != null && calculateLuma(currentBg) > 145;
        boolean cyanLike = currentBg != null && isSimilarColor(currentBg,
                colorPalette.getOrDefault("actionCyan", new Color(0x35f0ea)), 50);
        if (!tooBright && !cyanLike) {
            return;
        }

        button.putClientProperty("FlatLaf.style", toFlatStyle(neutralBg, neutralFg, neutralBorder, neutralHover,
                neutralPressed, focus, new Insets(5, 12, 5, 12), neutralBg, neutralFg));
        button.setBackground(neutralBg);
        button.setForeground(neutralFg);
        button.setBorder(BorderFactory.createLineBorder(neutralBorder, 1, true));
        ensureButtonVisualSync(button, neutralBg, neutralBorder, neutralHover, neutralPressed, neutralBorder,
                neutralBorder, neutralFg, neutralBg);
        enforceModernButtonSizing(button, 72, 30, true);
        button.putClientProperty("JComponent.sizeVariant", "regular");
        button.putClientProperty("JButton.buttonType", null);
        button.setMargin(new Insets(5, 12, 5, 12));
        button.setOpaque(true);
        button.setBorderPainted(true);
        button.setContentAreaFilled(true);
        button.setFocusPainted(false);
        button.setRolloverEnabled(true);
        button.repaint();
    }

    private void enforceModernButtonSizing(AbstractButton button, int minWidth, int minHeight, boolean bold) {
        if (button == null) {
            return;
        }

        Font base = button.getFont();
        if (base == null) {
            Object defaultFont = UIManager.get("Button.font");
            if (defaultFont instanceof Font) {
                base = (Font) defaultFont;
            }
        }
        if (base != null) {
            float size = Math.max(base.getSize2D(), 13.0f);
            int style = bold ? Font.BOLD : Font.PLAIN;
            button.setFont(base.deriveFont(style, size));
        }

        Dimension preferred = button.getPreferredSize();
        int width = Math.max(preferred != null ? preferred.width : 0, minWidth);
        int height = Math.max(preferred != null ? preferred.height : 0, minHeight);
        button.setMinimumSize(new Dimension(minWidth, minHeight));
        button.setPreferredSize(new Dimension(width, height));
    }

    private void ensureReadableComponentText(Component component) {
        if (component == null || component instanceof AbstractButton) {
            return;
        }

        Color bg = component.getBackground();
        if (bg == null) {
            return;
        }

        Color fg = component.getForeground();
        boolean bright = calculateLuma(bg) > 145;
        boolean cyanLike = isSimilarColor(bg, colorPalette.getOrDefault("actionCyan", new Color(0x35f0ea)), 52);
        if (!bright && !cyanLike) {
            return;
        }

        component.setForeground(ensureReadableForeground(bg, fg));
        if (component instanceof javax.swing.JComponent) {
            javax.swing.JComponent jc = (javax.swing.JComponent) component;
            if (!Boolean.TRUE.equals(jc.getClientProperty("hostile.componentReadableInstalled"))) {
                jc.putClientProperty("hostile.componentReadableInstalled", Boolean.TRUE);
                jc.addPropertyChangeListener("background", e -> {
                    Color dynamicBg = jc.getBackground();
                    Color dynamicFg = ensureReadableForeground(dynamicBg, jc.getForeground());
                    jc.setForeground(dynamicFg);
                    jc.repaint();
                });
            }
        }
    }

    private void normalizeWarmAccentsToCyan() {
        Color action = colorPalette.getOrDefault("actionCyan",
                colorPalette.getOrDefault("accentColor", Color.CYAN));
        Color actionHover = colorPalette.getOrDefault("actionCyanHover",
                colorPalette.getOrDefault("secondaryAccentColor", action));

        // Explicit keys that often carry orange defaults in Burp/IntelliJ-derived UI.
        UIManager.put("DefaultTabs.underlineColor", action);
        UIManager.put("EditorTabs.underlineColor", action);
        UIManager.put("TabbedPane.underlineColor", action);
        UIManager.put("ToolWindow.HeaderTab.underlineColor", action);
        UIManager.put("TabbedPane.selectedColor", action);
        UIManager.put("TabbedPane.focusColor", actionHover);
        UIManager.put("Button.default.focusColor", actionHover);
        UIManager.put("Button.default.focusedBorderColor", actionHover);
    }

    private void applyContextMenuColors() {
        Color menuBg = colorPalette.getOrDefault("panelBackground",
                colorPalette.getOrDefault("primaryBackground", new Color(0x042024)));
        Color menuFg = colorPalette.getOrDefault("primaryForeground", new Color(0xe6fbfb));
        Color menuSelBg = colorPalette.getOrDefault("menuSelectionBackground",
                colorPalette.getOrDefault("selectionBackground", new Color(0x114049)));
        Color menuSelFg = colorPalette.getOrDefault("menuSelectionForeground",
                ensureReadableForeground(menuSelBg, colorPalette.getOrDefault("selectionForeground", menuFg)));
        Color menuBorder = colorPalette.getOrDefault("separatorColor",
                colorPalette.getOrDefault("borderColor", menuSelBg));

        UIManager.put("PopupMenu.background", menuBg);
        UIManager.put("PopupMenu.foreground", menuFg);
        UIManager.put("PopupMenu.borderColor", menuBorder);
        UIManager.put("Menu.background", menuBg);
        UIManager.put("Menu.foreground", menuFg);
        UIManager.put("Menu.selectionBackground", menuSelBg);
        UIManager.put("Menu.selectionForeground", menuSelFg);
        UIManager.put("MenuItem.background", menuBg);
        UIManager.put("MenuItem.foreground", menuFg);
        UIManager.put("MenuItem.selectionBackground", menuSelBg);
        UIManager.put("MenuItem.selectionForeground", menuSelFg);
        UIManager.put("MenuItem.acceleratorForeground", menuFg);
        UIManager.put("MenuItem.acceleratorSelectionForeground", menuSelFg);
        UIManager.put("CheckBoxMenuItem.background", menuBg);
        UIManager.put("CheckBoxMenuItem.foreground", menuFg);
        UIManager.put("CheckBoxMenuItem.selectionBackground", menuSelBg);
        UIManager.put("CheckBoxMenuItem.selectionForeground", menuSelFg);
        UIManager.put("RadioButtonMenuItem.background", menuBg);
        UIManager.put("RadioButtonMenuItem.foreground", menuFg);
        UIManager.put("RadioButtonMenuItem.selectionBackground", menuSelBg);
        UIManager.put("RadioButtonMenuItem.selectionForeground", menuSelFg);
        UIManager.put("PopupMenu.selectionBackground", menuSelBg);
        UIManager.put("PopupMenu.selectionForeground", menuSelFg);
        UIManager.put("ActionMenu.background", menuBg);
        UIManager.put("ActionMenu.foreground", menuFg);
        UIManager.put("ActionMenu.selectionBackground", menuSelBg);
        UIManager.put("ActionMenu.selectionForeground", menuSelFg);
        UIManager.put("ActionPopupMenu.background", menuBg);
        UIManager.put("ActionPopupMenu.foreground", menuFg);
        UIManager.put("ActionPopupMenu.selectionBackground", menuSelBg);
        UIManager.put("ActionPopupMenu.selectionForeground", menuSelFg);

        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object keyObj = keys.nextElement();
            if (!(keyObj instanceof String)) {
                continue;
            }

            String key = (String) keyObj;
            String lowerKey = key.toLowerCase();
            boolean menuKey = lowerKey.contains("menu")
                    || lowerKey.contains("popup")
                    || lowerKey.contains("context")
                    || lowerKey.contains("actionmenu")
                    || lowerKey.contains("actionpopup");
            if (!menuKey) {
                continue;
            }

            Object value = UIManager.get(key);
            if (!(value instanceof Color)) {
                continue;
            }

            if (lowerKey.contains("selection") || lowerKey.contains("hover") || lowerKey.contains("armed")
                    || lowerKey.contains("highlight")) {
                if (lowerKey.contains("foreground") || lowerKey.contains("text")) {
                    UIManager.put(key, menuSelFg);
                } else {
                    UIManager.put(key, menuSelBg);
                }
            } else if (lowerKey.contains("foreground") || lowerKey.contains("text")
                    || lowerKey.contains("accelerator")) {
                UIManager.put(key, menuFg);
            } else if (lowerKey.contains("border") || lowerKey.contains("separator")
                    || lowerKey.contains("line")) {
                UIManager.put(key, menuBorder);
            } else if (lowerKey.contains("background")) {
                UIManager.put(key, menuBg);
            }
        }
    }

    private String toFlatStyle(Color bg, Color fg, Color border, Color hover, Color pressed, Color focus,
            Insets margin, Color disabledBg, Color disabledFg) {
        return "arc:9;"
                + "background:" + toHex(bg) + ";"
                + "foreground:" + toHex(fg) + ";"
                + "hoverForeground:" + toHex(fg) + ";"
                + "pressedForeground:" + toHex(fg) + ";"
                + "disabledForeground:" + toHex(disabledFg) + ";"
                + "disabledText:" + toHex(disabledFg) + ";"
                + "disabledBackground:" + toHex(disabledBg) + ";"
                + "borderColor:" + toHex(border) + ";"
                + "hoverBorderColor:" + toHex(hover) + ";"
                + "pressedBorderColor:" + toHex(pressed) + ";"
                + "focusedBorderColor:" + toHex(focus) + ";"
                + "focusColor:" + toHex(focus) + ";"
                + "hoverBackground:" + toHex(hover) + ";"
                + "pressedBackground:" + toHex(pressed) + ";"
                + "minimumHeight:26;"
                + "margin:" + margin.top + "," + margin.left + "," + margin.bottom + "," + margin.right + ";";
    }

    private void ensureButtonVisualSync(AbstractButton button, Color baseBg, Color baseBorder, Color hoverBg,
            Color pressedBg, Color hoverBorder, Color pressedBorder, Color fg, Color disabledBg) {
        if (Boolean.TRUE.equals(button.getClientProperty("hostile.syncInstalled"))) {
            return;
        }
        button.putClientProperty("hostile.syncInstalled", Boolean.TRUE);

        java.util.function.Consumer<javax.swing.ButtonModel> applyState = model -> {
            if (!model.isEnabled()) {
                button.setBackground(disabledBg);
                button.setBorder(BorderFactory.createLineBorder(baseBorder, 1, true));
            } else if (model.isPressed()) {
                button.setBackground(pressedBg);
                button.setBorder(BorderFactory.createLineBorder(pressedBorder, 1, true));
            } else if (model.isRollover()) {
                button.setBackground(hoverBg);
                button.setBorder(BorderFactory.createLineBorder(hoverBorder, 1, true));
            } else {
                button.setBackground(baseBg);
                button.setBorder(BorderFactory.createLineBorder(baseBorder, 1, true));
            }
            button.setForeground(fg);
            button.repaint();
        };

        button.getModel().addChangeListener(e -> applyState.accept(button.getModel()));
        applyState.accept(button.getModel());
    }

    private Color ensureReadableForeground(Color background, Color preferred) {
        if (background == null) {
            return preferred == null ? Color.WHITE : preferred;
        }
        if (preferred == null) {
            return calculateLuma(background) > 140 ? new Color(0x042024) : new Color(0xe6fbfb);
        }

        double contrastDelta = Math.abs(calculateLuma(background) - calculateLuma(preferred));
        if (contrastDelta >= 110) {
            return preferred;
        }
        return calculateLuma(background) > 140 ? new Color(0x042024) : new Color(0xe6fbfb);
    }

    private String normalizeLabel(String value) {
        if (value == null) {
            return "";
        }
        return value.replaceAll("<[^>]+>", " ")
                .replace('\n', ' ')
                .trim()
                .toLowerCase(Locale.ROOT);
    }

    private String toHex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    private String extractText(Component component, String methodName) {
        try {
            java.lang.reflect.Method method = component.getClass().getMethod(methodName);
            Object value = method.invoke(component);
            return value == null ? "" : String.valueOf(value);
        } catch (Exception ignored) {
            return "";
        }
    }

    private void ensureReadableButtonText(AbstractButton button) {
        if (button == null) {
            return;
        }
        Color bg = button.getBackground();
        Color fg = ensureReadableForeground(bg, button.getForeground());
        button.setForeground(fg);
        if (Boolean.TRUE.equals(button.getClientProperty("hostile.readableInstalled"))) {
            return;
        }
        button.putClientProperty("hostile.readableInstalled", Boolean.TRUE);
        button.getModel().addChangeListener(e -> {
            Color dynamicBg = button.getBackground();
            Color dynamicFg = ensureReadableForeground(dynamicBg, button.getForeground());
            button.setForeground(dynamicFg);
            button.repaint();
        });
        button.addPropertyChangeListener("background", e -> {
            Color dynamicBg = button.getBackground();
            Color dynamicFg = ensureReadableForeground(dynamicBg, button.getForeground());
            button.setForeground(dynamicFg);
            button.repaint();
        });
        button.addPropertyChangeListener("enabled", e -> {
            Color dynamicBg = button.getBackground();
            Color dynamicFg = ensureReadableForeground(dynamicBg, button.getForeground());
            button.setForeground(dynamicFg);
            button.repaint();
        });
    }

    private boolean isMenuComponent(Component component) {
        if (component == null) {
            return false;
        }

        if (component instanceof JMenuItem || component instanceof JPopupMenu) {
            return true;
        }

        String selfClass = component.getClass().getName().toLowerCase(Locale.ROOT);
        if (selfClass.contains("menuitem") || selfClass.contains("popupmenu") || selfClass.contains("actionmenu")) {
            return true;
        }

        Container parent = component.getParent();
        while (parent != null) {
            if (parent instanceof JPopupMenu || parent instanceof JMenuItem) {
                return true;
            }
            String parentClass = parent.getClass().getName().toLowerCase(Locale.ROOT);
            if (parentClass.contains("popupmenu") || parentClass.contains("menuitem")
                    || parentClass.contains("actionmenu")) {
                return true;
            }
            parent = parent.getParent();
        }

        return false;
    }

    private void styleRuntimeMenuComponent(Component component) {
        Color menuBg = colorPalette.getOrDefault("panelBackground",
                colorPalette.getOrDefault("primaryBackground", new Color(0x042024)));
        Color menuFg = colorPalette.getOrDefault("primaryForeground", new Color(0xe6fbfb));
        Color menuSelBg = colorPalette.getOrDefault("menuSelectionBackground",
                colorPalette.getOrDefault("selectionBackground", new Color(0x114049)));
        Color menuSelFg = colorPalette.getOrDefault("menuSelectionForeground",
                ensureReadableForeground(menuSelBg, colorPalette.getOrDefault("selectionForeground", menuFg)));

        if (component instanceof JMenuItem) {
            JMenuItem item = (JMenuItem) component;
            item.setOpaque(true);
            item.setBackground(menuBg);
            item.setForeground(menuFg);
            if (!Boolean.TRUE.equals(item.getClientProperty("hostile.menuSyncInstalled"))) {
                item.putClientProperty("hostile.menuSyncInstalled", Boolean.TRUE);
                item.getModel().addChangeListener(e -> {
                    javax.swing.ButtonModel model = item.getModel();
                    if (model.isArmed() || model.isSelected() || model.isRollover()) {
                        item.setBackground(menuSelBg);
                        item.setForeground(menuSelFg);
                    } else {
                        item.setBackground(menuBg);
                        item.setForeground(menuFg);
                    }
                    item.repaint();
                });
            }
        }

        if (component instanceof javax.swing.JComponent) {
            ((javax.swing.JComponent) component).putClientProperty("FlatLaf.style",
                    "background:" + toHex(menuBg) + ";"
                            + "foreground:" + toHex(menuFg) + ";"
                            + "selectionBackground:" + toHex(menuSelBg) + ";"
                            + "selectionForeground:" + toHex(menuSelFg) + ";");
        }
    }

    private void processNestedUiObject(String parentKey, JsonObject obj) {
        for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
            String key = entry.getKey();
            JsonElement val = entry.getValue();

            if (val.isJsonPrimitive()) {
                String valStr = val.getAsString();
                String fullKey = parentKey + "." + key;

                Object resolvedValue = resolveValue(valStr);
                if (resolvedValue != null) {
                    UIManager.put(fullKey, resolvedValue);
                }
            } else if (val.isJsonObject()) {
                processNestedUiObject(parentKey + "." + key, val.getAsJsonObject());
            }
        }
    }

    private Object resolveValue(String value) {
        // Check if it's a reference to a color in our palette
        if (colorPalette.containsKey(value)) {
            return colorPalette.get(value);
        }
        // Check if it's a hex string
        if (value.startsWith("#")) {
            return parseColor(value);
        }
        // Check for "opacityWithHex" - complex parsing, skipping for MVP or
        // implementing simple case
        if (value.startsWith("opacityWithHex")) {
            // format: "opacityWithHex colorName opacity"
            // e.g. "opacityWithHex red 0.1"
            String[] parts = value.split(" ");
            if (parts.length >= 3) {
                String colorName = parts[1];
                float opacity = Float.parseFloat(parts[2]);
                Color base = colorPalette.get(colorName);
                if (base != null) {
                    return new Color(base.getRed(), base.getGreen(), base.getBlue(), (int) (opacity * 255));
                }
            }
        }

        // Determine if it's an Integer (Int/Float values in JSON are Strings here)
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
        }

        return value; // Return raw string if nothing else matches (could be boolean, etc which Gson
                      // handles but we get string)
    }

    private Color parseColor(String hex) {
        // Handle hex formats #RRGGBB or #AARRGGBB
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }
        if (hex.length() == 6) {
            return new Color(
                    Integer.valueOf(hex.substring(0, 2), 16),
                    Integer.valueOf(hex.substring(2, 4), 16),
                    Integer.valueOf(hex.substring(4, 6), 16));
        } else if (hex.length() == 8) {
            // Assuming ARGB format if 8 chars, or RGBA? Java Color handles alpha as
            // constructor arg
            // Usually hex in themes is #RRGGBB
            // Let's stick to simple 6 char for now or parse alpha
            // If web hex #RRGGBBAA
            long val = Long.parseLong(hex, 16);
            // AARRGGBB is standard Java decode, but usually web is RGBA?
            // Let's assume standard RGB for now to avoid crashes
        }
        return Color.GRAY; // Default
    }
}
