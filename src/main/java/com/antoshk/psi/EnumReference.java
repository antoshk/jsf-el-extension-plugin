package com.antoshk.psi;

import com.antoshk.EnumUtils;
import com.antoshk.PropertyParser;
import com.antoshk.Utils;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EnumReference extends PsiReferenceBase<PsiElement> {
    
    private Project project;
    private String key;
    
    public EnumReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
        key = element.getText().substring(textRange.getStartOffset(), textRange.getEndOffset());
        project = element.getProject();
    }
    
    @Nullable
    @Override
    public PsiElement resolve() {
        return EnumUtils.findEnum(key, project);
    }
    
    @NotNull
    @Override
    public Object[] getVariants() {
        Map<String, String> enums = PropertyParser.collectProperties(Utils.ENUM_PROPERTY_FILENAME, project, false);
        List<LookupElement> variants = new ArrayList<>();
        for (Map.Entry<String, String> entry : enums.entrySet()) {
            PsiElement element = EnumUtils.findEnum(entry.getKey(), project);
            if (element != null) {
                variants.add(LookupElementBuilder.createWithSmartPointer(entry.getKey(), element));
            }
        }
        return variants.toArray();
    }
    
}
