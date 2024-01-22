package org.example.query

import com.google.gson.JsonObject

class Or : Condition() {

    private val conditions = mutableListOf<Condition>()

    override fun addCondition(condition: Condition) {
        conditions += condition
    }

    override fun check(json: JsonObject): Boolean {
        return conditions.any { it.check(json) }
    }
}