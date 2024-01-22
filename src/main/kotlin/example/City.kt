package org.example.example

import org.example.entity.PersistentEntity

data class City(
    var name: String,

): PersistentEntity() {
    override fun toString(): String {
        return "City(id = '$id', name='$name')"
    }
}

