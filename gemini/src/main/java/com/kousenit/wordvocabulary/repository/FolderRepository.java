package com.kousenit.wordvocabulary.repository;

import com.kousenit.wordvocabulary.entity.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FolderRepository extends JpaRepository<Folder, Long> {

}
