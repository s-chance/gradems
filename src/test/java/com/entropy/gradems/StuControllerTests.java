package com.entropy.gradems;

import com.entropy.gradems.controller.StuController;
import com.entropy.gradems.service.StuService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(StuController.class)
public class StuControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StuService stuService;

    @Test
    public void testUpdateDepart() throws Exception {
        String dId = "01";
        String dName = "test";
        when(stuService.updateDepart(dId, dName)).thenReturn(1);

        mockMvc.perform(patch("/updateDepart/{dId}", dId)
                .param("dName", dName))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) content().string("1"));


    }
}
