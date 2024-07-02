package com.kousenit.gemini.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SimpleQuizListDTO {

    @Builder
    public static record Quiz(String quiz, String answer) {}

    private List<Quiz> quizList;
}
