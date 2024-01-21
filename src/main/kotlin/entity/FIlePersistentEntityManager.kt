package entity

import com.google.gson.Gson
import org.example.convert.FilePersistentEntityConverter
import org.example.entity.PersistentEntity
import org.example.entity.PersistentEntityManager
import java.io.FileWriter
import java.util.UUID

class FilePersistentEntityManager(
    private val converter : FilePersistentEntityConverter = FilePersistentEntityConverter(),
    private val gson: Gson = Gson()
) : PersistentEntityManager {

    override fun <T : PersistentEntity> save(entity: T): UUID {
        val json = converter.serialize(entity)
        val jsonWriter = gson.newJsonWriter(FileWriter("./data.txt", true))

        gson.toJson(json, jsonWriter)
        jsonWriter.close()

        return entity.id
    }

    override fun <T : PersistentEntity> get(id: UUID): T {
        TODO("Not implemented yet")
    }

    override fun delete(id: UUID) {
        TODO("Not implemented yet")
    }
}