package com.antoshk.psi;

import com.antoshk.ELUtils;
import com.antoshk.EnumUtils;
import com.intellij.codeInsight.completion.CompletionUtilCore;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiEnumConstant;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class EnumReferenceContributor extends PsiReferenceContributor {
    
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(PsiElement.class),
            new PsiReferenceProvider() {
                @NotNull
                @Override
                public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                    if (element.getClass().getSimpleName().equals("ELVariableImpl")) {
                        String value = element.getText();
                        if (value != null) {
                            TextRange textRange;
                            if (value.contains(CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED)) {
                                textRange = new TextRange(0, value.indexOf(CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED));
                                value = value.substring(0, value.indexOf(CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED));
                            } else {
                                textRange = new TextRange(0, value.length());
                            }
                            
                            Map<ELUtils.Parts, String> enumNameParts = ELUtils.processElTree(element, value);
                            if (enumNameParts.size() == 2) {
                                PsiEnumConstant constant = EnumUtils.findEnumConstant(enumNameParts.get(ELUtils.Parts.ENUM_NAME),
                                    enumNameParts.get(ELUtils.Parts.CONST_NAME), element.getProject());
                                if (constant != null) {
                                    return new PsiReference[]{
                                        new EnumConstantReference(element, textRange, constant)
                                    };
                                }
                            } else if (enumNameParts.size() == 1) {
                                PsiClass enumClass = EnumUtils.findEnum(enumNameParts.get(ELUtils.Parts.ENUM_NAME), element.getProject());
                                if (enumClass != null) {
                                    return new PsiReference[]{new EnumReference(element, textRange, enumClass)};
                                }
                            }
                        }
                    }
                    return PsiReference.EMPTY_ARRAY;
                }
            });
        
    }
    
}
