package org.example.query

import com.google.gson.JsonObject

class Neq(
    private val field: String,
    private val value: Any?
) : Condition() {

    init {
        if (value != null && value !is Number && value !is String) {
            throw IllegalArgumentException(
                "Only <null>, numbers and strings values can be used in the 'where' clause"
            )
        }
    }

    override fun addCondition(condition: Condition) {
        throw IllegalStateException("Can't add a nested condition to the 'neq'")
    }

    override fun check(json: JsonObject): Boolean {
        return (json[field] == null || json[field].asString != value.toString())
    }
}