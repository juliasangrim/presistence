package org.example.example

import org.example.entity.PersistentEntity
import org.example.entity.annotation.OneToOne

data class Store(
    var name : String,
    var cash: Long,
    @OneToOne
    var city: City
) : PersistentEntity() {

    override fun toString(): String {
        return "Store(id = '$id', name='$name', cash=$cash, city=$city)"
    }
}