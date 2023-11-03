package mobi.sevenwinds.app.budget

import io.ktor.features.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mobi.sevenwinds.app.author.AuthorEntity
import mobi.sevenwinds.app.author.AuthorResponse
import mobi.sevenwinds.app.author.AuthorService
import mobi.sevenwinds.app.author.AuthorTable
import mobi.sevenwinds.utils.TimeUtils
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.leftJoin
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object BudgetService {
    suspend fun addRecord(body: BudgetRecord): BudgetRecord = withContext(Dispatchers.IO) {
        transaction {
            val author: AuthorEntity? = if (body.author != null) {
                val data = AuthorTable.select { AuthorTable.id eq body.author }.singleOrNull()
                if (data == null)
                    throw NotFoundException("Author with id ${body.author} not found")

                AuthorEntity.wrapRow(data)
            } else {null}

            if(body.author != null) {
                val data = AuthorTable.select{AuthorTable.id eq body.author}.singleOrNull()
                if(data == null)
                    throw NotFoundException("Author with id ${body.author} not found")

                AuthorEntity.wrapRow(data).toResponse()
            }

            val entity = BudgetEntity.new {
                this.year = body.year
                this.month = body.month
                this.amount = body.amount
                this.type = body.type
                this.author = if(author == null) null else author.id.value
            }

            return@transaction entity.toResponse()
        }
    }

    suspend fun getYearStats(param: BudgetYearParam): BudgetYearStatsResponse = withContext(Dispatchers.IO) {
        transaction {
            val query = BudgetTable.leftJoin(AuthorTable, { author }, {AuthorTable.id})
                .select { BudgetTable.year eq param.year }
                .let { if (param.name?.isNotBlank() == true) it.andWhere { AuthorTable.name.regexp( param.name) } else it }
                .limit(param.limit, param.offset)

            val total = query.count()
            val data = query.map { AuthorBudgetRecord(it[BudgetTable.year], it[BudgetTable.month],
                                    it[BudgetTable.amount], it[BudgetTable.type],
                if(it[AuthorTable.name] == null) null
                else AuthorEntity.wrapRow(it).toResponse()
            ) }

            val sumByType = data.groupBy { it.type.name }.mapValues { it.value.sumOf { v -> v.amount } }

            return@transaction BudgetYearStatsResponse(
                total = total,
                totalByType = sumByType,
                items = data
            )
        }
    }
}