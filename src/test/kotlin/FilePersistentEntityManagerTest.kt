import entity.FilePersistentEntityManager
import org.example.example.Author
import org.example.example.Book
import org.example.example.City
import org.example.example.Store
import org.junit.jupiter.api.assertThrows
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.Path
import kotlin.io.path.deleteRecursively
import kotlin.io.path.exists
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

internal class FilePersistentEntityManagerTest {

    private val entityManager = FilePersistentEntityManager(path = "./test")

    @Test
    fun whenInnerOneToOneEntityAlreadyExistOnSaveThenNotSaveIt() {
        val city = City("Novosibirsk")
        val store = Store(name = "Bookie", cash = 10000, city = city)
        val book1 = Book("Black Tower", 1000, emptyList(), store)
        entityManager.save(book1)

        val book2 = Book("White Towel", 500, emptyList(), store)
        entityManager.save(book2)

        assertEquals(1, entityManager.getAll(Store::class).count())
        assertEquals(1, entityManager.getAll(City::class).count())
    }

    @Test
    fun whenInnerOneToManyEntityAlreadyExistOnSaveThenNotSaveIt() {
        val city = City("Novosibirsk")
        val store = Store(name = "Bookie", cash = 10000, city = city)
        val author1 = Author("Steven King")
        val author2 = Author("Charles")
        val book1 = Book("Black Tower", 1000, listOf(author2, author1), store)
        entityManager.save(book1)

        val book2 = Book("White Towel", 500, listOf(author2, author1), store)
        entityManager.save(book2)

        assertEquals(2, entityManager.getAll(Author::class).count())
    }

//    @Test
//    fun whenInnerManyToOneEntityAlreadyExistOnSaveThenNotSaveIt() {
//        val city = City("Novosibirsk")
//        val store = Store(name = "Bookie", cash = 10000, city = city)
//        val author1 = Author("Steven King")
//        val author2 = Author("Charles")
//        val book1 = Book("Black Tower", 1000, listOf(author2, author1), store)
//        entityManager.save(book1)
//
//        val book2 = Book("White Towel", 500, listOf(author2, author1), store)
//        entityManager.save(book2)
//
//        assertEquals(2, entityManager.getAll(Author::class.java).count())
//    }

    @Test
    fun whenMainEntityAlreadyExistOnSaveThenError() {
        val city = City("Novosibirsk")
        val store = Store(name = "Bookie", cash = 10000, city = city)
        val book = Book("Black Tower", 1000, emptyList(), store)
        entityManager.save(book)

        assertThrows<Exception> { entityManager.save(book) }
    }

    @Test
    fun whenGetEntityThenOk() {
        val city = City("Novosibirsk")
        val store = Store(name = "Bookie", cash = 10000, city = city)
        val author1 = Author("Steven King")
        val author2 = Author("Charles")
        val book = Book("Black Tower", 1000, listOf(author2, author1), store)
        entityManager.save(book)

        val savedBook = entityManager.get(book.id, Book::class)

        assertEquals(book, savedBook)
    }

    @OptIn(ExperimentalPathApi::class)
    @AfterTest
    fun clearTestDir() {
        if (Path("./test").exists()) {
            Path("./test").deleteRecursively()
        }
    }
}