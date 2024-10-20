package com.payme.backend.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.payme.backend.domain.Profile;
import com.payme.backend.repository.ProfileRepository;
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
 * Spring Data Elasticsearch repository for the {@link Profile} entity.
 */
public interface ProfileSearchRepository extends ElasticsearchRepository<Profile, Long>, ProfileSearchRepositoryInternal {}

interface ProfileSearchRepositoryInternal {
    Page<Profile> search(String query, Pageable pageable);
    Page<Profile> searchProfile(String query, Pageable pageable);

    Page<Profile> search(Query query);

    @Async
    void index(Profile entity);

    @Async
    void deleteFromIndexById(Long id);
}

class ProfileSearchRepositoryInternalImpl implements ProfileSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ProfileRepository repository;

    ProfileSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, ProfileRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Profile> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Profile> searchProfile(String query, Pageable pageable) {
        var searchQuery = ElasticSearchQueryUtil.buildSearchQuery(query, pageable);
        return search(searchQuery.setPageable(pageable));
    }

    @Override
    public Page<Profile> search(Query query) {
        SearchHits<Profile> searchHits = elasticsearchTemplate.search(query, Profile.class);
        List<Profile> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Profile entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Profile.class);
    }
}
