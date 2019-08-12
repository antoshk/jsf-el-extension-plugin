package com.antoshk.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiEnumConstant;
import com.intellij.psi.PsiReferenceBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class EnumConstantReference extends PsiReferenceBase<PsiElement> {
    
    PsiEnumConstant constant;
    
    public EnumConstantReference(@NotNull PsiElement element, TextRange textRange, PsiEnumConstant constant) {
        super(element, textRange);
        this.constant = constant;
    }
    
    @Nullable
    @Override
    public PsiElement resolve() {
        return constant;
    }
    
    @NotNull
    @Override
    public Object[] getVariants() {
        return new ArrayList<>().toArray();
    }
    
}
