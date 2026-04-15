# 🏗️ System Architecture & Design Guidelines

이 문서는 Smart WMS ERP의 시스템 구조와 설계 원칙을 정의합니다. 모든 코드 변경 및 기능 추가 시 이 문서를 최우선으로 참조합니다.

---

## 🏛️ 1. Technical Stack & Ports
- **Backend:** Java (Spring Boot 3.x), JPA/Hibernate - **Port: 5182**
- **Database:** PostgreSQL 16
- **Frontend (Mobile):** React (TypeScript), Vite - **Port: 5180 (HTTPS)**
- **Back Office (Admin):** Vue 3 (TypeScript), Vite - **Port: 5181 (HTTPS)**
- **Infrastructure:** Docker Compose

---

## 📂 2. Backend Layering & Package Structure
기존 레이어드 패턴을 유지하되, 업무 도메인별로 패키지를 세분화하여 관리합니다.

- **`com.smart.wmserp.domain`:**
    - `master`: 상품(Item), 거래처(Partner), 환율(ExchangeRate) 등 기초 마스터 정보
    - `sales`: 인콰이어리 및 오퍼(Offer), 오퍼 품목(OfferItem)
    - `purchase`: 발주서(PurchaseOrder), 발주 품목(PurchaseOrderItem)
    - `wms`: 입고예정(InboundExpected), 재고(Stock), 재고로트(StockLot), 출고피킹(PickingList)
- **`com.smart.wmserp.service`:** 도메인 간 조율 및 복합 비즈니스 로직 (Pricing, Outbound FIFO 등)
- **`com.smart.wmserp.api`:** REST API 엔드포인트 및 외부 연동 컨트롤러

---

## 📊 3. Data Model (Entity Relationship)

### [Master Data]
- **Item (상품):** SKU 관리, 바코드, 단위, 카테고리 정보
- **Partner (거래처):** 매출처/매입처 구분, 통화(Currency), 결제 조건 관리
- **ExchangeRate (환율):** 일자별/통화별 환율 정보

### [Sales & Purchase]
- **Offer (오퍼):** 인콰이어리 번호 기반 관리, 상태(INQUIRY -> OFFER -> PI_CONVERTED), **담당자 이메일(`managerEmail`)**, **전체 비고(`remarks`)**
- **PurchaseOrder (발주):** P/I 기반 자동 생성, Vendor 매칭, 입고 예정 정보와 연결

### [WMS & Inventory]
- **Stock (현재고):** 상품별 실시간 가용 재고 합계
- **StockLot (재고로트):** **[핵심]** 입고 일자별 로트 관리 (FIFO 출고의 기반)
- **InboundExpected (입고예정):** 발주 기반 입고 대기 정보
- **OfferItem (오퍼 품목):** **바이어 품목명(`buyerItemName`)**, **품목별 비고(`lineRemarks`)** 포함

---

## 📡 4. Core API Specification

### [Sales / Offer Management]
- `POST /api/sales/offers/upload-excel`: 인콰이어리 엑셀 대량 업로드
- `POST /api/sales/offers/{id}/convert-to-pi`: 오퍼를 P/I(Proforma Invoice)로 확정
- `POST /api/sales/offers/{id}/generate-po`: P/I 기반 발주서 자동 생성 및 WMS 연동

### [WMS / Inventory Management]
- `GET /api/wms/inbound/expected`: 입고 대기 목록 조회
- `POST /api/wms/inbound/confirm`: 입고 확정 처리 (실제 재고 및 로트 생성)
- `POST /api/wms/stock/adjust`: 재고 실사 조정 (사유 포함)
- `GET /api/items/search?code={code}`: 바코드/품번 기반 상품 검색

---

## 📏 5. Core Business Rules (Immutable)
1. **FIFO (선입선출):** 모든 출고 할당은 `StockLot`의 입고 일시가 빠른 순서대로 자동 할당됨.
2. **재고 정합성:** `Stock` 수량은 반드시 `StockHistory`와 일치해야 하며, 물리적 트랜잭션 없이 수정 불가.
3. **통화 관리:** 모든 매입/매출 전표는 마스터에 정의된 거래처별 기본 통화를 따르며, 환율 서비스에 의해 원화 환산 처리됨.
4. **코드 자동화:** 품번(Part Number)은 대문자 및 특수문자 제거 규칙(`PartNumberUtil`)을 통해 정규화되어 관리됨.

---

## 🛠️ 6. Implementation Workflow
1. `ARCHITECTURE.md` 및 `PROGRESS.md` 로드하여 시스템 구조와 현황 파악
2. 도메인 모델(Entity) 설계 및 DB 스키마 반영
3. 비즈니스 로직(Service) 구현 및 단위 테스트
4. API(Controller) 노출 및 프론트엔드 연동
5. 검증 후 문서 최신화
