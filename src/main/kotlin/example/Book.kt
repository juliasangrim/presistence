package org.example.example

import org.example.entity.annotation.OneToOne
import org.example.entity.PersistentEntity

data class Book(
    var title : String,
    var pages : Int,
    @OneToOne
    var author : Author,
    @OneToOne
    var store : Store,
    ) : PersistentEntity() {

    override fun toString(): String {
        return "Book(id = '$id', title='$title', pages=$pages, author=$author)"
    }
}