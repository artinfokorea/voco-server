# 코드 컨벤션

## 구조

- presentation
    - controller
        - dto
            - in
                - ...Reqeust
            - out
                - ...Response
- application
    - interfaces
    - usecase
        - dto
            - in
                - ...UseCaseDto
            - out
                - ...Info
        - ...UseCase
- domain
    - model
        - ...Entity
    - interfaces
        - ...CommandRepository
        - ...QueryRepository
- infrastructure
    - adaptor
    - repository
        - ...CommandRepositoryImpl
        - ...QueryRepositoryImpl

## 의존 관계

- presentation -> application
- application -> domain
- infrastructure -> application, domain
                    