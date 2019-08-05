package com.antoshk;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ElCompletionContributor extends CompletionContributor {
    
    private static final Logger LOG = Logger.getLogger(ElCompletionContributor.class);
    
    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        String text = parameters.getPosition().getText();
        int caretPosition = parameters.getOffset() - parameters.getPosition().getTextOffset();
        String actual = getActualInput(text, caretPosition);
        Map<Parts, String> enumNameParts = processElTree(parameters, actual);
        
        if (enumNameParts.get(Parts.ENUM_NAME) != null && enumNameParts.get(Parts.CONST_NAME) != null) {
            String className = getEnumName(enumNameParts.get(Parts.ENUM_NAME));
            if (className != null) {
                Utils.extractEnumConstants(className)
                    .filter(enumConst -> enumConst.getName().startsWith(actual))
                    .forEach(enumConst -> {
                        LookupElementBuilder builder = LookupElementBuilder.create(enumConst.getName());
                        String args = Utils.createArgList(enumConst);
                        if (args.length() > 0) {
                            builder = builder.withTypeText(args);
                        }
                        result.withPrefixMatcher(actual).addElement(builder);
                    });
            }
        } else if (enumNameParts.get(Parts.ENUM_NAME) != null) {
            Map<String, String> enums = Utils.collectProperties("enumPlugin.properties");
            enums.entrySet()
                .stream()
                .filter(entry -> entry.getKey().startsWith(actual))
                .forEach(entry -> result.withPrefixMatcher(actual).addElement(LookupElementBuilder.create(entry.getKey())));
        }
    }
    
    private Map<Parts, String> processElTree(CompletionParameters parameters, String actual) {
        Map<Parts, String> parts = new HashMap<>();
        
        PsiElement current = parameters.getPosition();
        
        if (!current.getParent().toString().equals("ELExpressionHolder")) {
            current = current.getParent();
        }
        
        if (current.getPrevSibling() != null && current.getPrevSibling().getText().equals(".") && actual.matches("[A-Za-z0-9_]*")) {
            parts.put(Parts.CONST_NAME, current.getText());
            Optional.ofNullable(current.getPrevSibling().getPrevSibling()).ifPresent(elem -> parts.put(Parts.ENUM_NAME, elem.getText()));
        } else if (actual.matches("[A-Za-z0-9_]+")) {
            parts.put(Parts.ENUM_NAME, current.getText());
        }
        
        return parts;
    }
    
    private String getEnumName(String enumName) {
        if (enumName != null) {
            Map<String, String> enums = Utils.collectProperties("enumPlugin.properties");
            return enums.get(enumName);
        }
        return null;
    }
    
    private String getActualInput(String text, int caretPosition) {
        int lastSpaceIdx = text.lastIndexOf(" ");
        if (lastSpaceIdx < 0 || lastSpaceIdx > caretPosition) {
            return text.substring(0, caretPosition);
        } else {
            return text.substring(text.lastIndexOf(" "), caretPosition).trim();
        }
    }
    
    enum Parts {
        ENUM_NAME, CONST_NAME;
    }
    
}

