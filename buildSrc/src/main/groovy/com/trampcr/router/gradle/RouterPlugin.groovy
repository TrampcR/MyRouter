package com.trampcr.router.gradle

import com.android.build.api.transform.Transform
import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import groovy.json.JsonSlurper
import org.gradle.api.Plugin
import org.gradle.api.Project

class RouterPlugin implements Plugin<Project> {
    private RouterMappingTransform transform = new RouterMappingTransform()

    @Override
    void apply(Project project) {
        println("RouterPlugin >>> apply start ...")
        println("RouterPlugin >>> apply project = ${project.getName()}")

        // 注册 Transform
        if (project.plugins.hasPlugin(AppPlugin)) {
            AppExtension appExtension = project.extensions.getByType(AppExtension.class)
            Transform transform = new RouterMappingTransform()
            appExtension.registerTransform(transform)
        }

        // 注册 RouterExtension
        project.getExtensions().create("router", RouterExtension.class)

        // 使用 kapt 帮助用户自动传递信息给 注解处理器
        if (project.extensions.findByName("kapt") != null) {
            println("RouterPlugin >>> apply kapt = ${project.extensions.findByName("kapt")}")
            project.extensions.findByName("kapt").arguments {
                arg("root_project_dir", project.rootProject.projectDir.absolutePath)
            }
        }

        // 实现旧的产物自动清理
        project.clean.doFirst {
            File routerMappingFile = new File(project.rootProject.projectDir, "router_mapping")
            if (routerMappingFile.exists()) {
                routerMappingFile.deleteDir()
            }
        }

        if (!project.plugins.hasPlugin(AppPlugin)) {
            return
        }

        // 配置阶段之后获取 RouterExtension 配置
        project.afterEvaluate {
            RouterExtension extension = project["router"]
            println("RouterPlugin >>> apply wikiDir = ${extension.wikiDir}")

            project.tasks.findAll {
                it.name.startsWith("compile") && it.name.endsWith("JavaWithJavac")
            }.each {
                it.doLast {
                    File routerMappingDir = new File(project.rootProject.projectDir, "router_mapping")
                    if (!routerMappingDir.exists()) {
                        return
                    }

                    File[] childMappingFiles = routerMappingDir.listFiles()
                    if (childMappingFiles.length < 1) {
                        return
                    }

                    StringBuilder builder = new StringBuilder()
                    builder.append("# 页面文档\n\n")
                    childMappingFiles.each {
                        if (it.name.endsWith(".json")) {
                            JsonSlurper slurper = new JsonSlurper()
                            def content = slurper.parse(it)
                            content.each {
                                def url = it["url"]
                                def description = it["description"]
                                def realPath = it["realPath"]

                                builder.append("## $description\n")
                                builder.append("- $url\n")
                                builder.append("- $realPath\n\n")
                            }
                        }
                    }
                    File wikiFileDir = new File(extension.wikiDir)
                    if (!wikiFileDir.exists()) {
                        wikiFileDir.mkdir()
                    }

                    File mkFile = new File(wikiFileDir, "页面文档.md")
                    if (mkFile.exists()) {
                        mkFile.delete()
                    }

                    mkFile.write(builder.toString())
                }
            }
        }


    }
}