package org.example.convert

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import org.example.extention.orDefault

class PersistentEntityExclusionStrategy : ExclusionStrategy {

    override fun shouldSkipField(f: FieldAttributes?): Boolean {
        return f?.annotations?.isNotEmpty().orDefault()
    }

    override fun shouldSkipClass(clazz: Class<*>?): Boolean {
        return false
    }
}