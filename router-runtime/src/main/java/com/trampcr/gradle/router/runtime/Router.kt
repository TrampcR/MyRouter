package com.trampcr.gradle.router.runtime

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log

object Router {
    private const val TAG = "RouterTAG"
    private const val GENERATED_MAPPING = "com.trampcr.router.mapping.generated.RouterMapping"
    private var mappingMap: Map<String, String> = HashMap()

    fun init() {
        try {
            val clazz = Class.forName(GENERATED_MAPPING)
            val method = clazz.getMethod("get")
            val mapping = method.invoke(null) as Map<String, String>
            if (mapping.isNotEmpty()) {
                mapping.onEach {
                    Log.d(TAG, "init: mapping = ${it.key} -> ${it.value}")
                }
                mappingMap = mapping
            }
        } catch (e: Throwable) {
            Log.e(TAG, "init: error $e")
        }
    }

    fun go(context: Context?, url: String?) {
        if (context == null || url.isNullOrEmpty()) {
            Log.e(TAG, "go: context is null or url is null or Empty")
            return
        }

        // URL 匹配
        val uri = Uri.parse(url)
        val scheme = uri.scheme
        val host = uri.host
        val path = uri.path
        var targetClassName = ""

        mappingMap.onEach {
            val rUri = Uri.parse(it.key)
            val rScheme = rUri.scheme
            val rHost = rUri.host
            val rPath = rUri.path

            if (rScheme == scheme && rHost == host && rPath == path) {
                targetClassName = it.value
            }
        }

        if (targetClassName.isEmpty()) {
            Log.d(TAG, "go: targetClassName is empty")
            return
        }

        // 解析 URL 中的参数，并封装成一个 Bundle
        val bundle = Bundle()
        val query = uri.query
        query?.let {
            if (it.length >= 3) {
                val args = it.split("&")
                args.onEach { arg ->
                    val splits = arg.split("=")
                    bundle.putString(splits[0], splits[1])
                }
            }
        }

        // 打开对应的 Activity，并传入参数
        try {
            val clazz = Class.forName(targetClassName)
            val intent = Intent(context, clazz)
            intent.putExtras(bundle)
            context.startActivity(intent)
        } catch (e: Throwable) {
            Log.e(TAG, "go: startActivity error = $e")
        }
    }
}