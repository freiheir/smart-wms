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

## 📂 2. Backend Layering Pattern
- **`domain`:** JPA 엔티티 및 비즈니스 도메인 모델. 핵심 비즈니스 로직은 가능한 엔티티 내부에 구현.
- **`repository`:** Spring Data JPA를 통한 데이터 접근 계층.
- **`service`:** 트랜잭션 경계 설정 및 여러 도메인 서비스 간의 조율.
- **`controller`:** REST API 엔드포인트. DTO 변환 및 API 명세 관리.
- **`config`:** 보안, CORS, 데이터 초기화 등 설정 클래스.

---

## 📊 3. Data Model (Entity Relationship)

### [Item] - 상품 마스터
- `id` (Long, PK): 자동 생성 아이디
- `itemCode` (String, Unique): 상품 식별 코드 (예: SKU-001)
- `itemName` (String): 상품명
- `description` (String): 상세 설명
- `price` (Integer): 단가
- `stockQuantity` (Integer): **[핵심]** 현재 가용 재고 수량
- `itemUnit` (String, Not Null): 상품 단위 (EA, BOX, PLT 등)
- `barcode` (String, Unique): 상품 바코드
- `useYn` (String, Default 'Y'): 사용 여부
- `category` (String): 상품 카테고리
- `createdAt` (LocalDateTime): 생성 일시 (Auditing)
- `updatedAt` (LocalDateTime): 수정 일시 (Auditing)

---

## 📡 4. Core API Specification
### [Items]
- `GET /api/items`: 상품 전체 목록 조회
- `GET /api/items/search?code={code}`: 상품코드 또는 바코드로 상품 정보 조회
- `POST /api/items`: 신규 상품 등록
- `PUT /api/items/{id}`: 상품 정보 수정
- `DELETE /api/items/{id}`: 상품 삭제 (사용여부 'N' 처리)

---

## 📏 5. Core Business Rules (Immutable)
1. **재고 정합성:** `stockQuantity`는 어떠한 경우에도 물리적인 입출고 내역 없이 직접 수정해서는 안 됨 (추후 트랜잭션 로그 기반 관리).
2. **코드 체계:** 모든 마스터 데이터(상품, 로케이션 등)는 고유한 식별 코드(`code`)를 가져야 하며, 이는 사람이 읽을 수 있는 형식이어야 함.
3. **확장성:** 모든 테이블은 등록일시(`createdAt`), 수정일시(`updatedAt`)를 포함하는 공통 감시(Audit) 필드를 고려함.

---

## 🛠️ 6. Implementation Workflow
1. **분석:** 새로운 기능을 추가하기 전, 기존 도메인 모델과의 연관성을 확인한다.
2. **설계:** `ARCHITECTURE.md`에 새로운 테이블이나 로직을 업데이트한다.
3. **구현:** 설계된 내용을 바탕으로 코드를 작성한다.
4. **검증:** 전체 데이터 정합성이 깨지지 않았는지 테스트한다.
