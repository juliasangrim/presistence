package entity

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import org.example.convert.FilePersistentEntityConverter
import org.example.convert.PersistentEntityExclusionStrategy
import org.example.entity.PersistentEntity
import org.example.entity.PersistentEntityManager
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.UUID

class FilePersistentEntityManager(
    private val converter: FilePersistentEntityConverter = FilePersistentEntityConverter(),
    private val gson: Gson = GsonBuilder()
        .addDeserializationExclusionStrategy(PersistentEntityExclusionStrategy())
        .setLenient()
        .create()
) : PersistentEntityManager {

    override fun <T : PersistentEntity> save(entity: T): UUID {
        //TODO если с таким id уже есть
        val jsonInfoMap = converter.serializeEntity(entity)
        for (jsonInfo in jsonInfoMap) {
            val jsonWriter = gson.newJsonWriter(FileWriter("./${jsonInfo.key.simpleName}", true))
            for (json in jsonInfo.value) {
                gson.toJson(json, jsonWriter)
            }
            jsonWriter.close()

        }
        return entity.id
    }

    private fun iterate(reader: JsonReader): JsonObject? {
        var json: JsonObject? = null
        var lastName = ""
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
                    json?.addProperty(lastName, value)
                }

                JsonToken.BOOLEAN -> json?.addProperty(lastName, reader.nextBoolean())
                JsonToken.NULL -> json?.add(lastName, null)
                JsonToken.BEGIN_ARRAY -> reader.beginArray()
                JsonToken.END_ARRAY -> reader.endArray()
                JsonToken.END_DOCUMENT -> return null
                else -> error("Json token is null")
            }
        }
    }

    override fun <T : PersistentEntity> get(id: UUID, clazz: Class<T>): T {
        val reader = gson.newJsonReader(FileReader("./${clazz.simpleName}"))
        var resultJson = JsonObject()

        while (true) {
            val json = iterate(reader) ?: break
            if (json["id"].asString == id.toString()) {
                resultJson = json
                break
            }
        }

        reader.close()
        return gson.fromJson(resultJson, clazz)
    }

    override fun <T : PersistentEntity> delete(id: UUID, clazz: Class<T>) {
        val srcFile = File("./${clazz.simpleName}")
        val destFile = File("./${clazz.simpleName}_copy")
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
}