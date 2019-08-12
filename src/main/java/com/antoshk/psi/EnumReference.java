package com.antoshk.psi;

import com.intellij.model.SymbolResolveResult;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

public class EnumReference extends PsiReferenceBase<PsiElement> {
    
    private PsiClass enumClass;
    
    public EnumReference(@NotNull PsiElement element, TextRange textRange, PsiClass enumClass) {
        super(element, textRange);
        this.enumClass = enumClass;
    }
    
    @Nullable
    @Override
    public PsiElement resolve() {
        return enumClass;
    }
    
    @NotNull
    @Override
    public Object[] getVariants() {
        return new ArrayList<>().toArray();
    }
    
}
