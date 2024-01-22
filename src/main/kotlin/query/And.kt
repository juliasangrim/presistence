package org.example.query

import com.google.gson.JsonObject

class And : Condition() {

    private val conditions = mutableListOf<Condition>()

    override fun addCondition(condition: Condition) {
        conditions += condition
    }

    override fun check(json: JsonObject): Boolean {
        return conditions.all { it.check(json) }
    }
}