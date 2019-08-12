package com.antoshk;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiCompiledElement;
import com.intellij.psi.PsiEnumConstant;
import com.intellij.psi.PsiField;
import com.intellij.psi.search.GlobalSearchScope;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.stream.Stream;

public class EnumUtils {
    
    private static final Logger log = Logger.getLogger(EnumUtils.class);
    
    public static Stream<PsiEnumConstant> extractEnumConstants(String classFullName, Project project) {
        if (classFullName != null && classFullName.length() > 0) {
            GlobalSearchScope scope = GlobalSearchScope.allScope(project);
            PsiClass cl = JavaPsiFacade.getInstance(project).findClass(classFullName, scope);
            if (cl != null) {
                if (cl instanceof PsiCompiledElement) {
                    cl = (PsiClass) ((PsiCompiledElement) cl).getMirror(); // triggers decompilation
                }
                return Stream.of(cl.getFields()).filter(f -> f instanceof PsiEnumConstant).map(f -> (PsiEnumConstant) f);
            }
        }
        return Stream.empty();
    }
    
    public static PsiClass findEnum(String enumName, Project project) {
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);
        String enumFullClassname = getEnumFullClassName(enumName, project);
        if (enumFullClassname != null && enumFullClassname.length() > 0) {
            return JavaPsiFacade.getInstance(project).findClass(enumFullClassname, scope);
        }
        return null;
    }
    
    public static String getEnumFullClassName(String enumName, Project project) {
        if (enumName != null) {
            Map<String, String> enums = PropertyParser.collectProperties(Utils.ENUM_PROPERTY_FILENAME, project, true);
            if (enums.get(enumName) == null) {
                enums = PropertyParser.collectProperties(Utils.ENUM_PROPERTY_FILENAME, project, false);
            }
            return enums.get(enumName);
        }
        return null;
    }
    
    public static PsiEnumConstant findEnumConstant(String enumName, String enumConstantName, Project project) {
        PsiClass enumClass = findEnum(enumName, project);
        if (enumClass != null) {
            if (enumClass instanceof PsiCompiledElement) {
                enumClass = (PsiClass) ((PsiCompiledElement) enumClass).getMirror(); // triggers decompilation
            }
            for (PsiField field : enumClass.getFields()) {
                if (field instanceof PsiEnumConstant && field.getName() != null && field.getName().equals(enumConstantName)) {
                    return (PsiEnumConstant) field;
                }
            }
        }
        return null;
    }
    
    public static boolean isEnum(String enumName, Project project){
        return getEnumFullClassName(enumName, project) != null;
    }
    
    public static boolean isEnumConstant(String enumName, String constantName, Project project){
        return getEnumFullClassName(enumName, project) != null;
    }
    
}
