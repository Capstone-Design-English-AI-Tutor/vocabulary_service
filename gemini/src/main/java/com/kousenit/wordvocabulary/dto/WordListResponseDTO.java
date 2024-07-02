package com.kousenit.wordvocabulary.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@AllArgsConstructor
@Data
public class WordListResponseDTO {

    @Builder
    public record Word(Long id, String englishWord, String koreanMeaning) {}

    private List<Word> wordList;
}
