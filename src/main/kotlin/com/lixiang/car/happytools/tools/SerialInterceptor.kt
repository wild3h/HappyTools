package com.lixiang.car.happytools.tools

import wu.seal.jsontokotlin.interceptor.IKotlinClassInterceptor
import wu.seal.jsontokotlin.model.classscodestruct.DataClass
import wu.seal.jsontokotlin.model.classscodestruct.KotlinClass
import java.io.Serializable

class SerialInterceptor: IKotlinClassInterceptor<KotlinClass> {
    override fun intercept(kotlinClass: KotlinClass): KotlinClass {
        if (kotlinClass is DataClass) {
            return kotlinClass.copy(parentClassTemplate = "Serializable")
        }
        return kotlinClass
    }
}