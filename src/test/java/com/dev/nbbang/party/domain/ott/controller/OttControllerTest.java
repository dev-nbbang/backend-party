package com.dev.nbbang.party.domain.ott.controller;

import com.dev.nbbang.party.domain.ott.dto.OttDTO;
import com.dev.nbbang.party.domain.ott.dto.request.OttInformationRequest;
import com.dev.nbbang.party.domain.ott.entity.Ott;
import com.dev.nbbang.party.domain.ott.exception.FailDeleteOttException;
import com.dev.nbbang.party.domain.ott.exception.NoCreateOttException;
import com.dev.nbbang.party.domain.ott.exception.NoSuchOttException;
import com.dev.nbbang.party.domain.ott.repository.OttRepository;
import com.dev.nbbang.party.domain.ott.service.OttService;
import com.dev.nbbang.party.global.exception.NbbangException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = OttController.class)
@ExtendWith(SpringExtension.class)
class OttControllerTest {
    @MockBean
    private OttService ottService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("Ott 컨트롤러 : Ott 서비스 전체 조회 성공")
    void ott_서비스_전체_조회_성공() throws Exception {
        // given
        String uri = "/ott/list";
        given(ottService.findAllOtt()).willReturn(testOttListBuilder());

        // when
        MockHttpServletResponse response = mvc.perform(get(uri))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.[0].ottName").value("Netflix"))
                .andExpect(jsonPath("$.data.[1].ottName").value("Watcha"))
                .andExpect(jsonPath("$.data.[2].ottName").value("Disney Plus"))
                .andExpect(jsonPath("$.message").exists())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Ott 컨트롤러 : Ott 서비스 전체 조회 실패")
    void ott_서비스_전체_조회_실패() throws Exception {
        // given
        String uri = "/ott/list";
        given(ottService.findAllOtt()).willThrow(new NoSuchOttException("리스트 전체 조회 실패", NbbangException.NOT_FOUND_OTT));

        // when
        MockHttpServletResponse response = mvc.perform(get(uri))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.message").exists())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Ott 컨트롤러 : Ott 서비스 저장 성공")
    void ott_서비스_저장_성공() throws Exception {
        // given
        String uri = "/ott/new";
        given(ottService.saveOtt(any())).willReturn(testOttBuilder(1L, "Netflix"));

        // when
        MockHttpServletResponse response = mvc.perform(post(uri)
                .content(objectMapper.writeValueAsString(testRequest()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.ottName").value("Netflix"))
                .andExpect(jsonPath("$.data.ottHeadcount").value(4))
                .andExpect(jsonPath("$.data.ottPrice").value(1000L))
                .andExpect(jsonPath("$.data.ottImage").value("test"))
                .andExpect(jsonPath("$.message").exists())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("Ott 컨트롤러 : Ott 서비스 저장 실패")
    void ott_서비스_저장_실패() throws Exception {
        // given
        String uri = "/ott/new";
        given(ottService.saveOtt(any())).willThrow(new NoCreateOttException("저장 실패", NbbangException.NO_CREATE_OTT));

        // when
        MockHttpServletResponse response = mvc.perform(post(uri)
                .content(objectMapper.writeValueAsString(testRequest()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.message").exists())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Ott 컨트롤러 : Ott 서비스 수정 성공")
    void ott_서비스_수정_성공() throws Exception {
        // given
        String uri = "/ott/1";
        given(ottService.modifyOtt(anyLong(), any())).willReturn(testOttBuilder(1L, "Modified"));

        // when
        MockHttpServletResponse response = mvc.perform(put(uri)
                .content(objectMapper.writeValueAsString(testRequest()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.ottName").value("Modified"))
                .andExpect(jsonPath("$.data.ottHeadcount").value(4))
                .andExpect(jsonPath("$.data.ottPrice").value(1000L))
                .andExpect(jsonPath("$.data.ottImage").value("test"))
                .andExpect(jsonPath("$.message").exists())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("Ott 컨트롤러 : Ott 서비스 수정 실패")
    void ott_서비스_수정_실패() throws Exception {
        // given
        String uri = "/ott/1";
        given(ottService.modifyOtt(anyLong(), any())).willThrow(new NoCreateOttException("수정 실패", NbbangException.NO_CREATE_OTT));

        // when
        MockHttpServletResponse response = mvc.perform(put(uri)
                .content(objectMapper.writeValueAsString(testRequest()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.message").exists())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Ott 컨트롤러 : Ott 서비스 삭제 성공")
    void ott_서비스_삭제_성공() throws Exception {
// given
        String uri = "/ott/1";
        doNothing().when(ottService).deleteOtt(anyLong());

        // when
        MockHttpServletResponse response = mvc.perform(delete(uri))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("Ott 컨트롤러 : Ott 서비스 삭제 실패")
    void ott_서비스_삭제_실패() throws Exception {
        // given
        String uri = "/ott/1";
        doThrow(new FailDeleteOttException("삭제 실패", NbbangException.FAIL_TO_DELETE_OTT)).when(ottService).deleteOtt(anyLong());

        // when
        MockHttpServletResponse response = mvc.perform(delete(uri))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.message").exists())
                .andDo(print())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Ott 컨트롤러 : Ott 서비스 조회 성공")
    void ott_서비스_조회_성공() throws Exception {
        // given
        String uri = "/ott/1";
        given(ottService.findOtt(anyLong())).willReturn(testOttBuilder(1L, "Netflix"));

        // when
        MockHttpServletResponse response = mvc.perform(get(uri))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.ottName").value("Netflix"))
                .andExpect(jsonPath("$.data.ottHeadcount").value(4))
                .andExpect(jsonPath("$.data.ottPrice").value(1000L))
                .andExpect(jsonPath("$.data.ottImage").value("test"))
                .andExpect(jsonPath("$.message").exists())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Ott 컨트롤러 : Ott 서비스 조회 실패")
    void ott_서비스_조회_실패() throws Exception {
        // given
        String uri = "/ott/1";
        given(ottService.findOtt(anyLong())).willThrow(new NoSuchOttException("조회 실패", NbbangException.NOT_FOUND_OTT));
        // when
        MockHttpServletResponse response = mvc.perform(get(uri))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.message").value("조회 실패"))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    private static OttDTO testOttBuilder(Long ottId, String ottName) {
        return OttDTO.builder()
                .ottId(ottId)
                .ottName(ottName)
                .ottHeadcount(4)
                .ottPrice(1000L)
                .ottImage("test")
                .build();
    }

    private static List<OttDTO> testOttListBuilder() {
        List<OttDTO> ottList = new ArrayList<>();
        ottList.add(testOttBuilder(1L, "Netflix"));
        ottList.add(testOttBuilder(1L, "Watcha"));
        ottList.add(testOttBuilder(1L, "Disney Plus"));

        return ottList;
    }

    private static OttInformationRequest testRequest() {
        return OttInformationRequest.builder()
                .ottName("Netflix")
                .ottHeadcount(4)
                .ottPrice(1000L)
                .ottImage("test").build();
    }
}