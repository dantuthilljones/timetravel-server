package me.detj.timetravel.controllers;

import me.detj.timetravel.TimeTravelLogic;
import me.detj.timetravel.TimeTravelTestInstances;
import me.detj.timetravel.coders.date.DateCoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TestTimeTravelController {

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);

    private MockMvc mvc;


    @BeforeEach
    public void before() throws IOException {
        TimeTravelLogic timeTravelLogic = new TimeTravelLogic(
                TimeTravelTestInstances.dateSource(),
                DateCoder.getInstance(),
                TimeTravelTestInstances.stringCoder(),
                TimeTravelTestInstances.noOpCrypter(),
                16);
        TimeTravelController timeTravelController = new TimeTravelController(timeTravelLogic);

        mvc = MockMvcBuilders.standaloneSetup()
                .setControllerAdvice(timeTravelController)
                .build();
    }

    @Test
    public void testTodaysWords() throws Exception {
        String words = mvc.perform(MockMvcRequestBuilders.get("/api/todays-words"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(11))
                .andReturn().getResponse().getContentAsString();

        mvc.perform(
                MockMvcRequestBuilders.post("/api/check")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(words))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("PRESENT"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.date.year").value(2000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.date.month").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.date.day").value(1));
    }

    @Test
    public void testBadWords() throws Exception {
        String words = "[\"word1\",\"word1\",\"word1\"]";

        mvc.perform(
                MockMvcRequestBuilders.post("/api/check")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(words))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("ERROR"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.date").doesNotExist());
    }

    @Test
    public void testPastWords() throws Exception {
        String request = "{\"perPage\": 10, \"page\": 0}";

        mvc.perform(
                MockMvcRequestBuilders.post("/api/past-words")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(request))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.pageNum").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size").value(10))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].date.year").value(1999))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].date.month").value(12))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].date.day").value(31));
    }

    @Test
    public void testToday() throws Exception {
        mvc.perform(
                MockMvcRequestBuilders.get("/api/today")
                        .contentType(APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.year").value(2000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.month").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.day").value(1));
    }
}
