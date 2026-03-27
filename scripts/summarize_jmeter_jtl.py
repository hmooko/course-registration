#!/usr/bin/env python3
from __future__ import annotations

import argparse
import csv
import math
from pathlib import Path


def percentile(sorted_values: list[int], q: float) -> float:
    if not sorted_values:
        return 0.0
    if len(sorted_values) == 1:
        return float(sorted_values[0])

    position = (len(sorted_values) - 1) * q
    lower_index = math.floor(position)
    upper_index = math.ceil(position)

    if lower_index == upper_index:
        return float(sorted_values[lower_index])

    lower_value = sorted_values[lower_index]
    upper_value = sorted_values[upper_index]
    return lower_value + (upper_value - lower_value) * (position - lower_index)


def read_jtl(jtl_path: Path) -> tuple[list[int], int, int, int, int]:
    elapsed_times: list[int] = []
    success_count = 0
    failure_count = 0
    first_timestamp: int | None = None
    last_timestamp: int | None = None

    with jtl_path.open("r", encoding="utf-8", newline="") as file:
        reader = csv.DictReader(file)
        required_headers = {"timeStamp", "elapsed", "success"}
        missing_headers = required_headers.difference(reader.fieldnames or [])
        if missing_headers:
            missing_text = ", ".join(sorted(missing_headers))
            raise ValueError(f"JTL 파일에 필요한 헤더가 없습니다: {missing_text}")

        for row in reader:
            timestamp = int(row["timeStamp"])
            elapsed = int(row["elapsed"])
            success = row["success"].strip().lower() == "true"

            elapsed_times.append(elapsed)
            if success:
                success_count += 1
            else:
                failure_count += 1

            if first_timestamp is None or timestamp < first_timestamp:
                first_timestamp = timestamp
            if last_timestamp is None or timestamp > last_timestamp:
                last_timestamp = timestamp

    if first_timestamp is None or last_timestamp is None:
        raise ValueError("JTL 파일에 샘플 데이터가 없습니다.")

    return elapsed_times, success_count, failure_count, first_timestamp, last_timestamp


def summarize(jtl_path: Path, label: str) -> str:
    elapsed_times, success_count, failure_count, first_timestamp, last_timestamp = read_jtl(jtl_path)

    elapsed_times.sort()
    total_count = len(elapsed_times)
    average = sum(elapsed_times) / total_count
    median = percentile(elapsed_times, 0.50)
    p90 = percentile(elapsed_times, 0.90)
    p95 = percentile(elapsed_times, 0.95)
    p99 = percentile(elapsed_times, 0.99)
    min_value = elapsed_times[0]
    max_value = elapsed_times[-1]

    duration_ms = max(last_timestamp - first_timestamp, 1)
    throughput = total_count / (duration_ms / 1000)
    error_rate = (failure_count / total_count) * 100

    lines = [
        f"전략: {label}",
        f"파일: {jtl_path}",
        f"총 요청 수: {total_count}",
        f"성공 요청 수: {success_count}",
        f"실패 요청 수: {failure_count}",
        f"에러율(%): {error_rate:.2f}",
        f"평균 응답시간(ms): {average:.2f}",
        f"중앙값(ms): {median:.2f}",
        f"p90(ms): {p90:.2f}",
        f"p95(ms): {p95:.2f}",
        f"p99(ms): {p99:.2f}",
        f"최소(ms): {min_value}",
        f"최대(ms): {max_value}",
        f"TPS: {throughput:.2f}",
    ]
    return "\n".join(lines)


def main() -> None:
    parser = argparse.ArgumentParser(
        description="JMeter JTL 결과를 논문용 지표로 요약합니다."
    )
    parser.add_argument("jtl", type=Path, help="요약할 JTL 파일 경로")
    parser.add_argument(
        "--label",
        default="unnamed",
        help="출력에 표시할 실험 이름 예: no-cache, caffeine, redis",
    )
    args = parser.parse_args()

    print(summarize(args.jtl, args.label))


if __name__ == "__main__":
    main()
