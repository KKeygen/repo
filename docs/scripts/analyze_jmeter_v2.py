#!/usr/bin/env python3
import csv
import gzip
import json
import math
from collections import Counter, defaultdict
from pathlib import Path


ROOT = Path(__file__).resolve().parents[2]
JTL = ROOT / "docs" / "jmeter_result2.jtl.gz"
OUT = ROOT / "docs" / "assets" / "jmeter_v2"


def percentile(values, p):
    values = sorted(values)
    if not values:
        return None
    k = (len(values) - 1) * p / 100
    lo = math.floor(k)
    hi = math.ceil(k)
    if lo == hi:
        return values[lo]
    return values[lo] * (hi - k) + values[hi] * (k - lo)


def fmt_ms(value):
    return int(round(value))


def load_rows():
    rows = []
    row_lengths = Counter()
    with gzip.open(JTL, "rt", newline="") as fh:
        reader = csv.reader(fh)
        header = next(reader)
        for row in reader:
            row_lengths[len(row)] += 1
            if len(row) != 13:
                continue
            rec = dict(zip(header, row))
            for key in ("timeStamp", "elapsed", "bytes", "grpThreads", "allThreads", "Latency", "IdleTime"):
                rec[key] = int(rec[key] or 0)
            rows.append(rec)
    return rows, row_lengths


def summarize(rows):
    latencies = [r["elapsed"] for r in rows]
    start = min(r["timeStamp"] for r in rows)
    end = max(r["timeStamp"] + r["elapsed"] for r in rows)
    return {
        "count": len(rows),
        "duration_s": round((end - start) / 1000, 3),
        "throughput": round(len(rows) / ((end - start) / 1000), 2),
        "min_ms": min(latencies),
        "avg_ms": round(sum(latencies) / len(latencies), 1),
        "p50_ms": fmt_ms(percentile(latencies, 50)),
        "p75_ms": fmt_ms(percentile(latencies, 75)),
        "p90_ms": fmt_ms(percentile(latencies, 90)),
        "p95_ms": fmt_ms(percentile(latencies, 95)),
        "p99_ms": fmt_ms(percentile(latencies, 99)),
        "p999_ms": fmt_ms(percentile(latencies, 99.9)),
        "max_ms": max(latencies),
        "first_start": start,
        "last_start": max(r["timeStamp"] for r in rows),
        "last_end": end,
    }


def svg_frame(title, width=960, height=420):
    return [
        f'<svg xmlns="http://www.w3.org/2000/svg" width="{width}" height="{height}" viewBox="0 0 {width} {height}">',
        '<rect width="100%" height="100%" fill="#ffffff"/>',
        '<style>text{font-family:Inter,Arial,sans-serif;fill:#142033} .muted{fill:#5d6878} .axis{stroke:#d7dde7;stroke-width:1} .grid{stroke:#eef2f7;stroke-width:1} .blue{fill:#2f6fc9} .cyan{fill:#168aad} .green{fill:#2d8a63} .amber{fill:#c47b20} .red{fill:#be3a3a} .line{fill:none;stroke-width:3;stroke-linejoin:round;stroke-linecap:round}</style>',
        f'<text x="28" y="34" font-size="22" font-weight="800">{title}</text>',
    ]


def write_svg(path, lines):
    path.write_text("\n".join(lines + ["</svg>\n"]), encoding="utf-8")


