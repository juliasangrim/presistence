package org.example.query

import org.example.entity.PersistentEntity
import kotlin.reflect.KClass

class QueryBuilder {

    private lateinit var from: String

    private lateinit var condition: Condition

    fun <T : PersistentEntity> from(clazz: KClass<T>) {
        this.from = clazz.simpleName.orEmpty()
    }

    fun where(initializer: Condition.() -> Unit) {
        condition = And().apply(initializer)
    }

    fun build(): Query {
        return Query(from, condition)
    }
}

fun query(initializer: QueryBuilder.() -> Unit): QueryBuilder {
    return QueryBuilder().apply(initializer)
}