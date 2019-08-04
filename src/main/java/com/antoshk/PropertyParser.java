package com.antoshk;

import com.intellij.psi.PsiFile;

import java.util.HashMap;
import java.util.Map;

public class PropertyParser {
    static Map<String, String> parse(PsiFile file) {
        Map<String, String> result = new HashMap<>();
        String[] lines = file.getText().split("\n");
        for (String line : lines) {
            String[] pair = line.split("=");
            result.put(pair[0].trim(), pair[1].trim());
        }
        return result;
    }
}
