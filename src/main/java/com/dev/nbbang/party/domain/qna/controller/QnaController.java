package com.dev.nbbang.party.domain.qna.controller;

import com.dev.nbbang.party.domain.party.dto.PartyDTO;
import com.dev.nbbang.party.domain.party.exception.NoSuchPartyException;
import com.dev.nbbang.party.domain.party.service.PartyService;
import com.dev.nbbang.party.domain.qna.dto.QnaDTO;
import com.dev.nbbang.party.domain.qna.dto.request.AnswerRequest;
import com.dev.nbbang.party.domain.qna.dto.request.QuestionCreateRequest;
import com.dev.nbbang.party.domain.qna.dto.request.QuestionModifyRequest;
import com.dev.nbbang.party.domain.qna.dto.response.QnaInformationResponse;
import com.dev.nbbang.party.domain.qna.dto.response.QuestionInformationResponse;
import com.dev.nbbang.party.domain.qna.dto.response.QnaListResponse;
import com.dev.nbbang.party.domain.qna.exception.FailDeleteQnaException;
import com.dev.nbbang.party.domain.qna.exception.NoCreateQnaException;
import com.dev.nbbang.party.domain.qna.exception.NoSuchQnaException;
import com.dev.nbbang.party.domain.qna.service.QnaService;
import com.dev.nbbang.party.global.common.CommonResponse;
import com.dev.nbbang.party.global.common.CommonSuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(value = "/qna")
@RequiredArgsConstructor
@Slf4j
public class QnaController {
    private final QnaService qnaService;
    private final PartyService partyService;

    @PostMapping(value = "/new")
    public ResponseEntity<?> createQuestion(@RequestBody QuestionCreateRequest request, HttpServletRequest servletRequest) {
        log.info("[Qna Controller - Create Question] 문의 등록");

        // requestBody로 받지만 혹시 여기서 사용하는게 좋은지 판단 필요
        String memberId = servletRequest.getHeader("X-Authorization-Id");

        // 파티 아이디로 파티 있는지 조회하기
        PartyDTO findParty = partyService.findPartyByPartyId(request.getPartyId());

        // 문의 등록
        QnaDTO savedQuestion = qnaService.createQuestion(QuestionCreateRequest.toEntity(request, findParty));

        return ResponseEntity.status(HttpStatus.CREATED).body(CommonSuccessResponse.response(true, QuestionInformationResponse.create(savedQuestion), "성공적으로 문의를 등록했습니다."));

    }

    @GetMapping(value = "/{partyId}")
    public ResponseEntity<?> findQnaList(@PathVariable(name = "partyId") Long partyId, HttpServletRequest servletRequest) {
        log.info("[Qna Controller - Find QnA List] 질문자의 QnA 리스트 전체 조회");

        // 질문자 아이디 추출
        String memberId = servletRequest.getHeader("X-Authorization-Id");

        // 리스트 없는  경우에도 성공적으로 나가는 현상 수정 필요
        List<QnaDTO> findQnaList = qnaService.findAllQnA(partyId, memberId);

        return ResponseEntity.ok(CommonSuccessResponse.response(true, QnaListResponse.createList(findQnaList), "모든 문의내역 조회에 성공했습니다."));
    }

    @DeleteMapping(value = "/{qnaId}")
    public ResponseEntity<?> deleteQuestion(@PathVariable(name = "qnaId") Long qnaId) {
        log.info("[Qna Controller - Delete Question] 문의 삭제");

        // 문의내역 삭제
        qnaService.deleteQuestion(qnaId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }

    @PutMapping(value = "/{qnaId}")
    public ResponseEntity<?> modifyQuestionDetail(@RequestBody QuestionModifyRequest request, @PathVariable(name = "qnaId") Long qnaId) {
        log.info("[Qna Controller - Modify Question Detail] 문의 수정");

        // 문의 내역 수정 후 수정된 문의 내역 가져오기
        QnaDTO modifiedQuestion = qnaService.modifyQuestion(qnaId, request.getQuestionDetail());

        return ResponseEntity.status(HttpStatus.CREATED).body(CommonSuccessResponse.response(true, QuestionInformationResponse.create(modifiedQuestion), "문의 내용이 성공적으로 수정되었습니다."));
    }

    @PutMapping(value = "/{qnaId}/answer/{answerType}")
    public ResponseEntity<?> manageAnswer(@PathVariable(name = "qnaId") Long qnaId, @PathVariable(name = "answerType") Integer answerType, @RequestBody AnswerRequest request) {
        log.info("[Qna Controller - Manage Question] 문의 관리 (답변 등록, 삭제 , 수정)");

        // 파티장이 문의 내역 답변 관리
        QnaDTO manageAnswer = qnaService.manageAnswer(qnaId, request.getAnswerDetail(), answerType);

        String responseMessage = "답변 등록 및 수정에 성공했습니다.";
        if (answerType == 2) responseMessage = "답변 삭제에 성공했습니다.";

        return ResponseEntity.status(HttpStatus.CREATED).body(CommonSuccessResponse.response(true, QnaInformationResponse.create(manageAnswer), responseMessage));
    }

    @GetMapping(value = "/{partyId}/unanswer/list")
    public ResponseEntity<?> unansweredQnaList(@PathVariable(name = "partyId") Long partyId, HttpServletRequest request) {
        log.info("[Qna Controller - Unanswerd Question]");

        // 파티장이 미답변 질문 리스트를 조회
        List<QnaDTO> unansweredQuestionList = qnaService.findAllUnansweredQuestion(partyId);

        return ResponseEntity.ok(CommonSuccessResponse.response(true, QnaListResponse.createList(unansweredQuestionList), "미답변 문의내역 리스트 조회에 성공했습니다."));
    }
}
