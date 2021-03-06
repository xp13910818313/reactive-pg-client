package io.reactiverse.kotlin.pgclient

import io.reactiverse.pgclient.PgConnectOptions
import io.reactiverse.pgclient.PgConnection as PgConnectionVertxAlias
import io.reactiverse.sqlclient.PreparedQuery
import io.reactiverse.sqlclient.RowSet
import io.reactiverse.sqlclient.Tuple
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.awaitResult

suspend fun PgConnectionVertxAlias.prepareAwait(sql : String) : PreparedQuery {
  return awaitResult{
    this.prepare(sql, it)
  }
}

suspend fun PgConnectionVertxAlias.preparedQueryAwait(sql : String) : RowSet {
  return awaitResult{
    this.preparedQuery(sql, it)
  }
}

suspend fun PgConnectionVertxAlias.queryAwait(sql : String) : RowSet {
  return awaitResult{
    this.query(sql, it)
  }
}

suspend fun PgConnectionVertxAlias.preparedQueryAwait(sql : String, arguments : Tuple) : RowSet {
  return awaitResult{
    this.preparedQuery(sql, arguments, it)
  }
}

suspend fun PgConnectionVertxAlias.preparedBatchAwait(sql : String, batch : List<Tuple>) : RowSet {
  return awaitResult{
    this.preparedBatch(sql, batch, it)
  }
}

object PgConnection {
/**
 * Connects to the database and returns the connection if that succeeds.
 * <p/>
 * The connection interracts directly with the database is not a proxy, so closing the
 * connection will close the underlying connection to the database.
 *
 * @param vertx the vertx instance
 * @param options the connect options
 * @param handler the handler called with the connection or the failure
 *
 * <p/>
 * NOTE: This function has been automatically generated from the [io.reactiverse.pgclient.PgConnection original] using Vert.x codegen.
 */
  suspend fun connectAwait(vertx : Vertx, options : PgConnectOptions) : PgConnectionVertxAlias {
    return awaitResult{
      PgConnectionVertxAlias.connect(vertx, options, it)
    }
  }

/**
 * Like [io.reactiverse.pgclient.PgConnection] with options build from the environment variables.
 *
 * @param vertx 
 * @param handler 
 *
 * <p/>
 * NOTE: This function has been automatically generated from the [io.reactiverse.pgclient.PgConnection original] using Vert.x codegen.
 */
  suspend fun connectAwait(vertx : Vertx) : PgConnectionVertxAlias {
    return awaitResult{
      PgConnectionVertxAlias.connect(vertx, it)
    }
  }

/**
 * Like [io.reactiverse.pgclient.PgConnection] with options build from <code>connectionUri</code>.
 *
 * @param vertx 
 * @param connectionUri 
 * @param handler 
 *
 * <p/>
 * NOTE: This function has been automatically generated from the [io.reactiverse.pgclient.PgConnection original] using Vert.x codegen.
 */
  suspend fun connectAwait(vertx : Vertx, connectionUri : String) : PgConnectionVertxAlias {
    return awaitResult{
      PgConnectionVertxAlias.connect(vertx, connectionUri, it)
    }
  }

}
