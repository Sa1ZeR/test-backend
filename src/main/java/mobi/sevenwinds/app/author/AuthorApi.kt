package mobi.sevenwinds.app.author

import com.papsign.ktor.openapigen.annotations.type.string.pattern.RegularExpression
import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.path.normal.post
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route

fun NormalOpenAPIRoute.author() {
    route("author") {
        route("/add").post<Unit, AuthorResponse, AuthorRequest>(info("Добавить запись")) {
            param, body -> respond(AuthorService.createAuthor(body))
        }
    }
}

data class AuthorRequest (
    @RegularExpression("^[a-zA-ZА-Яа-я-]+(?:\\s[a-zA-ZА-Яа-я-]+){1,2}\$") val name: String
)

data class AuthorResponse (
    val name: String,
    val created: String
)