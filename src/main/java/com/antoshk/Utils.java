package com.antoshk;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiEnumConstant;
import com.intellij.psi.PsiExpressionList;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Utils {
    
    public static final String ENUM_PROPERTY_FILENAME = "enumPlugin.properties";
    
    public static String createArgList(PsiEnumConstant enumConstant) {
        PsiExpressionList argumentList = enumConstant.getArgumentList();
        if (argumentList != null) {
            List<String> args = Stream.of(argumentList).map(PsiElement::getText).collect(Collectors.toList());
            return String.join(",", args);
        }
        return "";
    }
    
}
