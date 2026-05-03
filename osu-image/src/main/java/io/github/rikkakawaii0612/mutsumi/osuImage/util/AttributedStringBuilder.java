package io.github.rikkakawaii0612.mutsumi.osuImage.util;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// 嘿嘿 Deepseek 真好用
public class AttributedStringBuilder {
    private record TextRun(String text, Font font) {
    }

    private final List<TextRun> runs = new ArrayList<>();
    private Font currentFont = null; // 待定，或设置默认字体

    public AttributedStringBuilder() {
        // 可以设置一个默认字体，如 new Font(Font.DIALOG, Font.PLAIN, 12)
    }

    // 设置后续新增文本的字体
    public AttributedStringBuilder setFont(Font font) {
        this.currentFont = font;
        return this;
    }

    // 追加字符或字符串，使用当前设置的字体
    public AttributedStringBuilder append(String text) {
        if (currentFont == null) {
            throw new IllegalStateException("Font not set. Call setFont(Font) first.");
        }
        if (text != null && !text.isEmpty()) {
            runs.add(new TextRun(text, currentFont));
        }
        return this;
    }

    // 追加单个字符的便捷方法
    public AttributedStringBuilder append(char c) {
        return append(String.valueOf(c));
    }

    public AttributedStringBuilder clear() {
        this.runs.clear();
        return this;
    }

    public AttributedStringBuilder removeLast() {
        this.runs.removeLast();
        return this;
    }

    // 最终构建出 AttributedString 对象
    public AttributedString build() {
        // 计算总长度
        int totalLength = runs.stream().mapToInt(run -> run.text.length()).sum();

        // 创建一个临时 StringBuilder 来拼接所有文本
        StringBuilder rawText = new StringBuilder(totalLength);
        for (TextRun run : runs) {
            rawText.append(run.text);
        }

        // 创建最终的 AttributedString
        AttributedString result = new AttributedString(rawText.toString());

        // 重新为每个片段应用字体属性
        int currentIdx = 0;
        for (TextRun run : runs) {
            int endIdx = currentIdx + run.text.length();
            // 核心：通过 TextAttribute.FONT 来应用 Font 对象
            result.addAttribute(TextAttribute.FONT, run.font, currentIdx, endIdx);
            currentIdx = endIdx;
        }
        return result;
    }

    public Set<Font> getFonts() {
        return this.runs.stream().map(TextRun::font).collect(Collectors.toSet());
    }
}
