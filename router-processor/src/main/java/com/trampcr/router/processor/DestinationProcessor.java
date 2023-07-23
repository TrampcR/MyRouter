package com.trampcr.router.processor;

import com.google.auto.service.AutoService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.trampcr.router.annotations.Destination;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

@AutoService(Processor.class)
public class DestinationProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // 避免重复调用
        if (roundEnv.processingOver()) {
            return false;
        }
        System.out.println("Processor >>> process start ...");
        System.out.println("Processor >>> process annotations = " + annotations);

        Set<Element> allElements = (Set<Element>) roundEnv.getElementsAnnotatedWith(Destination.class);
        System.out.println("Processor >>> process allElements size = " + allElements.size());

        if (allElements.size() < 1) {
            return false;
        }

        String className = "RouterMapping_" + System.currentTimeMillis();
        String rootDir = processingEnv.getOptions().get("root_project_dir");
        System.out.println("Processor >>> process rootDir = " + rootDir);

        JsonArray jsonArray = new JsonArray();
        StringBuilder builder = new StringBuilder();
        builder.append("package com.trampcr.router.mapping;\n\n");
        builder.append("import java.util.HashMap;\n");
        builder.append("import java.util.Map;\n\n");
        builder.append("public class ").append(className).append(" {\n");
        builder.append("    public static Map<String, String> get() {\n");
        builder.append("        Map<String, String> mapping = new HashMap<>();\n\n");

        for (Element element: allElements) {
            TypeElement typeElement = (TypeElement) element;
            Destination destination = typeElement.getAnnotation(Destination.class);
            if (destination == null) {
                continue;
            }
            String url = destination.url();
            String description = destination.description();
            String realPath = typeElement.getQualifiedName().toString();
            System.out.println("Processor >>> process url = " + url);
            System.out.println("Processor >>> process description = " + description);
            System.out.println("Processor >>> process realPath = " + realPath);
            builder.append("        mapping.put(\"").append(url).append("\", \"").append(realPath).append("\");\n");

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("url", url);
            jsonObject.addProperty("description", description);
            jsonObject.addProperty("realPath", realPath);
            jsonArray.add(jsonObject);
        }

        builder.append("\n");
        builder.append("        return mapping;\n");
        builder.append("    }\n");
        builder.append("}");

        String mappingClassName = "com.trampcr.router.mapping." + className;
        System.out.println("Processor >>> process class = \n" + builder);
        // 写 class 文件到本地
        try {
            JavaFileObject fileObject = processingEnv.getFiler().createSourceFile(mappingClassName);
            Writer writer = fileObject.openWriter();
            writer.write(builder.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 写 JSON 文件到本地
        File rootDirFile = new File(rootDir);
        if (!rootDirFile.exists()) {
            throw new RuntimeException("rootDir is no exist");
        }

        File mappingFileDir = new File(rootDirFile, "router_mapping");
        if (!mappingFileDir.exists()) {
            mappingFileDir.mkdir();
        }

        File mappingFile = new File(mappingFileDir, "mapping_" + System.currentTimeMillis() + ".json");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(mappingFile));
            writer.write(jsonArray.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Processor >>> process end ...");
        return false;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        System.out.println("Processor >>> getSupportedAnnotationTypes start ...");
        return Collections.singleton(Destination.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }
}
