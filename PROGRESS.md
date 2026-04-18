# 🚀 Project Progress: Smart WMS ERP

이 파일은 작업의 연속성을 위해 Gemini CLI가 읽고 참조하는 진행 현황판입니다. 퇴근 전/출근 후 반드시 최신화하여 Git에 공유합니다.

---

## 📌 현재 상태 (Last Updated: 2026-04-14)
- **개발 단계:** Phase 2 - 영업관리 및 물류 프로세스 통합 완료
- **주요 마일스톤:** 백엔드 핵심 엔진 구축, BO(Back Office) 주요 화면 연동, 빌드 안정화 완료

## ✅ 완료된 작업 (2026-04-14)
- [x] **백엔드 엔진 구축:**
    - `PricingService`: 다중 통화 환율 및 마진 시뮬레이션
    - `OfferService`: 품번 자동 정제(`PartNumberUtil`) 및 오퍼 관리
    - `PurchaseService`: P/I -> P/O 자동 전환 및 Vendor 매칭
    - `WmsService`: 입고 확정, 반품, 재고 실사 조정 로직
    - `OutboundService`: **FIFO(선입선출)** 기반 출고 할당 및 Picking List 생성
    - `ReportService`: 경영진용 대시보드 통계 API (성공률, 매출/매입, 재고부족)
- [x] **영업 관리 엑셀 자동화 (upload_data 생성):**
    - `export_data.xlsx` 원본 업로드 및 자동 분석 로직 구현 (`InquiryExcelService`)
    - `OfferItem` 엔티티 고도화 (20여개 엑셀 컬럼 필드 추가)
    - 거래처 미지정 업로드 및 자동 매핑 기능 추가
    - 가공된 `upload_data.xlsx` 다운로드 API 구현
    - 인콰이어리 상세 조회 화면 및 완전 삭제 기능 추가
- [x] **도메인 모델 고도화:** Partner, Item, Offer, PO, Stock, StockLot 등 10여개 엔티티 및 Repository 구축
- [x] **빌드 오류 해결:** 패키지 이동에 따른 Import 오류 수정 및 Lombok `@Builder` 경고 해결
- [x] **BO 프론트엔드 연동:**
    - 경영 대시보드 (`Dashboard.vue`)
    - 영업 관리 (`OfferManagement.vue`)
    - 입고 관리 (`InboundManagement.vue`)
    - 품목 마스터 (`ItemManagement.vue`)
    - 거래처 관리 (`PartnerManagement.vue`)
    - 재고 현황 (`StockStatus.vue`)
- [x] **인프라/환경 설정:** 
    - `npm install` 버전 충돌 해결 (`@vitejs/plugin-basic-ssl` 하향 조정)
    - Vite Proxy 설정을 통한 BE-FE 연동 확인

## 🚧 진행 중 / 문제 해결
- **모바일 PDA 기능:** 기존 모바일 기능(React)을 고도화된 백엔드 엔진과 재연동 필요.
- **데이터 정합성 테스트:** 실제 시나리오(부분입고, 취소 등)에 대한 엔드투엔드 테스트 필요.

## 📅 다음 목표 (Next Steps)
1. **모바일(PDA) 고도화:** 고도화된 WMS 엔진 기반으로 모바일 입출고 스캔 화면 재구축.
2. **엑셀 업로드 UI:** BO 영업 관리 화면에서 인콰이어리 엑셀 대량 업로드 UI 구현.
3. **권한 관리:** 관리자/영업/창고 담당자별 접근 권한 설정.

---

## 🤖 Gemini CLI를 위한 가이드
새로운 세션을 시작할 때, 이 파일을 읽고 다음 질문에 답하며 업무를 재개하세요:
1. "마지막으로 완료된 작업은 무엇인가?" (Phase 2 통합 완료)
2. "다음에 즉시 시작해야 할 우선순위 작업은 무엇인가?" (모바일 PDA 연동)
3. "이전 세션에서 해결하지 못한 기술적 난제나 메모가 있는가?" (없음, 빌드 성공 상태)
