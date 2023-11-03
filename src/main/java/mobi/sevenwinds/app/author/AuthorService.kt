package mobi.sevenwinds.app.author

import io.ktor.features.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

object AuthorService {

    suspend fun createAuthor(body: AuthorRequest): AuthorResponse = withContext(Dispatchers.IO) {
        transaction {
            val entity = AuthorEntity.new {
                this.name = body.name
                this.created = DateTime.now()
            }

            return@transaction entity.toResponse()
        }
    }

    suspend fun findById(id: Int): AuthorResponse = withContext(Dispatchers.IO) {
        transaction {
            val data = AuthorTable.select{AuthorTable.id eq id}.singleOrNull()
            if(data == null)
                throw NotFoundException("Author with id ${id} not found")

            return@transaction AuthorEntity.wrapRow(data).toResponse();
        }
    }
}