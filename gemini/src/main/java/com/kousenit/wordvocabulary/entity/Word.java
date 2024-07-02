package com.kousenit.wordvocabulary.entity;

import jakarta.persistence.*;
import lombok.*;


//유저이메일, folderId, folderName, 영어 단어, 한글 뜻
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Word {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String englishWord;

    @Column(nullable = false)
    private String koreanMeaning;

    @ManyToOne
    @JoinColumn(name = "folder_id", nullable = false)
    private Folder folder;

}
