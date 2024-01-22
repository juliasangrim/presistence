package org.example.query

import com.google.gson.JsonObject

abstract class Condition {

    abstract fun addCondition(condition: Condition)

    abstract fun check(json: JsonObject): Boolean

    fun or(initializer: Condition.() -> Unit) {
        addCondition(Or().apply(initializer))
    }

    infix fun String.eq(value: Any?) {
        addCondition(Eq(this, value))
    }

    infix fun String.neq(value: Any?) {
        addCondition(Neq(this, value))
    }
}