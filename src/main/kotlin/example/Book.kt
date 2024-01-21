package org.example.example

import org.example.OneToOne
import org.example.entity.PersistentEntity

class Book(
    var title : String,
    var pages : Int,
    @OneToOne
    var author : Author
    ) : PersistentEntity() {

    override fun toString(): String {
        return "Book(id = '$id', title='$title', pages=$pages, author=$author)"
    }
}