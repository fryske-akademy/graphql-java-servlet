package graphql.servlet;

import graphql.kickstart.execution.GraphQLQueryResult;
import graphql.servlet.core.GraphQLObjectMapper;
import java.io.IOException;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

interface QueryResponseWriter {

  static QueryResponseWriter createWriter(
      GraphQLQueryResult result,
      GraphQLObjectMapper graphQLObjectMapper,
      long subscriptionTimeout
  ) {
    Objects.requireNonNull(result, "GraphQL query result cannot be null");

    if (result.isBatched()) {
      return new BatchedQueryResponseWriter(result.getResults(), graphQLObjectMapper);
    } else if (result.isAsynchronous()) {
      return new SingleAsynchronousQueryResponseWriter(result.getResult(), graphQLObjectMapper, subscriptionTimeout);
    } else if (result.isError()) {
      return new ErrorQueryResponseWriter(result.getStatusCode(), result.getMessage());
    }
    return new SingleQueryResponseWriter(result.getResult(), graphQLObjectMapper);
  }

  void write(HttpServletRequest request, HttpServletResponse response) throws IOException;

}
