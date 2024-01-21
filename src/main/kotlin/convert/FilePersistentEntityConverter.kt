package org.example.convert

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import org.example.entity.PersistentEntity
import org.example.entity.annotation.ManyToOne
import org.example.entity.annotation.OneToMany
import org.example.entity.annotation.OneToOne
import org.example.extention.checkIsEntity
import org.example.extention.checkIsList
import org.example.extention.getEntity
import org.example.extention.getListEntity
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.javaField


class FilePersistentEntityConverter {

    private val gson = GsonBuilder()
        .addSerializationExclusionStrategy(PersistentEntityExclusionStrategy())
        .create()

    private val jsonElementList = mutableMapOf<KClass<out PersistentEntity>, MutableList<JsonElement>>()

    fun serializeEntity(src: PersistentEntity): Map<KClass<out PersistentEntity>, MutableList<JsonElement>> {
        serialize(src)
        return jsonElementList
    }

    private fun serialize(src: PersistentEntity) {
        val annotations = parseFieldAnnotation(src)
        val json = gson.toJsonTree(src)
        if (annotations.isEmpty()) {
            jsonElementList.computeIfPresent(src::class) { _, value ->
                value.add(json)
                value
            }
            jsonElementList.putIfAbsent(src::class, mutableListOf(json))
        } else {
            annotations.forEach { annotation ->
                addFiledRelationship(annotation.value, json, src, annotation.key)
                jsonElementList.putIfAbsent(src::class, mutableListOf(json))
                if (annotation.value.annotationClass == OneToMany::class) {
                    for (fieldListElem in annotation.key.getListEntity(src)) {
                        serialize(fieldListElem)
                    }
                } else {
                    serialize(annotation.key.getEntity(src))
                }
            }
        }
    }

    fun deserialize() {

    }

    private fun parseFieldAnnotation(src: PersistentEntity): Map<KProperty1<out PersistentEntity, *>, Annotation> {
        val annotationMap = mutableMapOf<KProperty1<out PersistentEntity, *>, Annotation>()
        for (field in src::class.declaredMemberProperties) {
            for (annotation in field.javaField?.declaredAnnotations.orEmpty()) {
                when (annotation.annotationClass) {
                    OneToOne::class, ManyToOne::class -> {
                        field.checkIsEntity(src)
                        annotationMap.putIfAbsent(field, annotation)
                    }

                    OneToMany::class -> {
                        field.checkIsList(src)
                        annotationMap.putIfAbsent(field, annotation)
                    }
                }
            }
        }
        return annotationMap
    }

    private fun addFiledRelationship(
        a: Annotation,
        json: JsonElement,
        src: PersistentEntity,
        targetField: KProperty1<out PersistentEntity, *>
    ) {
        when (a.annotationClass) {
            OneToOne::class, ManyToOne::class ->
                json.asJsonObject.addProperty(
                    targetField.name,
                    targetField.getEntity(src).id.toString()
                )

            OneToMany::class -> {
                val entityList = targetField.getListEntity(src)
                val jsonArray = JsonArray()
                entityList.forEach { element ->
                    jsonArray.add(element.id.toString())
                }
                json.asJsonObject.add(targetField.name, jsonArray)
            }

        }
    }
}