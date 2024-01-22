package org.example.extention

import org.example.entity.PersistentEntity
import kotlin.reflect.KProperty1
import kotlin.reflect.full.isSupertypeOf
import kotlin.reflect.full.starProjectedType

fun KProperty1<out PersistentEntity, *>.checkIsList() {
    val actualClass = this.getter.returnType
    if (!List::class.starProjectedType.isSupertypeOf(actualClass)) {
        error("Wrong field type for One to Many relationship.")
    } else {
        val listArgumentClass = actualClass.arguments[0].type ?: error("Wrong list type for One to Many relationship.")
        if (!PersistentEntity::class.starProjectedType.isSupertypeOf(listArgumentClass)){
            error("Wrong list type for One to Many relationship.")
        }
    }
}

fun KProperty1<out PersistentEntity, *>.checkIsEntity() {
    val actualClass = this.getter.returnType
    if (!PersistentEntity::class.starProjectedType.isSupertypeOf(actualClass))
        error("Wrong field type for One to One relationship.")
}

fun KProperty1<out PersistentEntity, *>.getEntity(src: PersistentEntity): PersistentEntity {
    val elem = this.getter.call(src)
    if (elem is PersistentEntity) {
        return elem
    } else error("Wrong field type for One to One relationship.")
}

fun KProperty1<out PersistentEntity, *>.getListEntity(src: PersistentEntity): List<PersistentEntity> {
    val elems = this.getter.call(src)
    if (elems is List<*>) {
        val persistentEntityList = elems.map { elem ->
            if (elem is PersistentEntity) {
                elem
            } else error("Wrong field type for One to One relationship.")
        }
        return persistentEntityList
    } else error("Wrong list type for One to Many relationship.")
}