def chart_throughput(success_rows):
    start = min(r["timeStamp"] for r in success_rows)
    buckets = Counter((r["timeStamp"] - start) // 1000 for r in success_rows)
    data = [(i, buckets[i]) for i in range(0, max(buckets) + 1)]
    width, height = 960, 420
    left, top, right, bottom = 70, 70, 28, 58
    plot_w = width - left - right
    plot_h = height - top - bottom
    ymax = max(v for _, v in data)
    lines = svg_frame("售空前成功下单吞吐（按秒）", width, height)
    for i in range(5):
        y = top + plot_h * i / 4
        val = round(ymax * (4 - i) / 4)
        lines += [f'<line class="grid" x1="{left}" y1="{y:.1f}" x2="{width-right}" y2="{y:.1f}"/>',
                  f'<text class="muted" x="18" y="{y+5:.1f}" font-size="13">{val}</text>']
    bw = plot_w / len(data) * 0.72
    for i, val in data:
        x = left + plot_w * i / len(data) + (plot_w / len(data) - bw) / 2
        h = plot_h * val / ymax
        y = top + plot_h - h
        lines.append(f'<rect class="green" x="{x:.1f}" y="{y:.1f}" width="{bw:.1f}" height="{h:.1f}" rx="3"/>')
        lines.append(f'<text x="{x + bw/2:.1f}" y="{y-7:.1f}" font-size="12" text-anchor="middle">{val}</text>')
        lines.append(f'<text class="muted" x="{x + bw/2:.1f}" y="{height-28}" font-size="12" text-anchor="middle">{i}s</text>')
    lines += [f'<line class="axis" x1="{left}" y1="{top+plot_h}" x2="{width-right}" y2="{top+plot_h}"/>',
              f'<text class="muted" x="{width/2}" y="{height-10}" font-size="13" text-anchor="middle">距首个请求开始的时间（秒）</text>']
    write_svg(OUT / "pre_soldout_success_throughput.svg", lines)


def chart_latency_buckets(success_rows):
    start = min(r["timeStamp"] for r in success_rows)
    grouped = defaultdict(list)
    for row in success_rows:
        grouped[(row["timeStamp"] - start) // 1000].append(row["elapsed"])
    xs = sorted(grouped)
    series = {
        "P50": [percentile(grouped[x], 50) for x in xs],
        "P90": [percentile(grouped[x], 90) for x in xs],
        "P99": [percentile(grouped[x], 99) for x in xs],
    }
    width, height = 960, 420
    left, top, right, bottom = 72, 72, 145, 58
    plot_w = width - left - right
    plot_h = height - top - bottom
    ymax = math.ceil(max(max(v) for v in series.values()) / 500) * 500
    colors = {"P50": "#2d8a63", "P90": "#c47b20", "P99": "#be3a3a"}
    lines = svg_frame("售空前延迟趋势（1秒分桶）", width, height)
    for i in range(6):
        y = top + plot_h * i / 5
        val = round(ymax * (5 - i) / 5)
        lines += [f'<line class="grid" x1="{left}" y1="{y:.1f}" x2="{width-right}" y2="{y:.1f}"/>',
                  f'<text class="muted" x="18" y="{y+5:.1f}" font-size="13">{val}ms</text>']
    for name, values in series.items():
        points = []
        for x, val in zip(xs, values):
            px = left + plot_w * x / max(xs)
            py = top + plot_h - plot_h * val / ymax
            points.append(f"{px:.1f},{py:.1f}")
        lines.append(f'<polyline class="line" stroke="{colors[name]}" points="{" ".join(points)}"/>')
        for x, val in zip(xs, values):
            px = left + plot_w * x / max(xs)
            py = top + plot_h - plot_h * val / ymax
            lines.append(f'<circle cx="{px:.1f}" cy="{py:.1f}" r="3.5" fill="{colors[name]}"/>')
    for idx, name in enumerate(series):
        y = top + 24 + idx * 28
        lines += [f'<rect x="{width-right+25}" y="{y-12}" width="16" height="16" fill="{colors[name]}"/>',
                  f'<text x="{width-right+50}" y="{y+2}" font-size="14">{name}</text>']
    for x in xs:
        px = left + plot_w * x / max(xs)
        lines.append(f'<text class="muted" x="{px:.1f}" y="{height-28}" font-size="12" text-anchor="middle">{x}s</text>')
    write_svg(OUT / "pre_soldout_latency_trend.svg", lines)


def chart_histogram(success_rows):
    vals = [r["elapsed"] for r in success_rows]
    bin_size = 250
    bins = list(range(0, 3000 + bin_size, bin_size))
    counts = [0 for _ in bins[:-1]]
    for val in vals:
        idx = min(len(counts) - 1, val // bin_size)
        counts[idx] += 1
    width, height = 960, 420
    left, top, right, bottom = 72, 72, 28, 66
    plot_w = width - left - right
    plot_h = height - top - bottom
    ymax = max(counts)
    lines = svg_frame("售空前延迟分布", width, height)
    for i in range(5):
        y = top + plot_h * i / 4
        val = round(ymax * (4 - i) / 4)
        lines += [f'<line class="grid" x1="{left}" y1="{y:.1f}" x2="{width-right}" y2="{y:.1f}"/>',
                  f'<text class="muted" x="18" y="{y+5:.1f}" font-size="13">{val}</text>']
    bw = plot_w / len(counts) * 0.78
    for idx, count in enumerate(counts):
        x = left + plot_w * idx / len(counts) + (plot_w / len(counts) - bw) / 2
        h = plot_h * count / ymax
        y = top + plot_h - h
        lines.append(f'<rect class="blue" x="{x:.1f}" y="{y:.1f}" width="{bw:.1f}" height="{h:.1f}" rx="3"/>')
        if count:
            lines.append(f'<text x="{x+bw/2:.1f}" y="{y-6:.1f}" font-size="11" text-anchor="middle">{count}</text>')
        label = f"{bins[idx]}-{bins[idx+1]}"
        lines.append(f'<text class="muted" x="{x+bw/2:.1f}" y="{height-34}" font-size="10" text-anchor="middle" transform="rotate(-35 {x+bw/2:.1f},{height-34})">{label}</text>')
    lines.append(f'<text class="muted" x="{width/2}" y="{height-10}" font-size="13" text-anchor="middle">响应时间区间（毫秒）</text>')
    write_svg(OUT / "pre_soldout_latency_distribution.svg", lines)


def chart_response_mix(rows):
    first = min(r["timeStamp"] for r in rows)
    first_success = min(r["timeStamp"] for r in rows if r["bytes"] == 195)
    last_success = max(r["timeStamp"] for r in rows if r["bytes"] == 195)
    until = last_success + 1000
    filtered = [r for r in rows if first_success <= r["timeStamp"] < until]
    buckets = defaultdict(Counter)
    for row in filtered:
        buckets[(row["timeStamp"] - first_success) // 1000][row["bytes"]] += 1
    xs = list(range(0, max(buckets) + 1))
    width, height = 960, 420
    left, top, right, bottom = 70, 70, 170, 58
    plot_w = width - left - right
    plot_h = height - top - bottom
    ymax = max(sum(buckets[x].values()) for x in xs)
    colors = {195: "#2d8a63", 200: "#be3a3a", 203: "#c47b20"}
    labels = {195: "195B 成交成功", 200: "200B 售空拒绝", 203: "203B 其他拒绝"}
    lines = svg_frame("售空边界响应构成", width, height)
    for i in range(5):
        y = top + plot_h * i / 4
        val = round(ymax * (4 - i) / 4)
        lines += [f'<line class="grid" x1="{left}" y1="{y:.1f}" x2="{width-right}" y2="{y:.1f}"/>',
                  f'<text class="muted" x="18" y="{y+5:.1f}" font-size="13">{val}</text>']
    bw = plot_w / len(xs) * 0.72
    for xidx, sec in enumerate(xs):
        x = left + plot_w * xidx / len(xs) + (plot_w / len(xs) - bw) / 2
        ybase = top + plot_h
        for klass in (195, 203, 200):
            val = buckets[sec][klass]
            if not val:
                continue
            h = plot_h * val / ymax
            ybase -= h
            lines.append(f'<rect x="{x:.1f}" y="{ybase:.1f}" width="{bw:.1f}" height="{h:.1f}" fill="{colors[klass]}" rx="2"/>')
        lines.append(f'<text class="muted" x="{x + bw/2:.1f}" y="{height-28}" font-size="12" text-anchor="middle">{sec}s</text>')
    for idx, klass in enumerate((195, 203, 200)):
        y = top + 24 + idx * 28
        lines += [f'<rect x="{width-right+20}" y="{y-12}" width="16" height="16" fill="{colors[klass]}"/>',
                  f'<text x="{width-right+45}" y="{y+2}" font-size="14">{labels[klass]}</text>']
    write_svg(OUT / "sellout_boundary_response_mix.svg", lines)


def write_summary(rows, row_lengths):
    classes = Counter(r["bytes"] for r in rows)
    success = [r for r in rows if r["bytes"] == 195]
    soldout = [r for r in rows if r["bytes"] == 200]
    other = [r for r in rows if r["bytes"] == 203]
    summary = {
        "source": str(JTL.relative_to(ROOT)),
        "raw_row_lengths": dict(row_lengths),
        "main_samples": len(rows),
        "response_bytes": dict(classes),
        "successful_orders": summarize(success),
        "sold_out_rejections": summarize(soldout),
        "other_business_rejections": summarize(other),
        "success_start_window_s": round((max(r["timeStamp"] for r in success) - min(r["timeStamp"] for r in success)) / 1000, 3),
        "peak_success_starts_per_second": max(Counter((r["timeStamp"] - min(x["timeStamp"] for x in success)) // 1000 for r in success).values()),
    }
    (OUT / "summary.json").write_text(json.dumps(summary, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")


def main():
    OUT.mkdir(parents=True, exist_ok=True)
    rows, row_lengths = load_rows()
    success = [r for r in rows if r["bytes"] == 195]
    chart_throughput(success)
    chart_latency_buckets(success)
    chart_histogram(success)
    chart_response_mix(rows)
    write_summary(rows, row_lengths)


if __name__ == "__main__":
    main()
