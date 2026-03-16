package io.github.neczpal.restdsl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JavaSpringIT {

    @TempDir
    Path tempDir;

    @Test
    public void testEndToEnd() throws Exception {
        // 1. Prepare test files with the correct RDSL syntax
        Path rsdlFile = tempDir.resolve("petstore.rdsl");
        String rsdlContent = """
                api Petstore {
                    version: "1.0.0"
                    base: "/api/v3"
                }
                model Pet {
                    id: Int
                    name: String
                }
                service PetService {
                    base: "/pet"
                    get getPetById {
                        path: "/{id}"
                        pathParams: {
                            id: Int
                        }
                        responses: {
                            200: Pet
                        }
                    }
                }
                """;
        Files.write(rsdlFile, rsdlContent.getBytes());

        Path serverOutputDir = tempDir.resolve("server");
        Path clientOutputDir = tempDir.resolve("client");
        String packageName = "io.github.neczpal.restdsl.petstore";

        // 2. Generate server and client code
        Main.main(new String[]{"-g", "spring", "-p", packageName, rsdlFile.toString(), serverOutputDir.toString()});
        Main.main(new String[]{"-g", "restclient", "-p", packageName, rsdlFile.toString(), clientOutputDir.toString()});

        // 3. Compile generated code
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null)) {
            List<File> serverFiles = Files.walk(serverOutputDir)
                    .filter(p -> p.toString().endsWith(".java"))
                    .map(Path::toFile)
                    .collect(Collectors.toList());
            Iterable<? extends javax.tools.JavaFileObject> serverCompilationUnits =
                    fileManager.getJavaFileObjectsFromFiles(serverFiles);
            compiler.getTask(null, fileManager, null, null, null, serverCompilationUnits).call();

            List<File> clientFiles = Files.walk(clientOutputDir)
                    .filter(p -> p.toString().endsWith(".java"))
                    .map(Path::toFile)
                    .collect(Collectors.toList());
            Iterable<? extends javax.tools.JavaFileObject> clientCompilationUnits =
                    fileManager.getJavaFileObjectsFromFiles(clientFiles);
            compiler.getTask(null, fileManager, null, null, null, clientCompilationUnits).call();
        }

        // 4. Load compiled classes
        URLClassLoader serverClassLoader = new URLClassLoader(new URL[]{serverOutputDir.toUri().toURL()});
        URLClassLoader clientClassLoader = new URLClassLoader(new URL[]{clientOutputDir.toUri().toURL(), serverOutputDir.toUri().toURL()});

        Class<?> petServiceApi = serverClassLoader.loadClass(packageName + ".PetServiceApi");
        Class<?> pet = serverClassLoader.loadClass(packageName + ".Pet");

        // 5. Create dummy implementation
        Object petImpl = java.lang.reflect.Proxy.newProxyInstance(
                serverClassLoader,
                new Class[]{petServiceApi},
                (_, method, args) -> {
                    if ("getPetById".equals(method.getName())) {
                        // The generated model has a constructor with all fields
                        Constructor<?> petConstructor = pet.getConstructor(Integer.class, String.class);
                        Object petInstance = petConstructor.newInstance(args[0], "Rex");
                        
                        // The generated interface expects a ResponseEntity
                        Class<?> responseEntity = Class.forName("org.springframework.http.ResponseEntity");
                        return responseEntity.getMethod("ok", Object.class).invoke(null, petInstance);
                    }
                    return null;
                });

        // 6. Invoke the method using reflection
        Method getPetByIdMethod = petServiceApi.getMethod("getPetById", Integer.class);

        Object result = getPetByIdMethod.invoke(petImpl, 1);

        // 7. Assert the results
        assertNotNull(result);
        Object responseBody = result.getClass().getMethod("getBody").invoke(result);
        assertNotNull(responseBody);

        assertEquals(1, pet.getMethod("id").invoke(responseBody));
        assertEquals("Rex", pet.getMethod("name").invoke(responseBody));
    }
}