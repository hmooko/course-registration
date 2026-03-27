# JMeter 결과 요약 가이드

## 1. 목적

JMeter가 생성한 `.jtl` 파일을 논문 표에 바로 옮길 수 있는 형태로 요약한다.

요약 스크립트:

- [`summarize_jmeter_jtl.py`](/Users/koohyunmo/Developer/course-registration/scripts/summarize_jmeter_jtl.py)

## 2. 요약 대상 지표

스크립트는 아래 지표를 출력한다.

- 총 요청 수
- 성공 요청 수
- 실패 요청 수
- 에러율
- 평균 응답시간
- 중앙값
- p90
- p95
- p99
- 최소 응답시간
- 최대 응답시간
- TPS

## 3. 실행 예시

### No Cache

```bash
python3 scripts/summarize_jmeter_jtl.py performance/results/no-cache.jtl --label no-cache
```

### Caffeine

```bash
python3 scripts/summarize_jmeter_jtl.py performance/results/caffeine.jtl --label caffeine
```

### Redis

```bash
python3 scripts/summarize_jmeter_jtl.py performance/results/redis.jtl --label redis
```

## 4. 출력 예시 형식

```text
전략: no-cache
파일: performance/results/no-cache.jtl
총 요청 수: 6000
성공 요청 수: 6000
실패 요청 수: 0
에러율(%): 0.00
평균 응답시간(ms): 1234.56
중앙값(ms): 1180.00
p90(ms): 1600.00
p95(ms): 1750.00
p99(ms): 2200.00
최소(ms): 250
최대(ms): 3100
TPS: 97.50
```

## 5. 논문 표 반영 예시

| 전략 | 평균 응답시간(ms) | p95(ms) | p99(ms) | TPS | 에러율(%) |
| --- | ---: | ---: | ---: | ---: | ---: |
| No Cache |  |  |  |  |  |
| Caffeine |  |  |  |  |  |
| Redis |  |  |  |  |  |

## 6. 해석 시 주의

- TPS는 `전체 요청 수 / 전체 측정 시간` 기준으로 계산된다.
- 에러가 포함된 실험은 응답시간 해석보다 실패 원인 분석을 먼저 해야 한다.
- Redis와 Caffeine은 cold/warm 상태를 구분해서 결과를 적는 편이 더 정확하다.
