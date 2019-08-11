package com.antoshk;

import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ELUtils {
    public static final String EL_VARIABLE = "ELVariableImpl";
    public static final String EL_EXPRESSION_HOLDER = "ELExpressionHolder";
    
    public static Map<Parts, String> processElTree(PsiElement current, String actual) {
        Map<Parts, String> parts = new HashMap<>();
        
        if (current instanceof LeafPsiElement && current.getParent().getClass().getSimpleName().equals(EL_VARIABLE)) {
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
    
    public enum Parts {
        ENUM_NAME, CONST_NAME;
    }
}
