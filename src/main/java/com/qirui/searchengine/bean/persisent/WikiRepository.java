package com.qirui.searchengine.bean.persisent;

import com.qirui.searchengine.bean.ESWikiBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WikiRepository extends JpaRepository<Wiki, Integer> {
    @Query(value = "select new com.qirui.searchengine.bean.ESWikiBean(w.wikiId,w.wikiTitle,w.wikiBrief,w.wikiInfo,w.wikiBody,w.wikiUrl) " +
            "from Wiki w")
    Page<ESWikiBean> getAllRecord(Pageable pageable);
}
