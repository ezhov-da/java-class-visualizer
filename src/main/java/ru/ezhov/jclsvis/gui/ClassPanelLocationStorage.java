package ru.ezhov.jclsvis.gui;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ClassPanelLocationStorage {
    private Map<String, ClassPanel> classPanelMap = new HashMap<>();

    public void add(String clazz, ClassPanel classPanel) {
        classPanelMap.put(clazz, classPanel);
    }

    public ClassPanel get(String clazz) {
        return classPanelMap.get(clazz);
    }

    public Collection<ClassPanel> allValues() {
        return classPanelMap.values();
    }

    public Map<String, ClassPanel> all() {
        return classPanelMap;
    }
}
