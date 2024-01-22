package org.example.entity

import org.example.query.Query
import java.util.UUID
import kotlin.reflect.KClass

interface PersistentEntityManager {

    fun <T : PersistentEntity> save(entity : T) : UUID
    fun <T : PersistentEntity> get(id : UUID, clazz: KClass<T>) : T?

    fun <T : PersistentEntity> getAll(clazz: KClass<T>) : List<T>
    fun <T : PersistentEntity> delete(id: UUID, clazz: KClass<T>)
    fun <T : PersistentEntity> search(query: Query, clazz: KClass<T>): List<T>
}