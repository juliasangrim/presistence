package org.example

import entity.FilePersistentEntityManager
import org.example.example.Author
import org.example.example.Book
import org.example.example.City
import org.example.example.Store
import java.util.*

fun main() {
    val entityManager = FilePersistentEntityManager()
    val author = Author("Steven King")
    val city = City("Novosibirsk")
    val store = Store(name = "Bookie", cash = 10000, city = city)
    val book = Book("Black Tower", 1000, author, store)
    entityManager.save(book)
//    val entity = entityManager.get(UUID.fromString("40d0df52-1f40-4271-8238-4568d6c50832"), Book::class.java)
//    println(entity.toString())
//    entityManager.delete(UUID.fromString("40d0df52-1f40-4271-8238-4568d6c50832"), Book::class.java)
//        val entity = entityManager.get(UUID.fromString("40d0df52-1f40-4271-8238-4568d6c50832"), Book::class.java)
//    println(entity.toString())
}