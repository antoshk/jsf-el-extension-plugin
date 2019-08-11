package com.antoshk;

import com.intellij.lang.properties.psi.PropertiesFile;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PropertyParser {
    
    static Map<String, String> parse(List<PsiFile> files) {
        Map<String, String> result = new HashMap<>();
        for (PsiFile file : files) {
            PropertiesFile propFile = (PropertiesFile) file;
            propFile.getProperties().forEach(prop -> result.put(prop.getKey(), prop.getValue()));
        }
        return result;
    }
    
    public static Map<String, String> collectProperties(String fileName, Project project, boolean useCache) {
        Map<String, String> result;
        if (useCache) {
            result = EnumCache.getInstance().getEnums(project);
            if (result != null) {
                return result;
            }
        }
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);
        PsiFile[] files = FilenameIndex.getFilesByName(project, fileName, scope);
        if (files.length != 0) {
            result = parse(Arrays.asList(files));
            EnumCache.getInstance().updateCache(project, result);
            return result;
        }
        return new HashMap<>();
    }
    
}
