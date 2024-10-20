package com.payme.backend.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.payme.backend.domain.Kyc;
import com.payme.backend.repository.KycRepository;
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
 * Spring Data Elasticsearch repository for the {@link Kyc} entity.
 */
public interface KycSearchRepository extends ElasticsearchRepository<Kyc, Long>, KycSearchRepositoryInternal {}

interface KycSearchRepositoryInternal {
    Page<Kyc> search(String query, Pageable pageable);
    Page<Kyc> searchKyc(String query, Pageable pageable);

    Page<Kyc> search(Query query);

    @Async
    void index(Kyc entity);

    @Async
    void deleteFromIndexById(Long id);
}

class KycSearchRepositoryInternalImpl implements KycSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final KycRepository repository;

    KycSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, KycRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Kyc> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Kyc> searchKyc(String query, Pageable pageable) {
        var searchQuery = ElasticSearchQueryUtil.buildSearchQuery(query, pageable);
        return search(searchQuery.setPageable(pageable));
    }

    @Override
    public Page<Kyc> search(Query query) {
        SearchHits<Kyc> searchHits = elasticsearchTemplate.search(query, Kyc.class);
        List<Kyc> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Kyc entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Kyc.class);
    }
}
