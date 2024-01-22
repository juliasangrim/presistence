package org.example.convert

import org.example.entity.PersistentEntity
import org.example.entity.annotation.ManyToOne
import org.example.entity.annotation.OneToMany
import org.example.entity.annotation.OneToOne
import org.example.extention.checkIsEntity
import org.example.extention.checkIsList
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.javaField

sealed class AnnotationUtils {

    companion object {
        fun <T : PersistentEntity> parseFieldAnnotation(clazz: KClass<T>): Map<KProperty1<out PersistentEntity, *>, Annotation> {
            val annotationMap = mutableMapOf<KProperty1<out PersistentEntity, *>, Annotation>()
            for (field in clazz.declaredMemberProperties) {
                for (annotation in field.javaField?.declaredAnnotations.orEmpty()) {
                    when (annotation.annotationClass) {
                        OneToOne::class, ManyToOne::class -> {
                            field.checkIsEntity()
                            annotationMap.putIfAbsent(field, annotation)
                        }

                        OneToMany::class -> {
                            field.checkIsList()
                            annotationMap.putIfAbsent(field, annotation)
                        }
                    }
                }
            }
            return annotationMap
        }
    }
}