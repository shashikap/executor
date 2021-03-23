package com.typeboot.executor.cassandra

import com.datastax.oss.driver.api.core.ConsistencyLevel
import com.datastax.oss.driver.api.core.CqlSession
import com.typeboot.executor.spi.RowMapper
import com.typeboot.executor.spi.ScriptExecutor
import com.typeboot.executor.spi.model.ScriptStatement
import java.sql.SQLException
import java.time.Duration

class CassandraExecutor(private val cqlSession: CqlSession, private val timeout: Long, private val consistencyLevel: ConsistencyLevel) : ScriptExecutor {

    override fun executeStatement(stmt: ScriptStatement): Boolean {
        with(this.cqlSession) {
            try {
                println("execute statement: [${stmt.content}]")
                val bounded = prepare(stmt.content).bind()
                        .setTimeout(Duration.ofSeconds(timeout))
                        .setConsistencyLevel(consistencyLevel)
                execute(bounded)
            } catch (se: SQLException) {
                throw RuntimeException("error executing statements", se)
            }
        }
        return true
    }

    override fun queryForObject(stmt: ScriptStatement?, rowMapper: RowMapper?): Any {
        TODO("Not yet implemented")
    }

    override fun shutdown() {
        if (!this.cqlSession.isClosed) {
            this.cqlSession.close();
        }
    }
}