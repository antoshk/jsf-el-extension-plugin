package com.antoshk;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Utils {
    static Project getCurrentProject() {
        Project[] projects = ProjectManager.getInstance().getOpenProjects();
        for (Project project : projects) {
            Window window = WindowManager.getInstance().suggestParentWindow(project);
            if (window != null && window.isActive()) {
                return project;
            }
        }
        throw new RuntimeException("Cant obtain current project");
    }

    static Map<String, String> collectProperties(String fileName) {
        Project p = getCurrentProject();
        GlobalSearchScope scope = GlobalSearchScope.allScope(p);
        PsiFile[] files = FilenameIndex.getFilesByName(p, fileName, scope);
        if (files.length != 0) {
            return PropertyParser.parse(files[0]);
        }
        return new HashMap<>();
    }

    static Stream<PsiEnumConstant> extractEnumConstants(String classFullName) {
        Project p = getCurrentProject();
        GlobalSearchScope scope = GlobalSearchScope.allScope(p);
        PsiClass cl = JavaPsiFacade.getInstance(p).findClass(classFullName, scope);
        if (cl != null) {
            if (cl instanceof PsiCompiledElement) {
                cl = (PsiClass)((PsiCompiledElement)cl).getMirror(); // triggers decompilation
            }
            return Stream.of(cl.getFields()).filter(f -> f instanceof PsiEnumConstant).map(f -> (PsiEnumConstant) f);
        }
        return Stream.empty();
    }

    static String createArgList(PsiEnumConstant enumConstant){
        PsiExpressionList argumentList = enumConstant.getArgumentList();
        if (argumentList != null && !argumentList.isEmpty()) {
            List<String> args = Stream.of(argumentList).map(PsiElement::getText).collect(Collectors.toList());
            return String.join(",", args);
        }
        return "";
    }
}
