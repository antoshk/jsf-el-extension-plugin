package com.antoshk.psi;

import com.antoshk.EnumUtils;
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

public class EnumConstantReference extends PsiReferenceBase<PsiElement> {
    private Project project;
    private String enumName;
    private String constantName;
    
    public EnumConstantReference(@NotNull PsiElement element, TextRange textRange, String enumName) {
        super(element, textRange);
        constantName = element.getText().substring(textRange.getStartOffset(), textRange.getEndOffset());
        this.enumName = enumName;
        project = element.getProject();
    }
    
    @Nullable
    @Override
    public PsiElement resolve() {
        return EnumUtils.findEnumConstant(enumName, constantName, project);
    }
    
    @NotNull
    @Override
    public Object[] getVariants() {
        List<LookupElement> variants = new ArrayList<>();
        String className = EnumUtils.getEnumFullClassName(enumName, project);
        EnumUtils.extractEnumConstants(className, project)
            .forEach(enumConst -> {
                LookupElementBuilder builder = LookupElementBuilder.create(enumConst);
                String args = Utils.createArgList(enumConst);
                if (args.length() > 0) {
                    builder = builder.withTypeText(args);
                }
                variants.add(builder);
            });
        return variants.toArray();
    }

}
