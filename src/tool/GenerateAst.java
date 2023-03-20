package tool;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Incorrect Usage, please use this formatting: generate_ast <output directory>");
            System.exit(64);
        }
        String outputDirectory = args[0];
        defineAst(outputDirectory, "Expr", Arrays.asList(
                "Binary    : Expr left, Token operator, Expr right",
                "Grouping  : Expr expression",
                "Literal   : Object value",
                "Unary     : Token operator, Expr right"
        ));
    }

    private static void defineAst(String outputDirectory, String baseClassName, List<String> typeList) throws IOException {
        String output_path = outputDirectory + "/" + baseClassName + ".java";
        PrintWriter writer = new PrintWriter(output_path, "UTF-8");

        writer.println("package lox;");
        writer.println();
        writer.println("import java.util.List;");
        writer.println();
        writer.println("abstract class " + baseClassName + " {");

        for (String type : typeList) {
            String curClassName = type.split(":")[0].trim();
            String classFields = type.split(":")[1].trim();
            defineType(writer, baseClassName, curClassName, classFields);
        }

        writer.println("}");
        writer.close();
    }

    private static void defineType(PrintWriter writer, String baseClassName, String curClassName, String classFields) {
        writer.println("    static class " + curClassName + " extends " + baseClassName + " {");

        writer.println();
        String[] fields = classFields.split(", ");
        for (String field : fields) {
            writer.println("        final " + field + ";");
        }

        writer.println();

        // Produce the constructor
        writer.println("        " + curClassName + "(" + classFields + ") {");

        // Store the parameters in fields
        for (String field : fields) {
            String name = field.split(" ")[1];
            writer.println("            this." + name + " = " + name + ";");
        }

        writer.println("    }");
        writer.println(" }");

        // Define fields
    }
}
