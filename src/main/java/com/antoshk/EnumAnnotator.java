package com.antoshk;

import com.intellij.codeInsight.completion.CompletionUtilCore;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiEnumConstant;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class EnumAnnotator implements Annotator {
    
    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        
        if (element.getClass().getSimpleName().equals("ELVariableImpl")) {
            String value = element.getText();
            if (value != null) {
                if (value.contains(CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED)) {
                    value = value.substring(0, value.indexOf(CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED));
                }
                Map<ELUtils.Parts, String> enumNameParts = ELUtils.processElTree(element, value);
                if (enumNameParts.size() == 2) {
                    PsiClass enumClass = EnumUtils.findEnum(enumNameParts.get(ELUtils.Parts.ENUM_NAME), element.getProject());
                    if(enumClass != null) {
                        Annotation annotation = holder.createInfoAnnotation(element.getPrevSibling().getPrevSibling(), null);
                        annotation.setTextAttributes(DefaultLanguageHighlighterColors.STATIC_METHOD);
                        PsiEnumConstant constant = EnumUtils.findEnumConstant(enumClass, enumNameParts.get(ELUtils.Parts.CONST_NAME));
                        if (constant != null) {
                            annotation = holder.createInfoAnnotation(element, null);
                            annotation.setTextAttributes(DefaultLanguageHighlighterColors.CONSTANT);
                            annotation.setTooltip(Utils.createArgList(constant));
                        } else {
                            holder.createErrorAnnotation(element, "Unresolved enum constant name");
                        }
                    }
                } else if (enumNameParts.size() == 1) {
                    PsiClass enumClass = EnumUtils.findEnum(enumNameParts.get(ELUtils.Parts.ENUM_NAME), element.getProject());
                    if (enumClass != null) {
                        Annotation annotation = holder.createInfoAnnotation(element, null);
                        annotation.setTextAttributes(DefaultLanguageHighlighterColors.STATIC_METHOD);
                    }
                }
            }
        }
        
    }
    
}
