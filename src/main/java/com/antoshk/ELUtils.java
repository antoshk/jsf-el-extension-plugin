package com.antoshk;

import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ELUtils {
    
    public static final String EL_VARIABLE = "ELVariableImpl";
    public static final String EL_SELECT_EXPRESSION = "ELSelectExpressionImpl";
    public static final String EL_EXPRESSION_HOLDER = "ELExpressionHolder";
    
    public static Map<Parts, String> processElTree(PsiElement current, String actual) {
        Map<Parts, String> parts = new HashMap<>();
        
        if (current instanceof LeafPsiElement && current.getParent().getClass().getSimpleName().equals(EL_VARIABLE)) {
            current = current.getParent();
        }
        Optional<PsiElement> prevWrap = getPrevVar(current);
        Optional<PsiElement> prevPrevWrap = Optional.empty();
        if (prevWrap.isPresent()) {
            PsiElement prev = prevWrap.get();
            if(prev.getClass().getSimpleName().equals(EL_SELECT_EXPRESSION) && prev.getChildren().length == 2){
                prevWrap = Optional.of(prev.getChildren()[1]);
                prevPrevWrap = Optional.of(prev.getChildren()[0]);
            } else {
                prevPrevWrap = getPrevVar(prevWrap.get());
            }
        }
        
        if (prevWrap.isPresent() && prevWrap.get().getText().matches("[A-Z0-9_]+") && prevPrevWrap.isPresent()) {
            parts.put(Parts.CONST_PARAM, actual);
            parts.put(Parts.CONST_NAME, prevWrap.get().getText());
            parts.put(Parts.ENUM_NAME, prevPrevWrap.get().getText());
        } else if (prevWrap.isPresent() && actual.matches("[A-Z0-9_]*")) {
            parts.put(Parts.CONST_NAME, actual);
            parts.put(Parts.ENUM_NAME, prevWrap.get().getText());
        } else if (actual.matches("[A-Za-z0-9_]+")) {
            parts.put(Parts.ENUM_NAME, current.getText());
        }
        return parts;
    }
    
    public static Optional<PsiElement> getPrevVar(PsiElement current) {
        if (current.getPrevSibling() != null && current.getPrevSibling().getText().equals(".")) {
            return Optional.ofNullable(current.getPrevSibling().getPrevSibling());
        }
        return Optional.empty();
    }
    
    public static Optional<PsiElement> getPrevVar(PsiElement current, int depth) {
        Optional<PsiElement> result = getPrevVar(current);
        depth--;
        while (depth > 0) {
            if (result.isPresent()) {
                result = getPrevVar(result.get());
            }
            depth--;
        }
        return result;
    }
    
    public enum Parts {
        ENUM_NAME, CONST_NAME, CONST_PARAM;
    }
    
}
