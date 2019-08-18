package com.antoshk;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ElCompletionContributor extends CompletionContributor {
    
    private static final Logger LOG = Logger.getLogger(ElCompletionContributor.class);
    
    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        String text = parameters.getPosition().getText();
        Project project = parameters.getPosition().getProject();
        int caretPosition = parameters.getOffset() - parameters.getPosition().getTextOffset();
        String actual = getActualInput(text, caretPosition);
        Map<ELUtils.Parts, String> enumNameParts = ELUtils.processElTree(parameters.getPosition(), actual);
        
        if (enumNameParts.size() == 3) {
            String className = EnumUtils.getEnumFullClassName(enumNameParts.get(ELUtils.Parts.ENUM_NAME), project);
            if (className != null) {
                EnumUtils.extractEnumConstFields(className, project)
                    .filter(enumField -> enumField.getName().startsWith(actual))
                    .forEach(enumConst -> {
                        LookupElementBuilder builder = LookupElementBuilder.create(enumConst.getName());
                        result.withPrefixMatcher(actual).addElement(builder);
                    });
                result.stopHere();
            }
        } else if (enumNameParts.size() == 2) {
            String className = EnumUtils.getEnumFullClassName(enumNameParts.get(ELUtils.Parts.ENUM_NAME), project);
            if (className != null) {
                EnumUtils.extractEnumConstants(className, project)
                    .filter(enumConst -> enumConst.getName().startsWith(actual))
                    .forEach(enumConst -> {
                        LookupElementBuilder builder = LookupElementBuilder.create(enumConst.getName());
                        String args = Utils.createArgList(enumConst);
                        if (args.length() > 0) {
                            builder = builder.withTypeText(args);
                        }
                        result.withPrefixMatcher(actual).addElement(builder);
                    });
                result.stopHere();
            }
        } else if (enumNameParts.get(ELUtils.Parts.ENUM_NAME) != null) {
            Map<String, String> enums = PropertyParser.collectProperties(Utils.ENUM_PROPERTY_FILENAME, project, false);
            enums.entrySet()
                .stream()
                .filter(entry -> entry.getKey().startsWith(actual))
                .forEach(entry -> result.withPrefixMatcher(actual).addElement(LookupElementBuilder.create(entry.getKey())));
        }
    }
    
    private String getActualInput(String text, int caretPosition) {
        int lastSpaceIdx = text.lastIndexOf(" ");
        if (lastSpaceIdx < 0 || lastSpaceIdx > caretPosition) {
            return text.substring(0, caretPosition);
        } else {
            return text.substring(text.lastIndexOf(" "), caretPosition).trim();
        }
    }
    
}

