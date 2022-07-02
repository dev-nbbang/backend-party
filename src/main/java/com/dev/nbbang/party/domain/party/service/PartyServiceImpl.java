package com.dev.nbbang.party.domain.party.service;

import com.dev.nbbang.party.domain.ott.dto.OttDTO;
import com.dev.nbbang.party.domain.ott.entity.Ott;
import com.dev.nbbang.party.domain.party.dto.PartyDTO;
import com.dev.nbbang.party.domain.party.dto.request.MatchingRequest;
import com.dev.nbbang.party.domain.party.dto.response.MatchingInfoResponse;
import com.dev.nbbang.party.domain.party.entity.NoticeType;
import com.dev.nbbang.party.domain.party.entity.Participant;
import com.dev.nbbang.party.domain.party.entity.Party;
import com.dev.nbbang.party.domain.party.exception.*;
import com.dev.nbbang.party.domain.party.repository.ParticipantRepository;
import com.dev.nbbang.party.domain.party.repository.PartyRepository;
import com.dev.nbbang.party.domain.payment.api.service.ImportAPI;
import com.dev.nbbang.party.domain.payment.api.service.MemberAPI;
import com.dev.nbbang.party.domain.payment.repository.PaymentLogRepository;
import com.dev.nbbang.party.domain.payment.service.PaymentService;
import com.dev.nbbang.party.domain.qna.entity.Qna;
import com.dev.nbbang.party.domain.qna.exception.FailDeleteQnaException;
import com.dev.nbbang.party.domain.qna.repository.QnaRepository;
import com.dev.nbbang.party.global.common.CommonResponse;
import com.dev.nbbang.party.global.exception.NbbangException;
import com.dev.nbbang.party.global.util.AesUtil;
import com.dev.nbbang.party.global.util.RedisUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PartyServiceImpl implements PartyService {
    private final PartyRepository partyRepository;
    private final QnaRepository qnaRepository;
    private final ParticipantRepository participantRepository;
    private final PaymentLogRepository paymentLogRepository;
    private final ImportAPI importAPI;
    private final RedisUtil redisUtil;
    private final PaymentService paymentService;
    private final AesUtil aesUtil;
    private final MatchingProducer matchingProducer;

    private final String MATCHING_KEY_PREFIX = "matching:";

    /**
     * 파티장이 새로운 파티를 생성한다. (암호화)
     *
     * @param party 새로운 파티 생성 데이터
     * @return PartyDTO 저장된 파티 데이터 정보
     */
    @Override
    @Transactional
    public PartyDTO createParty(Party party) {
        // 추가 검증 로직 필요 시 추가

        // 1. 파티 생성
        Party createdParty = Optional.of(partyRepository.save(party)).orElseThrow(() -> new NoCreatePartyException("파티 생성에 실패했습니다.", NbbangException.NO_CREATE_PARTY));

        // 2. 파티원 테이블에 파티장 정보 입력하기
        Participant savedParticipant = Optional.of(participantRepository.save(Participant.builder()
                .party(createdParty)
                .participantId(createdParty.getLeaderId())
                .participantYmd(LocalDateTime.now())
                .ottId(createdParty.getOtt().getOttId()).build()))
                .orElseThrow(() -> new NoCreateParticipantException("파티원 정보 저장에 실패했습니다.", NbbangException.NO_CREATE_PARTICIPANT));

        // 3. 양방향이 되는 경우 연관관계 편의 메소드와 리턴할 때 같이 넣어주기
        return PartyDTO.create(createdParty);
    }

    /**
     * 파티 아이디를 이용해 파티 정보를 조회한다.
     *
     * @param partyId 고유 파티 아이디
     * @return PartyDTO 조회한 파티 데이터 정보
     */
    @Override
    public PartyDTO findPartyByPartyId(Long partyId) {
        // 1. 고유한 파티 아이디로 파티 정보 조회
        Party findParty = Optional.ofNullable(partyRepository.findByPartyId(partyId)).orElseThrow(() -> new NoSuchPartyException("등록되지 않았거나 이미 해체된 파티입니다.", NbbangException.NOT_FOUND_PARTY));

        return PartyDTO.create(findParty);
    }

    /**
     * 파티장이 파티를 해체한다.
     *
     * @param partyId  고유한 파티 아이디
     * @param leaderId 파티장 아이디
     */
    @Override
    @Transactional
    public void deleteParty(Long partyId, String leaderId) {
        // 2. 파티 권한 확인 후 파티 찾기
        Party findParty = validationLeader(partyId, leaderId);

        // 3. 파티원에게 파티 해체 알림

        // 4. 파티원 환불 처리

        // 5. 파티원 테이블 삭제
        participantRepository.deleteByParty(findParty);

        // 5-1. 파티원 삭제 확인
        List<Participant> findParticipants = participantRepository.findAllByParty(findParty);
        if (!findParticipants.isEmpty())
            throw new FailDeleteParticipantException("해당 파티에 참가한 파티원 탈퇴에 실패했습니다.", NbbangException.FAIL_TO_DELETE_PARTICIPANT);

        // 6. QNA 테이블 삭제
        qnaRepository.deleteByParty(findParty);

        // 6-1. QNA 삭제 확인
        List<Qna> findQnaList = qnaRepository.findAllByParty(findParty);
        if (!findQnaList.isEmpty())
            throw new FailDeleteQnaException("파티 질문 내역 삭제에 실패했습니다", NbbangException.FAIL_TO_DELETE_QNA);

        // 7. 파티 테이블 삭제
        partyRepository.deleteByPartyId(partyId);

        // 7-1. 파티 테이블 삭제 확인
        Optional.ofNullable(partyRepository.findByPartyId(partyId)).ifPresent(
                exception -> {
                    throw new FailDeletePartyException("파티 해체에 실패했습니다.", NbbangException.FAIL_TO_DELETE_PARTY);
                }
        );
    }

    /**
     * 파티장이 파티 정보를 수정한다 (일반 결제만 해당)
     *
     * @param partyId     고유 파티 아이디
     * @param title       파티 제목
     * @param partyDetail 파티 상세 내용
     * @param leaderId    파티장 아이디
     * @return 수정된 파티 정보
     */
    @Override
    @Transactional
    public PartyDTO updatePartyInformation(Long partyId, String title, String partyDetail, String leaderId) {
        // 1. 파티장 권한 확인 후 파티 찾기
        Party updatedParty = validationLeader(partyId, leaderId);

        // 2. 파티 타이틀, 파티 정보 수정
        updatedParty.updatePartyDetails(title, partyDetail);

        return PartyDTO.create(updatedParty);
    }

    /**
     * 마감안된 파티 리스트를 조회한다.
     *
     * @param ott  OTT 서비스
     * @param size 한번에 조회할 사이즈
     * @return 마감 안된 파티 리스트
     */
    @Override
    public List<PartyDTO> findPartyList(Ott ott, Long partyId, int size) {
        // 1. 마감 안된 파티 리스트 조회하기
        Slice<Party> findPartyList = partyRepository.findPartyList(ott, ott.getOttHeadcount(), partyId, PageRequest.of(0, size));

        if (findPartyList.isEmpty())
            throw new NoSuchPartyException("모집중인 파티가 없습니다.", NbbangException.NOT_FOUND_PARTY);

        return PartyDTO.createList(findPartyList);
    }

    /**
     * 매칭 타입을 통해 마감안된 파티 리스트를 조회한다.
     *
     * @param matchingType 매칭 타입
     * @param ott          OTT 서비스
     * @param size         한번에 조회할 사이즈
     * @return 마감 안된 파티 리스트
     */
    @Override
    public List<PartyDTO> findPartyListByMatchingType(Integer matchingType, Ott ott, Long partyId, int size) {
        // 1. 마감 안된 파티 리스트 조회하기
        Slice<Party> findPartyList = partyRepository.findPartyList(matchingType, ott, ott.getOttHeadcount(), partyId, PageRequest.of(0, size));

        if (findPartyList.isEmpty())
            throw new NoSuchPartyException("모집중인 파티가 없습니다.", NbbangException.NOT_FOUND_PARTY);
        return PartyDTO.createList(findPartyList);
    }

    /**
     * OTT 계정이 중복되었는지 판단한다.
     *
     * @param ott      OTT 서비스
     * @param ottAccId OTT 계정 아이디
     * @return true/false
     */
    @Override
    public Boolean duplicateOttAcc(Ott ott, String ottAccId) {
        // 1. OTT 계정 중복 확인
        Optional.ofNullable(partyRepository.findByOttAndOttAccId(ott, ottAccId)).ifPresent(
                exception -> {
                    throw new DuplicateOttAccException("중복된 Ott 계정입니다.", NbbangException.DUPLICATE_OTT_ACC);
                }
        );

        return true;
    }

    /**
     * 파티장이 파티 공지를 작성, 수정, 삭제한다.
     *
     * @param noticeType  파티 공지 타입 (작성, 수정, 삭제)
     * @param partyId     고유 파티 아이디
     * @param leaderId    파티장 아이디
     * @param partyNotice 파티 공지 내용
     * @return 수정된 파티 정보
     */
    @Override
    @Transactional
    public PartyDTO updatePartyNotice(NoticeType noticeType, Long partyId, String leaderId, String partyNotice) {
        // 1. 파티장 권한 확인 및 업데이트 할 파티 찾기
        Party updatedParty = validationLeader(partyId, leaderId);

        // 2. 파티 공지 업데이트
        if (noticeType != NoticeType.DELETE) updatedParty.updatePartyNotice(partyNotice);
        else updatedParty.deletePartyNotice();

        return PartyDTO.create(updatedParty);
    }

    /**
     * 파티장이 OTT 계정을 수정한다. (비밀번호 암호화 추가 필요)
     *
     * @param partyId  고유 파티 아이디
     * @param leaderId 파티장 아이디
     * @param ottAccId OTT 계정 아이디
     * @param ottAccPw OTT 계정 패스워드
     * @return 수정된 파티 정보
     */
    @Override
    @Transactional
    public PartyDTO updateOttAcc(Long partyId, String leaderId, String ottAccId, String ottAccPw) {
        // 1. 파티장 권한 확인 및 파티 찾기
        Party updatedParty = validationLeader(partyId, leaderId);

        updatedParty.updateOttAcc(ottAccId, ottAccPw);

        return PartyDTO.create(updatedParty);
    }

    // 파티장 검증
    private Party validationLeader(Long partyId, String leaderId) {
        // 1. 파티 찾기
        Party findParty = Optional.ofNullable(partyRepository.findByPartyId(partyId))
                .orElseThrow(() -> new NoSuchPartyException("등록되지 않았거나 이미 해체된 파티입니다.", NbbangException.NOT_FOUND_PARTY));

        // 2. 파티장 권한 확인
        if (!findParty.getLeaderId().equals(leaderId))
            throw new InvalidLeaderGrantException("파티를 해체할 권한이 없습니다.", NbbangException.INVALID_LEADER_GRANT);
        return findParty;
    }

    @Override
    public int findPrice(Long partyId) {
        PartyDTO partyDTO = findPartyByPartyId(partyId);
        Long dayPrice = partyDTO.getPrice();
        LocalDateTime finalDay = partyDTO.getRegYmd().plusDays(partyDTO.getPeriod());
        long days = Duration.between(finalDay, LocalDateTime.now()).toDays();
        return (int) (days * dayPrice);
    }

    @Override
    public OttDTO findOttPrice(Long partyId) {
        PartyDTO partyDTO = findPartyByPartyId(partyId);
        return OttDTO.create(partyDTO.getOtt());
    }

    @Override
    @Transactional
    public PartyDTO isPartyJoin(Long partyId, String memberId) {
        Party party = Optional.ofNullable(partyRepository.findByPartyId(partyId))
                .orElseThrow(() -> new NoSuchPartyException("등록되지 않았거나 이미 해체된 파티입니다.", NbbangException.NOT_FOUND_PARTY));
        if (party.getPresentHeadcount() < party.getMaxHeadcount()) {
            //파티원 테이블에 파티원 저장
            participantRepository.save(Participant.builder().party(party).participantId(memberId).participantYmd(LocalDateTime.now()).ottId(party.getOtt().getOttId()).build());
            party.increasePresentHeadcount();

            return PartyDTO.create(party);
        }

        // 바꾸기
        throw new NoCreateParticipantException("예외명 바꾸기", NbbangException.NOT_FOUND_OTT);
    }

    @Override
    @Transactional
    public void isRollBackPartyJoin(Long partyId, String memberId) {
        Party party = Optional.ofNullable(partyRepository.findByPartyId(partyId))
                .orElseThrow(() -> new NoSuchPartyException("등록되지 않았거나 이미 해체된 파티입니다.", NbbangException.NOT_FOUND_PARTY));

        participantRepository.deleteByPartyAndParticipantId(party, memberId);

        party.decreasePresentHeadCount();
    }

    @Override
    public List<PartyDTO> findJoinPartyList(Ott ott, int maxHeadCount) {
        List<Party> partyList = partyRepository.findAllByOttAndPresentHeadcountLessThanAndMatchingTypeOrderByRegYmd(ott, maxHeadCount, 2);
        List<PartyDTO> response = new ArrayList<>();
        for (Party party : partyList) {
            response.add(PartyDTO.builder()
                    .partyId(party.getPartyId())
                    .ott(party.getOtt())
                    .leaderId(party.getLeaderId())
                    .presentHeadcount(party.getPresentHeadcount())
                    .maxHeadcount(party.getMaxHeadcount())
                    .regYmd(party.getRegYmd())
                    .ottAccId(party.getOttAccId())
                    .ottAccPw(party.getOttAccPw())
                    .matchingType(party.getMatchingType())
                    .title(party.getTitle())
                    .partyDetail(party.getPartyDetail())
                    .price(party.getPrice())
                    .period(party.getPeriod())
                    .partyNotice(party.getPartyNotice())
                    .build());
        }
        return response;
    }

    @Override
    public long getMatchingSize(String key) {
        return redisUtil.getListSize(key);
    }

    @Override
    @Transactional
    public void matchingParty(List<PartyDTO> partyDTOList, Ott ott, long waitingMemberCount) {
        Queue<PartyDTO> partyQueue = new LinkedList<>(partyDTOList);
        boolean last = false;

        String memberId = redisUtil.getList(MATCHING_KEY_PREFIX + ott.getOttId());
        waitingMemberCount--;
        log.info(partyQueue.size()+"");
        while (!partyQueue.isEmpty() && waitingMemberCount >= 0) {
            PartyDTO party = partyQueue.poll();
            final Long partyId = party.getPartyId();

            // 파티 가입에 성공하는 경우
            try {
                PartyDTO joinParty = isPartyJoin(partyId, memberId);
                log.info("joinParty");
                // 알림 데이터
                String notifyType = "PARTY";
                String notifyDetail = "매칭이 성공되었습니다.\n 마이페이지에서 파티를 확인해보세요";
                Long notifyTypeId = partyId;

                final String billingKey = redisUtil.getData("billing:" + memberId);
                final String merchantUID = memberId + "-" + partyId + "-" + importAPI.randomString();
                final int price = (int) (ott.getOttPrice() / ott.getOttHeadcount());

                // 결제 진행
                Map<String, Object> paymentResponse = paymentService.autoPayment(billingKey, merchantUID, price, memberId);
                log.info(paymentResponse.toString());
                // 결제 성공 시
                if (paymentResponse != null) {
                    // 결제 이력을 넣어준다.
                    paymentService.paymentLogSave(paymentResponse.get("imp_uid").toString(), memberId, partyId, "정기 결제입니다.", price);

                    // 정기 결제이기 때문에 다음 결제 스케줄링을 걸어준다.
                    String merchantId = paymentService.schedulePayment(billingKey, merchantUID, price);

                    // 정기 결제 시 사용할 빌링키를 등록한다.
                    paymentService.saveBilling(memberId, aesUtil.decrypt(billingKey), merchantId, partyId);
                }
                // 결제 실패 한 경우
                else {
                    notifyDetail = "결제 오류!\n 결제 카드를 확인하시고 다시 매칭 해주세요.";
                    notifyType = "BILLING";
                    notifyTypeId = 0L;

                    isRollBackPartyJoin(party.getPartyId(), memberId);
                }

                // 알림 전송
                try {
                    matchingProducer.sendNotify(MatchingRequest.builder()
                            .notifySender("manager")
                            .notifyReceiver(memberId)
                            .notifyDetail(notifyDetail)
                            .notifyType(notifyType)
                            .notifyTypeId(notifyTypeId)
                            .build());
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

                // 파티원 참가 성공 후 파티에 빈자리 있으면 다시 파티큐에 넣어준다.
                if(joinParty.getMaxHeadcount() > joinParty.getPresentHeadcount())
                    partyQueue.add(joinParty);

                //개인 매칭 열 확인 후 삭제
                redisUtil.lRem("matching:"+memberId, 0, String.valueOf(ott.getOttId()));
                if(redisUtil.getListSize("matching:"+memberId)==0) {
                    redisUtil.deleteList("matching:"+memberId);
                    redisUtil.deleteData("billing:"+memberId);
                }

                // 다음 사람 매칭
                memberId = redisUtil.getList(MATCHING_KEY_PREFIX + ott.getOttId());
                waitingMemberCount--;
                last = false;
            }
            // 캐치 수정
            catch (Exception e) {
                last = true;
                e.printStackTrace();
            }
        }
        // while end
        // 마지막 인원이 대기열에서 빠졌지만, 파티에 가입 안되는 경우 다시 대기열에 넣어준다.
        if(last) redisUtil.leftPush(MATCHING_KEY_PREFIX + ott.getOttId(), memberId);
    }

    @Override
    public void setMatching(long ottId, String billingKey, String memberId) {
        redisUtil.rightPush("matching:" + ottId, memberId);
        redisUtil.rightPush("matching:"+memberId, String.valueOf(ottId));
//        redisUtil.setData("billing:" + memberId, billingKey);
        redisUtil.setData("billing:" + memberId, aesUtil.encrypt(billingKey));
    }
}
