# 회원 (Member) API

## 1. 회원가입

새로운 회원을 등록합니다.

**Endpoint**
```
POST /api/v1/members/sign-up
```

**Request Body**
| 필드 | 타입 | 필수 | 설명 | 예시 |
|------|------|------|------|------|
| koreanName | String | O | 한글 이름 | 홍길동 |
| englishName | String | O | 영문 이름 | Hong Gildong |
| email | String | O | 이메일 주소 | test@example.com |
| password | String | O | 비밀번호 | Password1! |

**비밀번호 규칙**
- 최소 8자 이상
- 영문자 1개 이상 포함
- 숫자 1개 이상 포함
- 특수문자(@$!%*#?&) 1개 이상 포함

**Request 예시**
```json
{
  "koreanName": "홍길동",
  "englishName": "Hong Gildong",
  "email": "test@example.com",
  "password": "Password1!"
}
```

**Response (성공)**
```json
{
  "type": "SUCCESS",
  "exception": null,
  "item": {
    "id": 1
  }
}
```

**에러 케이스**
| 에러 번호 | 메시지 |
|-----------|--------|
| MEMBER-2 | 비밀번호는 최소 8자 이상이며, 영문자, 숫자, 특수문자를 각각 하나 이상 포함해야 합니다. |
| MEMBER-4 | 이미 사용 중인 이메일입니다. |

---

## 데이터 모델

### Member (회원)
| 필드 | 타입 | 설명 |
|------|------|------|
| id | Long | 회원 고유 ID |
| koreanName | String | 한글 이름 |
| englishName | String | 영문 이름 |
| email | String | 이메일 |
| password | String | 비밀번호 (암호화) |
| createdAt | DateTime | 생성일시 |
| updatedAt | DateTime | 수정일시 |
| deletedAt | DateTime | 삭제일시 (Soft Delete) |