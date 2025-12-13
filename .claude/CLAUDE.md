# 코드 컨벤션

## 구조

- presentation(입출력 변환, 요청 검증, UseCase 호출  )
    - controller
        - dto
            - in
                - ...Reqeust
            - out
                - ...Response
- application(외부 레파지토리 접근, 흐름 제어, 트랜잭션 관리)
    - interfaces
    - usecase
        - dto
            - in
                - ...UseCaseDto
            - out
                - ...Info
        - ...UseCase
- domain(자기 상태 관리, 자기 상태를 기반으로 한 판단)
    - service(도메인 규칙/정책, 여러 엔티티 및 외부 리소스를 통한 판단)
    - model
        - ...Entity
    - interfaces
        - ...CommandRepository
        - ...QueryRepository
- infrastructure(기술적 세부사항을 구현)
    - adaptor
    - repository
        - ...CommandRepositoryImpl
        - ...QueryRepositoryImpl

## 의존 관계

- presentation -> application
- application -> domain
- infrastructure -> application, domain

## 확인

- 각 컨텍스트별로 의존은 지양한다(컨텍스트별로 읽기만 의존 가능, MSA 고려)
- API 는 Restfull Api를 지향한다.
- 사용하지 않는 코드는 항상 지워준다.
- 통합 테트스 생성 시 각 통합테스트별로 의존을 지양한다.
- 테스트 코드 이외에 운영 코드에 주석은 최소화 한다.
                    