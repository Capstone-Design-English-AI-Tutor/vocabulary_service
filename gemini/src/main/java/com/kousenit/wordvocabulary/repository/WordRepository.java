package com.kousenit.wordvocabulary.repository;

import com.kousenit.wordvocabulary.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WordRepository extends JpaRepository<Word, Long> {
    List<Word> findByFolderId(Long folderId);
}
