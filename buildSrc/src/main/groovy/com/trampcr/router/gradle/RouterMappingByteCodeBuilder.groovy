package com.trampcr.router.gradle

import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class RouterMappingByteCodeBuilder implements Opcodes {
    public static final String CLASS_NAME = 'com/trampcr/router/mapping/generated/RouterMapping'

    static byte[] get(Set<String> allMappingNames) {
        // 1. 创建一个类
        // 2. 创建构造方法
        // 3. 创建 get 方法
        //  创建一个 Map
        //  塞入所有映射表内容
        //  返回 map

        // ClassWriter 用于修改或者生成一个类的接口
        // ClassWriter.COMPUTE_MAXS 表示局部变量栈帧的大小
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS)
        classWriter.visit(V1_8, ACC_PUBLIC, CLASS_NAME, null, "java/lang/Object", null)

        // MehtodVisitor 用于修改或者生成一个方法的接口
        // 创建一个构造方法
        MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null)
        // 开启字节码生成或者访问
        methodVisitor.visitCode()
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        methodVisitor.visitInsn(RETURN);
        methodVisitor.visitMaxs(1, 1);
        methodVisitor.visitEnd()

        // 创建 get 方法
        methodVisitor = classWriter.visitMethod(ACC_PUBLIC | ACC_STATIC, "get", "()Ljava/util/Map;", "()Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;", null)
        methodVisitor.visitCode()
        // 创建一个 Map
        methodVisitor.visitTypeInsn(NEW, "java/util/HashMap")
        methodVisitor.visitInsn(DUP)
        methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false)
        methodVisitor.visitVarInsn(ASTORE, 0)
        // 塞入所有映射表内容
        allMappingNames.each {
            methodVisitor.visitVarInsn(ALOAD, 0)
            methodVisitor.visitMethodInsn(INVOKESTATIC, "com/trampcr/router/mapping/$it", "get", "()Ljava/util/Map;", false)
            methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "putAll", "(Ljava/util/Map;)V", true)
        }
        // 返回 map
        methodVisitor.visitVarInsn(ALOAD, 0)
        methodVisitor.visitInsn(ARETURN)
        methodVisitor.visitMaxs(2, 2)
        methodVisitor.visitEnd()

        return classWriter.toByteArray()
    }
}