package mobi.sevenwinds.app.author

import mobi.sevenwinds.utils.TimeUtils
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object AuthorTable : IntIdTable("author") {
    val name = varchar("name", 96)
    val created = datetime("created")
}

class AuthorEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<AuthorEntity>(AuthorTable)

    var name by AuthorTable.name
    var created by AuthorTable.created

    fun toResponse(): AuthorResponse {
        return AuthorResponse(id.value, name, TimeUtils.timeToString(created))
    }
}