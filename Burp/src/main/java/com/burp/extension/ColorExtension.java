package com.burp.extension;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Color;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ColorExtension implements BurpExtension {
    private MontoyaApi api;
    private Map<String, Color> colorPalette = new HashMap<>();

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

                // FORCE HIGH CONTRAST TEXT
                if (lowerKey.contains("text") || lowerKey.contains("foreground") || lowerKey.contains("label")
                        || lowerKey.contains("font")) {
                    // If text is dark, replace with theme foreground
                    if (calculateLuma(c) < 160) {
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

    private void forceRefresh() {
        // Force repaint/revalidate of all existing windows
        for (java.awt.Window window : java.awt.Window.getWindows()) {
            SwingUtilities.updateComponentTreeUI(window);
            if (window.isDisplayable()) {
                window.pack(); // Optional: might preserve layout better
                window.repaint();
            }
        }

        // Also explicitly handle Frames if getWindows missed them (though getWindows
        // usually covers it)
        for (java.awt.Frame frame : java.awt.Frame.getFrames()) {
            SwingUtilities.updateComponentTreeUI(frame);
            if (frame.isDisplayable()) {
                frame.repaint();
            }
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
