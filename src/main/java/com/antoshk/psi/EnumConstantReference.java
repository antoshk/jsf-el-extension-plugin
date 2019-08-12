package com.antoshk.psi;

import com.intellij.model.SymbolResolveResult;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiEnumConstant;
import com.intellij.psi.PsiReferenceBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

public class EnumConstantReference extends PsiReferenceBase<PsiElement> {
    
    PsiEnumConstant constant;
    
    public EnumConstantReference(@NotNull PsiElement element, TextRange textRange, PsiEnumConstant constant) {
        super(element, textRange);
        this.constant = constant;
    }
    
    @Nullable
    @Override
    public PsiElement resolve() {
        //return EnumUtils.findEnumConstant(enumName, constantName, project);
        return constant;
    }
    
    @NotNull
    @Override
    public Object[] getVariants() {
//        List<LookupElement> variants = new ArrayList<>();
//        String className = EnumUtils.getEnumFullClassName(enumName, project);
//        EnumUtils.extractEnumConstants(className, project)
//            .forEach(enumConst -> {
//                LookupElementBuilder builder = LookupElementBuilder.create(enumConst);
//                String args = Utils.createArgList(enumConst);
//                if (args.length() > 0) {
//                    builder = builder.withTypeText(args);
//                }
//                variants.add(builder);
//            });
        return new ArrayList<>().toArray();
    }

}
