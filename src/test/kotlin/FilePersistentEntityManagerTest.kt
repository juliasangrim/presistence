import com.google.gson.Gson
import entity.FilePersistentEntityManager
import org.example.example.Author
import org.example.example.Book
import org.example.example.City
import org.example.example.Company
import org.example.example.Employee
import org.example.example.Store
import org.example.query.query
import org.junit.jupiter.api.assertThrows
import java.io.File
import java.io.FileWriter
import java.util.UUID
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.Path
import kotlin.io.path.deleteRecursively
import kotlin.io.path.exists
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class FilePersistentEntityManagerTest {

    private val entityManager = FilePersistentEntityManager(path = "./test")

    @Test
    fun whenSaveThenOk() {
        val city = City("Novosibirsk")
        entityManager.save(city)

        val savedCity = entityManager.get(city.id, City::class)

        assertEquals(city, savedCity)
    }

    @Test
    fun whenEntityAlreadyExistOnSaveThenError() {
        val city = City("Novosibirsk")
        val store = Store(name = "Bookie", cash = 10000, city = city)
        val book = Book("Black Tower", 1000, emptyList(), store)
        entityManager.save(book)

        assertThrows<Exception> { entityManager.save(book) }
    }

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

    @Test
    fun whenGetAllEntitiesThenOk() {
        val city1 = City("Novosibirsk")
        entityManager.save(city1)
        val city2 = City("Moscow")
        entityManager.save(city2)
        val city3 = City("SPB")
        entityManager.save(city3)

        val cities = entityManager.getAll(City::class)

        assertEquals(3, cities.count())
        assertContains(cities, city1)
        assertContains(cities, city2)
        assertContains(cities, city3)
    }

    @Test
    fun whenSearchEntityThenOk() {
        val city = City("Novosibirsk")
        val store = Store(name = "Bookie", cash = 10000, city = city)
        val author1 = Author("Steven King")
        val author2 = Author("Charles")
        val book1 = Book("Black Tower", 1000, listOf(author2, author1), store)
        entityManager.save(book1)
        val book2 = Book("White Towel", 680, listOf(author2, author1), store)
        entityManager.save(book2)
        val book3 = Book("Pink Vowel", 540, listOf(author2, author1), store)
        entityManager.save(book3)

        val query = query {
            from(Book::class)
            where {
                "pages" neq 680
                or {
                    "title" eq "Black Tower"
                    "title" eq "White Towel"
                }
            }
        }.build()

        val books = entityManager.search(query, Book::class)

        assertEquals(1, books.count())
        assertEquals(book1, books[0])
    }

    @Test
    fun whenDeleteEntityWithOneToOneInnerEntityThenDeleteOnlyParent() {
        val city = City("Novosibirsk")
        val store = Store(name = "Bookie", cash = 10000, city = city)
        entityManager.save(store)

        entityManager.delete(store.id, Store::class)
        val deletedStore = entityManager.get(store.id, Store::class)
        val existingCity = entityManager.get(city.id, City::class)

        assertNull(deletedStore)
        assertNotNull(existingCity)
    }

    @Test
    fun whenDeleteEntityWithCascadeOneToManyInnerEntityThenDeleteBoth() {
        val city = City("Novosibirsk")
        val store = Store(name = "Bookie", cash = 10000, city = city)
        val author1 = Author("Steven King")
        val author2 = Author("Charles")
        val book = Book("Black Tower", 1000, listOf(author2, author1), store)
        entityManager.save(book)

        entityManager.delete(book.id, Book::class)

        val deletedBook = entityManager.get(book.id, Book::class)
        val deletedAuthor1 = entityManager.get(author1.id, Author::class)
        val deletedAuthor2 = entityManager.get(author2.id, Author::class)

        assertNull(deletedBook)
        assertNull(deletedAuthor1)
        assertNull(deletedAuthor2)
    }

    @Test
    fun whenDeleteEntityWithoutCascadeOneToManyInnerEntityThenDeleteOnlyParent() {
        val employee1 = Employee("John", 36)
        val employee2 = Employee("Lucy", 25)
        val company = Company("Google", mutableListOf(employee1, employee2))
        entityManager.save(company)

        entityManager.delete(company.id, Company::class)

        val deletedCompany = entityManager.get(company.id, Company::class)
        val existingEmployee1 = entityManager.get(employee1.id, Employee::class)
        val existingEmployee2 = entityManager.get(employee2.id, Employee::class)

        assertNull(deletedCompany)
        assertNotNull(existingEmployee1)
        assertNotNull(existingEmployee2)
    }

    @OptIn(ExperimentalPathApi::class)
    @AfterTest
    fun clearTestDir() {
        if (Path("./test").exists()) {
            Path("./test").deleteRecursively()
        }
    }
}