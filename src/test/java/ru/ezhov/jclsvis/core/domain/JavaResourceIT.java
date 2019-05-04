package ru.ezhov.jclsvis.core.domain;

import clsvis.model.Class_;
import clsvis.process.importer.BaseProjectImporter;
import clsvis.process.importer.CompiledClassImporter;
import org.junit.Test;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.*;

public class JavaResourceIT {

    @Test
    public void fromOk() {
        String path = "D:\\programmer\\java-library\\image4j-0.7.2.jar";

        BaseProjectImporter baseProjectImporter = new BaseProjectImporter();
        baseProjectImporter.importProject(Collections.singletonList(new File(path)));
        CompiledClassImporter classImporter = baseProjectImporter.getClassImporter();

        Collection<Class_> importedClasses = classImporter.getImportedClasses();

        JavaResource javaResource = JavaResource.from(importedClasses);
    }
}