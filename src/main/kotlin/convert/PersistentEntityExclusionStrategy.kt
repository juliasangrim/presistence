package org.example.convert

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes

class PersistentEntityExclusionStrategy : ExclusionStrategy {

    override fun shouldSkipField(f: FieldAttributes?): Boolean {
        return f?.annotations?.isNotEmpty() ?: false
    }

    override fun shouldSkipClass(clazz: Class<*>?): Boolean {
        return false
    }
}