package ck.apps.leabharcleachtadh.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.SentenceForm;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.Subject;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.Tense;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PracticeControllerIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void sessionsRemainIsolated() throws Exception {
        PracticeController.CreateSessionResponse sessionOne = createSession("DO");
        PracticeController.CreateSessionResponse sessionTwo = createSession("GO");

        prompt(sessionOne);
        PracticeController.AnswerResponse answerOne = answer(sessionOne.sessionId(), "wrong");
        assertThat(answerOne.correct()).isFalse();

        PracticeSession.SummaryResult summaryOne = summary(sessionOne.sessionId());
        assertThat(summaryOne.incorrect()).isEqualTo(1);

        PracticeSession.SummaryResult summaryTwo = summary(sessionTwo.sessionId());
        assertThat(summaryTwo.incorrect()).isZero();
        assertThat(summaryTwo.asked()).isZero();
    }

    private PracticeController.CreateSessionResponse createSession(String verb) throws Exception {
        PracticeController.CreateSessionRequest request = new PracticeController.CreateSessionRequest(
                List.of(verb),
                List.of(SentenceForm.STATEMENT_POSITIVE.name()),
                List.of(Tense.PAST.name()),
                List.of(Subject.SING_1ST.name()),
                "(the thing)",
                1,
                "strict"
        );
        String payload = objectMapper.writeValueAsString(request);
        String body = mockMvc.perform(post("/api/practice/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readValue(body, PracticeController.CreateSessionResponse.class);
    }

    private PracticeController.PromptResponse prompt(PracticeController.CreateSessionResponse session) throws Exception {
        String body = mockMvc.perform(get("/api/practice/sessions/{id}/prompt", session.sessionId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readValue(body, PracticeController.PromptResponse.class);
    }

    private PracticeController.AnswerResponse answer(String sessionId, String response) throws Exception {
        String body = mockMvc.perform(post("/api/practice/sessions/{id}/answer", sessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PracticeController.AnswerRequest(response))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readValue(body, PracticeController.AnswerResponse.class);
    }

    private PracticeSession.SummaryResult summary(String sessionId) throws Exception {
        String body = mockMvc.perform(get("/api/practice/sessions/{id}/summary", sessionId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readValue(body, PracticeSession.SummaryResult.class);
    }
}
