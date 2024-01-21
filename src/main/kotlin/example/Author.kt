package org.example.example

import org.example.entity.PersistentEntity

class Author(
    var name : String
    ) : PersistentEntity() {

    override fun toString(): String {
        return "Author(id='$id', name='$name')"
    }
}