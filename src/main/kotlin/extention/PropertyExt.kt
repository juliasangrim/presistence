package org.example.extention

import org.example.entity.PersistentEntity
import kotlin.reflect.KProperty1

fun KProperty1<out PersistentEntity, *>.checkIsList(src: PersistentEntity) {
    val elems = this.getter.call(src)
    if (elems !is List<*>) {
        error("Wrong field type for One to Many relationship.")
    } else {
        elems.forEach { elem ->
            if (elem !is PersistentEntity) {
                error("Wrong list type for One to Many relationship.")
            }
        }
    }
}

fun KProperty1<out PersistentEntity, *>.checkIsEntity(src: PersistentEntity) {
    val elem = this.getter.call(src)
    if (elem !is PersistentEntity) error("Wrong field type for One to One relationship.")
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