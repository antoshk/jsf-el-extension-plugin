package com.antoshk;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ElCompletionContributor extends CompletionContributor {

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        String text = parameters.getPosition().getText();
        int caretPosition = parameters.getOffset() - parameters.getPosition().getTextOffset();
        if (needEnumNameCompletion(text, caretPosition)) {
            Map<String, String> enums = Utils.collectProperties("enumPlugin.properties");
            String actual = getActualInput(text, caretPosition);
            enums.entrySet()
                    .stream()
                    .filter(entry -> entry.getKey().startsWith(actual))
                    .forEach(entry -> result.withPrefixMatcher(actual).addElement(LookupElementBuilder.create(entry.getKey())));
        } else if (needEnumConstantsCompletion(text, caretPosition)) {
            String actual = getActualInput(text, caretPosition);
            String[] nameParts = getNameParts(actual);
            Map<String, String> enums = Utils.collectProperties("enumPlugin.properties");
            String className = enums.get(nameParts[0]);
            if (className != null) {

                Utils.extractEnumConstants(className)
                        .filter(enumConst -> enumConst.getName().startsWith(nameParts[1]))
                        .forEach(enumConst -> {
                            LookupElementBuilder builder = LookupElementBuilder.create(enumConst.getName());
                            String args = Utils.createArgList(enumConst);
                            if (args.length() > 0) {
                                builder = builder.withTypeText(args);
                            }
                            result.withPrefixMatcher(nameParts[1]).addElement(builder);
                        });
            }
        }
    }

    private boolean needEnumConstantsCompletion(String text, int caretPosition) {
        if (text.contains("#{") && text.indexOf("#{") < caretPosition) {
            String actual = getActualInput(text, caretPosition);
            return actual.contains(".") && actual.indexOf('.') == actual.lastIndexOf('.');
        }
        return false;
    }

    private boolean needEnumNameCompletion(String text, int caretPosition) {
        if (text.contains("#{") && text.indexOf("#{") < caretPosition) {
            return getActualInput(text, caretPosition).matches("[A-Za-z0-9_]+");
        }
        return false;
    }

    private String getActualInput(String text, int caretPosition) {
        int lastSpaceIdx = text.lastIndexOf(" ");
        if (lastSpaceIdx < 0 || lastSpaceIdx > caretPosition) {
            return text.substring(text.lastIndexOf("{") + 1, caretPosition);
        } else {
            return text.substring(text.lastIndexOf(" "), caretPosition).trim();
        }
    }

    private String[] getNameParts(String actual) {
        String[] nameParts = actual.split("\\.");
        if (nameParts.length == 2) {
            return nameParts;
        } else {
            return new String[]{nameParts[0], ""};
        }
    }


}

