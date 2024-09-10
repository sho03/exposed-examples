import com.mysql.cj.jdbc.MysqlDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.vendors.ForUpdateOption
import table.UsersTable

fun main() {

    Database.connect(
        MysqlDataSource().apply {
            serverName = "localhost"
            port = 3316
            databaseName = "sample-db"
            user = "user"
            password = "password"
        }
    )

    transaction {
        // enable logging
        addLogger(StdOutSqlLogger)

        // insert
        UsersTable.insert {
            it[name] = "Bob"
            it[email] = "bob@example.com"
        }
        UsersTable.insert {
            it[name] = "Alice"
            it[email] = "alice@example.com"
        }

        // select
        val names = UsersTable.selectAll().map {
            it[UsersTable.name]
        }
        println(names)

        // select for update
        UsersTable.selectAll()
            .where {
                UsersTable.email eq "bob@example.com"
            }
            .forUpdate()
            .map { it[UsersTable.email] }
            .first()

        // select for update nowait
        UsersTable.selectAll()
            .where {
                UsersTable.email eq "alice@example.com"
            }
            .forUpdate(ForUpdateOption.Oracle.ForUpdateNoWait)
            .map { it[UsersTable.email] }
            .first()

        // delete all
        UsersTable.deleteAll()
    }
}
