/*
 * Copyright (C) 2017 Julien Viet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.julienviet.pgclient.impl;

import com.julienviet.pgclient.PgException;
import com.julienviet.pgclient.codec.Message;
import com.julienviet.pgclient.codec.decoder.message.AuthenticationClearTextPassword;
import com.julienviet.pgclient.codec.decoder.message.AuthenticationMD5Password;
import com.julienviet.pgclient.codec.decoder.message.AuthenticationOk;
import com.julienviet.pgclient.codec.decoder.message.BackendKeyData;
import com.julienviet.pgclient.codec.decoder.message.ErrorResponse;
import com.julienviet.pgclient.codec.decoder.message.ParameterStatus;
import com.julienviet.pgclient.codec.decoder.message.ReadyForQuery;
import com.julienviet.pgclient.codec.encoder.message.PasswordMessage;
import com.julienviet.pgclient.codec.encoder.message.StartupMessage;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
class StartupCommand extends CommandBase {

  private static final String UTF8 = "UTF8";
  private final Handler<AsyncResult<DbConnection>> handler;
  final String username;
  final String password;
  final String database;
  private String CLIENT_ENCODING;
  private DbConnection conn;
  private Handler<Void> doneHandler;

  StartupCommand(String username, String password, String database, Handler<AsyncResult<DbConnection>> handler) {
    this.username = username;
    this.password = password;
    this.database = database;
    this.handler = handler;
  }

  @Override
  void exec(DbConnection c, Handler<Void> handler) {
    conn = c;
    doneHandler = handler;
    c.writeMessage(new StartupMessage(username, database));
  }

  @Override
  public void handleMessage(Message msg) {
    if (msg.getClass() == AuthenticationMD5Password.class) {
      AuthenticationMD5Password authMD5 = (AuthenticationMD5Password) msg;
      conn.writeMessage(new PasswordMessage(username, password, authMD5.getSalt()));
    } else if (msg.getClass() == AuthenticationClearTextPassword.class) {
      conn.writeMessage(new PasswordMessage(username, password, null));
    } else if (msg.getClass() == AuthenticationOk.class) {
//      handler.handle(Future.succeededFuture(conn));
//      handler = null;
    } else if (msg.getClass() == ParameterStatus.class) {
      ParameterStatus paramStatus = (ParameterStatus) msg;
      if(paramStatus.getKey().equals("client_encoding")) {
        CLIENT_ENCODING = paramStatus.getValue();
      }
    } else if (msg.getClass() == BackendKeyData.class) {
    }  else if (msg.getClass() == ErrorResponse.class) {
      ErrorResponse error = (ErrorResponse) msg;
      doneHandler.handle(null);
      handler.handle(Future.failedFuture(new PgException(error)));
    } else if (msg.getClass() == ReadyForQuery.class) {
      // The final phase before returning the connection
      // We should make sure we are supporting only UTF8
      // https://www.postgresql.org/docs/9.5/static/multibyte.html#MULTIBYTE-CHARSET-SUPPORTED
      Future<DbConnection> fut;
      if(!CLIENT_ENCODING.equals(UTF8)) {
        fut = Future.failedFuture(CLIENT_ENCODING + " is not supported in the client only " + UTF8);
      } else {
        fut = Future.succeededFuture(conn);
      }
      doneHandler.handle(null);
      handler.handle(fut);
    } else {
      super.handleMessage(msg);
    }
  }

  @Override
  void fail(Throwable err) {
    handler.handle(Future.failedFuture(err));
  }
}