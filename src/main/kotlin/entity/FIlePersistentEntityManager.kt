package entity

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import org.example.convert.AnnotationUtils
import org.example.convert.FilePersistentEntityConverter
import org.example.convert.PersistentEntityExclusionStrategy
import org.example.entity.PersistentEntity
import org.example.entity.PersistentEntityManager
import org.example.entity.annotation.OneToMany
import org.example.query.Query
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.UUID
import kotlin.io.path.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.notExists
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.jvmErasure

class FilePersistentEntityManager(
    private val path: String = "./data",
    private val gson: Gson = GsonBuilder()
        .addDeserializationExclusionStrategy(PersistentEntityExclusionStrategy())
        .setLenient()
        .create()
) : PersistentEntityManager {

    init {
        if (Path(path).notExists()) {
            Path(path).createDirectory()
        }
    }

    override fun <T : PersistentEntity> save(entity: T): UUID {
        val converter = FilePersistentEntityConverter(entityManager = this)

        val jsonInfoMap = converter.serializeEntity(entity)
        for (jsonInfo in jsonInfoMap) {
            for (json in jsonInfo.value) {
                if (!isExist(UUID.fromString(json.asJsonObject["id"].asString), jsonInfo.key)) {
                    val jsonWriter = gson.newJsonWriter(FileWriter("${path}/${jsonInfo.key.simpleName}", true))
                    gson.toJson(json, jsonWriter)
                    jsonWriter.close()
                } else if (jsonInfo.key == entity::class) {
                    error("Entity update not implemented")
                }
            }

        }
        return entity.id
    }

    private fun <T : PersistentEntity> isExist(id: UUID, clazz: KClass<T>): Boolean {
        return get(id, clazz) != null
    }

    private fun iterate(reader: JsonReader): JsonObject? {
        var json: JsonObject? = null
        var lastName = ""
        var array: JsonArray? = null
        while (true) {
            when (reader.peek()) {
                JsonToken.BEGIN_OBJECT -> {
                    json = JsonObject()
                    reader.beginObject()
                }

                JsonToken.END_OBJECT -> {
                    reader.endObject()
                    return json
                }

                JsonToken.NAME -> {
                    lastName = reader.nextName()
                }

                JsonToken.STRING, JsonToken.NUMBER -> {
                    val value = reader.nextString()
                    if (array != null) {
                        array.add(value)
                    } else {
                        json?.addProperty(lastName, value)
                    }
                }

                JsonToken.BOOLEAN -> json?.addProperty(lastName, reader.nextBoolean())
                JsonToken.NULL -> json?.add(lastName, null)
                JsonToken.BEGIN_ARRAY -> {
                    reader.beginArray()
                    array = JsonArray()
                }

                JsonToken.END_ARRAY -> {
                    reader.endArray()
                    json?.add(lastName, array)
                    array = null
                }

                JsonToken.END_DOCUMENT -> return null
                else -> error("Json token is null")
            }
        }
    }

    override fun <T : PersistentEntity> get(id: UUID, clazz: KClass<T>): T? {
        if (Path("${path}/${clazz.simpleName}").notExists()) {
            return null
        }
        val reader = gson.newJsonReader(FileReader("${path}/${clazz.simpleName}"))
        var resultJson: JsonObject? = null

        while (true) {
            val json = iterate(reader) ?: break
            if (json["id"].asString == id.toString()) {
                resultJson = json
                break
            }
        }

        reader.close()
        return FilePersistentEntityConverter(entityManager = this).deserialize(resultJson, clazz)
    }

    override fun <T : PersistentEntity> getAll(clazz: KClass<T>): List<T> {
        if (Path("${path}/${clazz.simpleName}").notExists()) {
            return emptyList()
        }
        val reader = gson.newJsonReader(FileReader("${path}/${clazz.simpleName}"))
        val results = mutableListOf<T>()

        while (true) {
            val json = iterate(reader) ?: break
            val entity = FilePersistentEntityConverter(entityManager = this).deserialize(json, clazz)
            entity?.let { results.add(it) }
        }

        reader.close()
        return results
    }

    override fun <T : PersistentEntity> delete(id: UUID, clazz: KClass<T>) {
        val srcFile = File("${path}/${clazz.simpleName}")
        val destFile = File("${path}/${clazz.simpleName}_copy")
        val reader = gson.newJsonReader(FileReader(srcFile))
        val writer = gson.newJsonWriter(FileWriter(destFile))

        while (true) {
            val json = iterate(reader) ?: break
            if (json["id"] != null && json["id"].asString != id.toString()) {
                gson.toJson(json, writer)
            }
        }

        reader.close()
        writer.close()
        srcFile.delete()
        destFile.renameTo(srcFile)
    }

    override fun <T : PersistentEntity> search(query: Query, clazz: KClass<T>): List<JsonObject> {
        val reader = gson.newJsonReader(FileReader("${path}/${query.from}"))

        val entities = mutableListOf<T>()

        while (true) {
            val json = iterate(reader) ?: break
            if (query.condition.check(json)) {
                val entity = FilePersistentEntityConverter(entityManager = this).deserialize(json, clazz)
                entity?.let { entities.add(it) }
            }
        }

        reader.close()
        return entities
    }
}