package clsvis.process.importer;

import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

public class BaseProjectImporterIT {

    @Test
    public void importProjectOk() {
        String path = "D:\\programmer\\java-library\\image4j-0.7.2.jar";

        BaseProjectImporter baseProjectImporter = new BaseProjectImporter();
        baseProjectImporter.importProject(Collections.singletonList(new File(path)));
        CompiledClassImporter classImporter = baseProjectImporter.getClassImporter();

        System.out.println(classImporter.getImportedClasses().size());
    }
}