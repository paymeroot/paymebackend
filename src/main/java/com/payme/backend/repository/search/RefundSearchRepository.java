package com.payme.backend.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.payme.backend.domain.Refund;
import com.payme.backend.repository.RefundRepository;
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
 * Spring Data Elasticsearch repository for the {@link Refund} entity.
 */
public interface RefundSearchRepository extends ElasticsearchRepository<Refund, Long>, RefundSearchRepositoryInternal {}

interface RefundSearchRepositoryInternal {
    Page<Refund> search(String query, Pageable pageable);
    Page<Refund> searchRefund(String query, Pageable pageable);

    Page<Refund> search(Query query);

    @Async
    void index(Refund entity);

    @Async
    void deleteFromIndexById(Long id);
}

class RefundSearchRepositoryInternalImpl implements RefundSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final RefundRepository repository;

    RefundSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, RefundRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Refund> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Refund> searchRefund(String query, Pageable pageable) {
        var searchQuery = ElasticSearchQueryUtil.buildSearchQuery(query, pageable);
        return search(searchQuery.setPageable(pageable));
    }

    @Override
    public Page<Refund> search(Query query) {
        SearchHits<Refund> searchHits = elasticsearchTemplate.search(query, Refund.class);
        List<Refund> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Refund entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Refund.class);
    }
}
