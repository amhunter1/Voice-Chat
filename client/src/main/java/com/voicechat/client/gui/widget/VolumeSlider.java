package com.voicechat.client.gui.widget;

public class VolumeSlider {

    private float value;
    private float min;
    private float max;
    private String label;

    public VolumeSlider(String label, float min, float max, float defaultValue) {
        this.label = label;
        this.min = min;
        this.max = max;
        this.value = defaultValue;
    }

    public void render(int x, int y, int width, int height) {
        // TODO: Render with Hytale GUI API
        // Pseudocode:
        // Draw background bar
        // drawRect(x, y + height / 2 - 2, x + width, y + height / 2 + 2, 0xFF444444);

        // Calculate handle position
        // float percentage = (value - min) / (max - min);
        // int handleX = x + (int)(percentage * width);

        // Draw handle
        // drawRect(handleX - 4, y, handleX + 4, y + height, 0xFFFFFFFF);

        // Draw label
        // drawString(fontRenderer, label + ": " + (int)value, x, y - 10, 0xFFFFFF);
    }

    public void setValue(float value) {
        this.value = Math.max(min, Math.min(max, value));
    }

    public float getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }
}
