package org.example.convert

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import org.example.OneToOne
import org.example.entity.PersistentEntity
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.javaField

class FilePersistentEntityConverter {

    fun serialize(src: PersistentEntity): JsonElement {
        val gson = GsonBuilder()
            .addSerializationExclusionStrategy(PersistentEntityExclusionStrategy())
            .create()

        val json = gson.toJsonTree(src)
        for (field in src::class.declaredMemberProperties) {
            for (annotation in field.javaField?.declaredAnnotations.orEmpty()) {
                when (annotation.annotationClass) {
                    OneToOne::class ->
                        json.asJsonObject.addProperty(field.name, (field.getter.call(src) as PersistentEntity).id.toString())
                }
            }
        }

        return json
    }

    fun deserialize() {

    }
}