package io.github.neczpal.restdsl;

public class Main {
    public static void main(String[] args) {
        String generatorName = null;
        String inputFile = null;
        String outputDirectory = ".";
        String packageName = null;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-g":
                    if (i + 1 < args.length) {
                        generatorName = args[++i];
                    }
                    break;
                case "-p":
                    if (i + 1 < args.length) {
                        packageName = args[++i];
                    }
                    break;
                default:
                    if (inputFile == null) {
                        inputFile = args[i];
                    } else {
                        outputDirectory = args[i];
                    }
                    break;
            }
        }

        if (generatorName == null || inputFile == null) {
            System.out.println("Usage: java -jar rest-dsl.jar -g <generator-name> [-p <package-name>] <input-file.rsdl> [output-directory]");
            return;
        }

        try {
            RestDslGenerator.generate(inputFile, generatorName, packageName, outputDirectory);
            System.out.println("Successfully generated " + generatorName + " spec to " + outputDirectory);
        } catch (Exception e) {
            System.err.println("Error generating from file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
