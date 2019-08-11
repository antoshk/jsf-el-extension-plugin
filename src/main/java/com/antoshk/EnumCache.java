package com.antoshk;

import com.intellij.openapi.project.Project;

import java.util.HashMap;
import java.util.Map;

public class EnumCache {
    
    private static EnumCache enumCache;
    private Map<Project, Map<String, String>> cache = new HashMap<>();
    
    private EnumCache() {}
    
    public static EnumCache getInstance() {
        if (enumCache == null){
            enumCache = new EnumCache();
        }
        return enumCache;
    }
    
    public void updateCache(Project project, Map<String, String> enums){
        cache.put(project, enums);
    }
    
    public Map<String, String> getEnums(Project project){
        return cache.get(project);
    }
    
}
