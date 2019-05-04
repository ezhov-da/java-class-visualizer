package ru.ezhov.jclsvis.core.domain;

import clsvis.model.Class_;

import java.util.*;

public class Package {
    private String name;
    private Set<String> packages = new HashSet<>();
    private Set<String> classNames = new HashSet<>();

    public Package(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Set<String> getPackageNames() {
        return packages;
    }

    public void addClassName(String className) {
        classNames.add(className);
    }

    public void addPackageName(String packageName) {
        packages.add(packageName);
    }


    public Set<String> getClassNames() {
        return classNames;
    }



    public Optional<String> getParentPackageName() {
        Optional<String> optional;
        int i = name.lastIndexOf('.');
        if (i == -1) {
            optional = Optional.empty();
        } else {
            optional = Optional.of(name.substring(0, i));
        }
        return optional;
    }
}
