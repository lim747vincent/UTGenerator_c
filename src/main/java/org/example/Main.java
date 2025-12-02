import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws Exception {
        args = new String[]{"C:\\work\\source_code\\AGD\\backend\\build\\my\\com\\eprotea\\iss\\rpt\\D70037.class"};
        if (args.length == 0) {
            System.out.println("Usage: java -jar UTGenerator.jar <path-to-class-file>");
            return;
        }

        File classFile = new File(args[0]);
        if (!classFile.exists() || !classFile.getName().endsWith(".class")) {
            System.out.println("File not found or not a .class file: " + args[0]);
            return;
        }

        // Find package root: folder above top-level package (assume 'com')
        File parent = classFile.getParentFile();
        File rootFolder = null;
        while (parent != null) {
            if (new File(parent, "my").exists()) { // detect top-level package folder
                rootFolder = parent;
                break;
            }
            parent = parent.getParentFile();
        }
        if (rootFolder == null) {
            System.out.println("Cannot determine package root from path: " + args[0]);
            return;
        }

        // Compute fully qualified class name (FQCN)
        String fqcn = classFile.getCanonicalPath()
                .replace(rootFolder.getCanonicalPath() + File.separator, "")
                .replace(File.separatorChar, '.')
                .replace(".class", "");

        System.out.println("Detected class: " + fqcn);

        // Load class dynamically
        URLClassLoader loader = new URLClassLoader(new URL[]{rootFolder.toURI().toURL()});
        Class<?> clazz = loader.loadClass(fqcn);

        // Generate test template
        generateTestTemplate(clazz);
    }

    private static void generateTestTemplate(Class<?> clazz) throws Exception {
        String simpleName = clazz.getSimpleName();
        String instanceName = Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
        String testClassName = simpleName + "Testng";

        StringBuilder sb = new StringBuilder();

        sb
                .append("import org.powermock.api.mockito.PowerMockito; \n")
                .append("import org.powermock.core.classloader.annotations.PrepareForTest; \n")
                .append("\n")
                .append("import org.testng.annotations.*;\n")
                .append("\n")
                .append("@PrepareForTest({" + simpleName + ".class})\n");


        sb.append("public class ").append(testClassName).append("extends UnitTestingMaster {\n\n")
                .append("    private ").append(simpleName).append(" ").append(testClassName).append(";\n\n")
                .append("    @BeforeClass ").append("\n")
                .append("    public void setUp() {\n")
                .append("        ").append(testClassName).append(" = PowerMockito.spy( new ").append(simpleName).append("());\n")
                .append("    }\n\n");

        for (Method method : clazz.getDeclaredMethods()) {
            if (!Modifier.isPublic(method.getModifiers())) continue;

            String methodName = method.getName();
            String capitalized = capitalize(methodName);

            // Generate DataProvider
            sb.append("    @DataProvider(name = \"").append(methodName).append("Provider\")\n")
                    .append("    public Object[][] ").append(methodName).append("Provider() {\n")
                    .append("        return new Object[][] {\n")
                    .append("            {\"Testing for ").append(methodName).append(" execution\"},\n")
                    .append("        };\n")
                    .append("    }\n\n");

            // Generate Test method
            sb.append("    @Test(dataProvider = \"").append(methodName).append("Provider\", groups = \"").append(methodName + "Provider\")\n")
                    .append("    public void test").append(capitalized).append("(String testCase) throws Exception {\n")
                    .append("        // TODO: add test for ").append(methodName).append("\n")
                    .append("        resetInstance();\n")
                    .append("        System.out.println(testCase);\n")
                    .append("    }\n\n");
        }


        sb.append("    private void resetInstance() throws Exception {\n")
                .append("        ").append(testClassName).append(" = PowerMockito.spy( new ").append(simpleName).append("());\n")
                .append("    }\n\n");


        sb.append("}\n");

        // Write to file
        File outDir = new File("UTGenerator");
        outDir.mkdirs();
        File outFile = new File(outDir, testClassName + ".java");
        try (FileWriter writer = new FileWriter(outFile)) {
            writer.write(sb.toString());
        }

        System.out.println("Unit test template generated: " + outFile.getAbsolutePath());
    }

    private static String capitalize(String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

}