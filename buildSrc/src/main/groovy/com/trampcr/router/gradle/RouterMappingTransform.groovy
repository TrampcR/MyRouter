package com.trampcr.router.gradle

import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils

class RouterMappingTransform extends Transform {

    /**
     * 当前 Transform 名称
     * @return
     */
    @Override
    String getName() {
        return "RouterMappingTransform"
    }

    /**
     * 告知编译器，当前 Transform 需要消费的输入类型，这里是 CLASS 类型
     * @return
     */
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    /**
     * 告知编译器，当前 Transform 需要收集的范围
     * @return
     */
    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    /**
     * 是否支持增量，一般返回 false
     * @return
     */
    @Override
    boolean isIncremental() {
        return false
    }

    /**
     * 所有 class 收集好后，会被打包传入此方法
     * @param transformInvocation the invocation object containing the transform inputs.
     * @throws TransformException
     * @throws InterruptedException
     * @throws IOException
     */
    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        // 1. 遍历所有的 Input
        // 2. 对 Input 进行二次处理
        // 3. 将 Input 拷贝到目标目录

        RouterMappingCollector routerMappingCollector = new RouterMappingCollector()

        transformInvocation.inputs.each {
            // 把文件夹类型的输入拷贝到目标目录
            it.directoryInputs.each { directoryInput ->
                def destDir = transformInvocation.outputProvider.getContentLocation(
                        directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY
                )
                routerMappingCollector.collect(directoryInput.file)
                FileUtils.copyDirectory(directoryInput.file, destDir)
            }

            // 把 jar 类型的输入拷贝到目标目录
            it.jarInputs.each {jarInput ->
                def destDir = transformInvocation.outputProvider.getContentLocation(
                        jarInput.name, jarInput.contentTypes, jarInput.scopes, Format.JAR
                )
                routerMappingCollector.collectFromJarFile(jarInput.file)
                FileUtils.copyFile(jarInput.file, destDir)
            }
        }
        println("${getName()} >>> collect all className is ${routerMappingCollector.mappingClassNames}")
    }
}