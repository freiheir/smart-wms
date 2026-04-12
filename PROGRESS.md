# 🚀 Project Progress: Smart WMS ERP

이 파일은 작업의 연속성을 위해 Gemini CLI가 읽고 참조하는 진행 현황판입니다. 퇴근 전/출근 후 반드시 최신화하여 Git에 공유합니다.

---

## 📌 현재 상태 (Last Updated: 2026-04-12)
- **환경 설정 단계:** 로컬 개발 환경 구성 및 실행 확인 완료
- **주요 마일스톤:** Docker 인프라, 백엔드 빌드, 프론트/BO 실행 확인

## ✅ 완료된 작업
- [x] **인프라 설정:** `docker-compose.yml`을 통한 PostgreSQL 컨테이너 구동 확인 (`port: 5432`)
- [x] **백엔드 빌드:** Gradle을 이용한 `clean build` 및 `bootRun` 실행 확인
- [x] **프론트엔드(React):** Vite 기반 개발 서버 실행 확인 (`npm run dev`)
- [x] **백오피스(Vue):** Vite 기반 개발 서버 실행 확인 (`npm run dev`)
- [x] **연속성 도구:** `PROGRESS.md` 도입을 통한 업무 이관 체계 마련

## 🚧 진행 중 / 문제 해결
- **Docker 실행 오류:** Docker Desktop 미구동으로 인한 접속 불가 현상 확인 -> Docker Desktop 실행 후 재시도 필요 안내 완료.
- **기기 간 동기화:** 회사-집 간의 페르소나 및 진행 상황 공유 전략 수립 (Git 기반 `GEMINI.md` 및 `PROGRESS.md` 활용).

## 📅 다음 목표 (Next Steps)
1. **전체 시스템 연동 테스트:** DB-Backend-Frontend(React/Vue)가 모두 켜진 상태에서 데이터 통신 확인.
2. **API 명세 확인:** 백엔드에서 제공하는 기본 API 리스트 및 Swagger(있는 경우) 확인.
3. **페르소나 이관:** 회사 PC에서 Gemini의 페르소나/지침을 `GEMINI.md`로 추출하여 Git에 반영하기.

---

## 🤖 Gemini CLI를 위한 가이드
새로운 세션을 시작할 때, 이 파일을 읽고 다음 질문에 답하며 업무를 재개하세요:
1. "마지막으로 완료된 작업은 무엇인가?"
2. "다음에 즉시 시작해야 할 우선순위 작업은 무엇인가?"
3. "이전 세션에서 해결하지 못한 기술적 난제나 메모가 있는가?"
