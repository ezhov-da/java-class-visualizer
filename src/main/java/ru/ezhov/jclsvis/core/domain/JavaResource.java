package ru.ezhov.jclsvis.core.domain;

import clsvis.model.Class_;

import java.util.*;

public class JavaResource {
    private Map<String, Class_> classes = new HashMap<>();
    private Map<String, Package> packages = new HashMap<>();
    private Map<String, Package> packagesRoot = new HashMap<>();

    private JavaResource() {
    }

    public static JavaResource from(Collection<Class_> classes) {
        return new JavaResource().init(classes);
    }

    private JavaResource init(Collection<Class_> classesCollection) {
        for (Class_ class_ : classesCollection) {
            fillClasses(class_);
            fillPackages(class_);
        }
        return this;
    }

    private void fillClasses(Class_ class_) {
        String originalClassName = class_.getOriginalTypeName();
        classes.putIfAbsent(originalClassName, class_);
    }

    private void fillPackages(Class_ class_) {
        String originalClassName = class_.getOriginalTypeName();
        String packageName = class_.getPackageName();
        createPackages(packageName);
        buildPackageHierarchy(packageName);
        Package aPackage = packages.get(packageName);
        aPackage.addClassName(originalClassName);
        Optional<String> parentPackageName = aPackage.getParentPackageName();
        if (parentPackageName.isPresent()) {
            Package aPackageParent = packages.get(parentPackageName.get());
            aPackageParent.addPackageName(packageName);
        }
    }

    private void createPackages(String packageName) {
        Package aPackage = packages.get(packageName);
        if (aPackage == null) {
            aPackage = new Package(packageName);
            packages.put(packageName, aPackage);
        }
        Optional<String> parentPackageName = aPackage.getParentPackageName();
        if (parentPackageName.isPresent()) {
            createPackages(parentPackageName.get());
        } else {
            packagesRoot.putIfAbsent(packageName, aPackage);
        }
    }

    private void buildPackageHierarchy(String packageName) {
        Package aPackage = packages.get(packageName);
        Optional<String> parentPackageName = aPackage.getParentPackageName();
        if (parentPackageName.isPresent()) {
            Package aPackageParent = packages.get(parentPackageName.get());
            aPackageParent.addPackageName(packageName);
            buildPackageHierarchy(parentPackageName.get());
        }
    }

    public Class_ getClassByName(String name) {
        return classes.get(name);
    }

    public Package getPackageByName(String name) {
        return packages.get(name);
    }

    public Collection<Package> getRootPackages() {
        return packagesRoot.values();
    }
}
