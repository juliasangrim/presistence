package org.example.entity

import java.util.UUID

interface PersistentEntityManager {

    fun <T : PersistentEntity> save(entity : T) : UUID
    fun <T : PersistentEntity> get(id : UUID, clazz: Class<T>) : T
    fun <T : PersistentEntity> delete(id: UUID, clazz: Class<T>)
}