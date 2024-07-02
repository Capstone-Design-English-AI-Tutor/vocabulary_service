package com.kousenit.gemini;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.kousenit.gemini.GeminiRecords.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class GeminiServiceTest {
    @Autowired
    private GeminiService service;

    @Test
    void getCompletion_HHGtTG_question() {
        String text = service.getCompletion("""
            What is the Ultimate Answer to
            the Ultimate Question of Life, the Universe,
            and Everything?
            """);
        assertNotNull(text);
        System.out.println(text);
        assertThat(text).contains("42");
    }

    @Test
    void getCompletion() {
        String text = service.getCompletion("""
            How many roads must a man walk down
            before you can call him a man?
            """);
        assertNotNull(text);
        System.out.println(text);
    }

    @Test
    void pirateCoverLetter() {
        String text = service.getCompletion("""
            Please write a cover letter for a Java developer
            applying for an AI position, written in pirate speak.
            """);
        assertNotNull(text);
        System.out.println(text);
    }


    @Test
    void writeAStory() {
        String text = service.getCompletion("Write a story about a magic backpack.");
        assertNotNull(text);
        System.out.println(text);
    }

    @Test
    void describeAnImage() throws Exception {
        String text = service.getCompletionWithImage(
                "Describe this image",
                "A_cheerful_robot.png");
        assertNotNull(text);
        System.out.println(text);
    }

    @Test
    void countItems_gemini_pro() throws Exception {
        String text = service.getCompletionWithImage(
                """
               Use OCR to extract english word and korean meaning as a text.
               Give me a json file that contains english word in the image as key
               and corresponding korean word as value
                """,
                "예문있는영단어장.jpg");
        assertNotNull(text);
        System.out.println(text);
    }

    @Test
    void countItems_gemini_1_5() throws Exception {
        String text = service.analyzeImage(
                """
               Use OCR to extract english word and korean meaning as a text.
               Give me a json file that contains english word in the image as key
               and corresponding korean word as value
                """,
                "예문있는영단어장.jpg");
        assertNotNull(text);
        System.out.println(text);
    }

    @Test
    void getModels() {
        ModelList models = service.getModels();
        assertNotNull(models);
        models.models().stream()
                .map(Model::name)
                .filter(name -> name.contains("gemini"))
                .forEach(System.out::println);
    }

    @Test
    void countTokens_fullRequest() {
        var request = new GeminiRequest(
                List.of(new Content(
                        List.of(new TextPart("What is the airspeed velocity of an unladen swallow?")))));
        GeminiCountResponse response = service.countTokens(GeminiService.GEMINI_PRO, request);
        assertNotNull(response);
        System.out.println(response.totalTokens());
        assertThat(response.totalTokens()).isEqualTo(12);
    }

    @Test
    void countTokens() {
        int totalTokens = service.countTokens("What is the airspeed velocity of an unladen swallow?");
        assertThat(totalTokens).isEqualTo(12);
    }


}