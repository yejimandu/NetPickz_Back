# 🎬 NetPickz_Back

## 📌 프로젝트 소개
NetPickz는 TMDB API를 활용해 사용자에게 영화 추천을 제공하는 백엔드 프로젝트입니다.  
Spring Boot 기반으로 사용자 인증, 영화 정보 조회, 추천 기능을 제공합니다.

## 📅 프로젝트 기간
2025년 9월 (7일) ~ (진행 중) 목표 11월 말 12월초

## 👤 맡은 역할(전체)
- 백엔드 개발 전반 담당
  - 프로젝트 구조 설계 및 초기 세팅
  - TMDB API 연동 및 응답 구조 설계
  - 사용자 인증 흐름 설계 (JWT 예정)
  - Swagger 문서화 (SpringDoc 기반)
  - 기능 로그 및 Daily Log 작성
  - Git 브랜치 전략 수립 및 버전 관리
- 프런트 개발 전반 담당(예정)
- DB 전반 담당(예정)


## 🧱 기술 스택
- Java 17
- Spring Boot 3.x
- Gradle
- JPA (Hibernate)
- Spring Security (JWT 예정)
- SpringDoc OpenAPI (Swagger UI)
- WebClient
- TMDB API

## 📁 프로젝트 구조(계속 수정 예정)
```bash
├── src/main/java/
│   └── com.netpickz/
        │
│       ├── api/        # 컨트롤러 계층
│       ├── core/       # 도메인별 엔티티, DTO, 서비스, 레포지토리 
│       └── common/     # 설정 및 공통 유틸
└── resources/
│       └── application.yml

```

## 기능 목록(예정)

## 🗂️ 브랜치 전략
- main: 배포용 안정 버전
- dev: 개발 통합 브랜치
- feature/*: 기능 단위 작업 브랜치

## 🧠 개발 로그
추후 링크 연결 예정(Daily 로그 및 기능 로그 확인 가능)


## 🤖 개발 지원 도구
- Microsoft Copilot을 활용해 프로젝트 구조부터 기능 흐름, 문서 구성 등에서 고민되는 부분에 대해 AI 조언을 참고해 개발 방향을 설정함.
