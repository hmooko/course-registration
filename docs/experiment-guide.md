# 수강신청 조회 캐시 성능 비교 실험 가이드

## 1. 실험 목적

동일한 과목 조회 API를 세 가지 방식으로 구현한 뒤, 읽기 집중 상황에서 응답 시간과 처리량 차이를 측정한다.

- No Cache: `/api/v1/courses/no-cache`
- Caffeine: `/api/v1/courses/caffeine`
- Redis: `/api/v1/courses/redis`

## 2. 비교 대상과 가설

- No Cache는 모든 요청이 DB를 직접 조회하므로 응답 시간이 가장 길고 DB 부하가 가장 크다.
- Caffeine은 단일 애플리케이션 인스턴스 내부 메모리를 사용하므로 가장 낮은 조회 지연 시간을 보일 가능성이 높다.
- Redis는 네트워크 홉이 추가되지만 DB 부하를 크게 줄일 수 있어, 분산 환경에서 더 현실적인 대안이다.

## 3. 실험 전 준비

### 애플리케이션 실행

```bash
./gradlew bootRun
```

서버 기동 시 1만 건의 과목 더미 데이터가 H2 DB에 적재된다.

### Redis 실행

Redis 비교 실험 전에는 로컬 Redis 서버가 필요하다.

예시:

```bash
docker run --name course-registration-redis -p 6379:6379 redis:7
```

## 4. JMeter 테스트 파일

JMeter 테스트 플랜 파일:

- [`performance/jmeter/course-cache-comparison.jmx`](/Users/koohyunmo/Developer/course-registration/performance/jmeter/course-cache-comparison.jmx)

이 플랜은 `apiPath`만 바꿔서 세 실험을 같은 조건으로 반복 실행하도록 설계되어 있다.

## 5. 권장 실행 순서

캐시 비교 실험은 반드시 독립적으로 실행한다. 세 엔드포인트를 한 번에 섞어서 호출하면 캐시 워밍업 상태가 뒤섞여 결과가 오염된다.

권장 순서:

1. 애플리케이션 재시작
2. 대상 엔드포인트 1회 수동 호출 여부 결정
3. JMeter로 단일 전략만 실행
4. 결과 저장
5. 애플리케이션과 Redis 상태 초기화
6. 다음 전략 반복

## 6. 실행 예시

결과 저장 디렉터리 생성:

```bash
mkdir -p performance/results
```

### 6-1. No Cache

```bash
jmeter -n \
  -t performance/jmeter/course-cache-comparison.jmx \
  -Jprotocol=http \
  -Jhost=localhost \
  -Jport=8080 \
  -JapiPath=/api/v1/courses/no-cache \
  -Jusers=300 \
  -JrampUp=30 \
  -Jloops=20 \
  -l performance/results/no-cache.jtl \
  -e -o performance/results/no-cache-report
```

### 6-2. Caffeine

```bash
jmeter -n \
  -t performance/jmeter/course-cache-comparison.jmx \
  -Jprotocol=http \
  -Jhost=localhost \
  -Jport=8080 \
  -JapiPath=/api/v1/courses/caffeine \
  -Jusers=300 \
  -JrampUp=30 \
  -Jloops=20 \
  -l performance/results/caffeine.jtl \
  -e -o performance/results/caffeine-report
```

### 6-3. Redis

```bash
jmeter -n \
  -t performance/jmeter/course-cache-comparison.jmx \
  -Jprotocol=http \
  -Jhost=localhost \
  -Jport=8080 \
  -JapiPath=/api/v1/courses/redis \
  -Jusers=300 \
  -JrampUp=30 \
  -Jloops=20 \
  -l performance/results/redis.jtl \
  -e -o performance/results/redis-report
```

## 7. 논문에 기록할 핵심 지표

- Average Response Time
- Median
- 90% Line
- 95% Line
- 99% Line
- Throughput
- Error %

가능하면 아래 항목도 같이 수집한다.

- DB CPU 사용량
- DB 커넥션 사용량
- Redis 메모리 사용량
- 애플리케이션 힙 사용량

## 8. 실험 통제 조건

논문에서는 아래 조건을 동일하게 유지해야 한다.

- 데이터 수: 과목 10,000건
- JMeter 동시 사용자 수
- Ramp-up 시간
- Loop 횟수
- 서버 JVM 옵션
- Redis 버전
- 측정 시점의 캐시 상태

## 9. 캐시 워밍업 전략

캐시 방식은 워밍업 여부에 따라 결과 차이가 크다. 따라서 아래 두 실험 중 하나를 명확히 선택해야 한다.

- Cold Start 실험: 서버 재기동 후 바로 부하 테스트 수행
- Warm Cache 실험: 동일 엔드포인트를 1회 이상 선호출한 뒤 부하 테스트 수행

논문에서는 두 결과를 분리해 제시하는 편이 더 설득력 있다.

## 10. 결과 정리 표 예시

| 전략 | 평균 응답시간(ms) | p95(ms) | TPS | 에러율(%) | 비고 |
| --- | ---: | ---: | ---: | ---: | --- |
| No Cache |  |  |  |  | DB 직조회 |
| Caffeine |  |  |  |  | 로컬 메모리 캐시 |
| Redis |  |  |  |  | 글로벌 캐시 |

## 11. 해석 포인트

- Caffeine이 가장 빠르더라도 멀티 인스턴스 환경에서는 캐시 일관성 관리가 어렵다.
- Redis는 네트워크 비용이 있지만 확장성과 공유 캐시에 강점이 있다.
- No Cache는 구현이 단순하지만 읽기 폭주 시 DB 병목을 직접 유발한다.

## 12. 다음 권장 작업

- JMeter 결과를 자동으로 비교 정리하는 스크립트 추가
- 캐시 무효화 API 또는 수강신청 발생 시 캐시 삭제 로직 추가
- 논문 본문용 실험 환경 표와 결과 표 초안 작성
