package org.example

import entity.FilePersistentEntityManager
import org.example.example.Book
import java.util.UUID

fun main() {
    val entityManager = FilePersistentEntityManager()
//    val author1 = Author("Steven King")
//    val author2 = Author("Charles")
//    val city = City("Novosibirsk")
//    val store = Store(name = "Bookie", cash = 10000, city = city)
//    val book = Book("Black Tower", 1000, listOf(author2, author1), store)
//    entityManager.save(book)
    val entity = entityManager.get(UUID.fromString("40d0df52-1f40-4271-8238-4568d6c50832"), Book::class)
    println(entity?.toString())
//    entityManager.delete(UUID.fromString("40d0df52-1f40-4271-8238-4568d6c50832"), Book::class.java)
//        val entity = entityManager.get(UUID.fromString("40d0df52-1f40-4271-8238-4568d6c50832"), Book::class.java)
//    println(entity.toString())

    // TODO: ref fields
//    val query = query {
//        from(Book::class)
//        where {
//            "pages" neq 700
//            or {
//                "title" eq "The Lord Of The Rings"
//                "title" eq "Black Tower"
//            }
//        }
//    }.build()
//    val jsons = entityManager.search(query, Book::class.java)
//    println(jsons.count())
//    println(jsons)
}