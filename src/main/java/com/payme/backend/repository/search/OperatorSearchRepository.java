package com.payme.backend.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.payme.backend.domain.Operator;
import com.payme.backend.repository.OperatorRepository;
import com.payme.backend.web.utils.ElasticSearchQueryUtil;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link Operator} entity.
 */
public interface OperatorSearchRepository extends ElasticsearchRepository<Operator, Long>, OperatorSearchRepositoryInternal {}

interface OperatorSearchRepositoryInternal {
    Page<Operator> search(String query, Pageable pageable);
    Page<Operator> searchOperator(String query, Pageable pageable);

    Page<Operator> search(Query query);

    @Async
    void index(Operator entity);

    @Async
    void deleteFromIndexById(Long id);
}

class OperatorSearchRepositoryInternalImpl implements OperatorSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final OperatorRepository repository;

    OperatorSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, OperatorRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Operator> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Operator> searchOperator(String query, Pageable pageable) {
        var searchQuery = ElasticSearchQueryUtil.buildSearchQuery(query, pageable);
        return search(searchQuery.setPageable(pageable));
    }

    @Override
    public Page<Operator> search(Query query) {
        SearchHits<Operator> searchHits = elasticsearchTemplate.search(query, Operator.class);
        List<Operator> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Operator entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Operator.class);
    }
}
