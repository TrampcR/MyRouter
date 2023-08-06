package com.trampcr.router.gradle

import java.util.jar.JarEntry
import java.util.jar.JarFile

class RouterMappingCollector {
    private static final String PACKAGE_NAME = 'com\\trampcr\\router\\mapping'
    private static final String CLASS_NAME_PREFIX = 'RouterMapping_'
    private static final String CLASS_FILE_SUFFIX = '.class'

    private Set<String> mappingClassNames = new HashSet<>()

    /**
     * 获取收集好的映射表类名
     * @return
     */
    Set<String> getMappingClassNames() {
        return mappingClassNames
    }

    /**
     * 收集 class 文件或者 class 文件目录中的映射表类
     * @param classFile
     */
    void collect(File classFile) {
        if (classFile == null || !classFile.exists()) {
            return
        }

        if (classFile.isFile()) {
            println("Collector >>> collect class is File path = ${classFile.absolutePath}, name = ${classFile.name}")
            if (classFile.absolutePath.contains(PACKAGE_NAME) && classFile.name.startsWith(CLASS_NAME_PREFIX)
                    && classFile.name.endsWith(CLASS_FILE_SUFFIX)) {
                String className = classFile.name.replace(CLASS_FILE_SUFFIX, "")
                println("Collector >>> collect class is added")
                mappingClassNames.add(className)
            }
        } else {
            println("Collector >>> collect class is Director path = ${classFile.absolutePath}")
            classFile.listFiles().each {
                println("Collector >>> collect class is child file path = ${it.absolutePath}")
                collect(it)
            }
        }
    }

    /**
     * 收集 Jar 包中的映射类的类名
     * @param jarFile
     */
    void collectFromJarFile(File jarFile) {
        if (jarFile == null || !jarFile.exists()) {
            return
        }

        Enumeration enumeration = new JarFile(jarFile).entries()
        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry) enumeration.nextElement()
            String entryName = jarEntry.name
            if (entryName.contains(PACKAGE_NAME) && entryName.startsWith(CLASS_NAME_PREFIX)
                    && entryName.endsWith(CLASS_FILE_SUFFIX)) {
                String className = entryName.replace(PACKAGE_NAME, "")
                        .replace("/", "").replace(CLASS_FILE_SUFFIX, "")
                println("Collector >>> collectFromJarFile class is added")
                mappingClassNames.add(className)
            }
        }
    }
}