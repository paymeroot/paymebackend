package com.payme.backend.web.utils;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.json.JsonData;
import java.time.Instant;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;

public class ElasticSearchQueryUtil {

    /**
     * Builds an Elasticsearch query based on the provided search term and pageable.
     *
     * @param query    The search term to include in the query.
     * @param pageable The pagination information.
     * @return A Query object ready for execution.
     */
    public static Query buildSearchQuery(String query, Pageable pageable) {
        var boolQuery = QueryBuilders.bool()
            .should(QueryBuilders.queryString(q -> q.query("*" + query + "*")))
            .mustNot(QueryBuilders.exists(e -> e.field("deletedAt")));

        return new NativeQueryBuilder().withQuery(boolQuery.build()._toQuery()).withPageable(pageable).build();
    }

    public static Query buildQueryForRange(String field, Instant startDate, Instant endDate, Pageable pageable) {
        var boolQuery = QueryBuilders.bool()
            .should(
                QueryBuilders.range(
                    f -> f.field(field).gte(JsonData.fromJson(startDate.toString())).lte(JsonData.fromJson(endDate.toString()))
                )
            )
            .mustNot(QueryBuilders.exists(e -> e.field("deletedAt")));

        return new NativeQueryBuilder().withQuery(boolQuery.build()._toQuery()).withPageable(pageable).build();
    }
}
