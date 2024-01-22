package org.example.convert

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.example.entity.PersistentEntity
import org.example.entity.PersistentEntityManager
import org.example.entity.annotation.ManyToOne
import org.example.entity.annotation.OneToMany
import org.example.entity.annotation.OneToOne
import org.example.extention.getEntity
import org.example.extention.getListEntity
import java.util.UUID
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.jvmErasure


class FilePersistentEntityConverter(
    private val entityManager: PersistentEntityManager
) {

    private val gson = GsonBuilder()
        .addSerializationExclusionStrategy(PersistentEntityExclusionStrategy())
        .addDeserializationExclusionStrategy(PersistentEntityExclusionStrategy())
        .setLenient()
        .create()

    private val jsonElementList = mutableMapOf<KClass<out PersistentEntity>, MutableList<JsonElement>>()

    fun serializeEntity(src: PersistentEntity): Map<KClass<out PersistentEntity>, MutableList<JsonElement>> {
        serialize(src)
        return jsonElementList
    }

    private fun serialize(src: PersistentEntity) {
        val annotations = AnnotationUtils.parseFieldAnnotation(src::class)
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

    fun <T : PersistentEntity> deserialize(src: JsonObject?, clazz: KClass<T>): T? {
        if (src == null) {
            return null
        }

        val entity = gson.fromJson(src, clazz.java)
        val annotations = AnnotationUtils.parseFieldAnnotation(clazz)
        return if (annotations.isEmpty()) {
            entity
        } else {
            for (annotation in annotations) {
                when (annotation.value.annotationClass) {
                    OneToOne::class, ManyToOne::class -> {
                        val refId = src[annotation.key.name].asString
                        val refClass = annotation.key.javaField?.type
                        if (refClass != null && PersistentEntity::class.java.isAssignableFrom(refClass)) {
                            val refEntity = entityManager.get(UUID.fromString(refId), (refClass as Class<T>).kotlin)
                            (annotation.key as KMutableProperty1<*, *>).setter.call(entity, refEntity)
                        }
                    }
                    OneToMany::class -> {
                        val refEntities = mutableListOf<T>()
                        for (refId in src[annotation.key.name].asJsonArray) {
                            val refClass = annotation.key.getter.returnType.arguments[0].type?.jvmErasure?.java
                            if (refClass != null && PersistentEntity::class.java.isAssignableFrom(refClass)) {
                                val refEntity = entityManager.get(UUID.fromString(refId.asString), (refClass as Class<T>).kotlin)
                                if (refEntity != null) {
                                    refEntities.add(refEntity)
                                }
                            }
                        }
                        (annotation.key as KMutableProperty1<*, *>).setter.call(entity, refEntities)
                    }
                }
            }
            entity
        }
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