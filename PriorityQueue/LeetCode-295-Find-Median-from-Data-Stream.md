# 295. Find Median from Data Stream

**Hard** · `Heap` · `Design` · `Two Heaps` · [LeetCode ↗](https://leetcode.com/problems/find-median-from-data-stream/)

Design a class over a number stream:

| API | Behavior |
|---|---|
| `addNum(num)` | A new number arrives |
| `findMedian()` | Return the median of everything so far — average of the two middle values when the count is even |

```text
addNum(1) → addNum(2) → findMedian() = 1.5 → addNum(3) → findMedian() = 2.0
```

The finale of the heap line: [LC 703](https://leetcode.com/problems/kth-largest-element-in-a-stream/) watches one *fixed* position (k-th largest) with one heap; the median is a position that **moves with the stream** — one heap can't hold it, two can.

---

## Why two heaps — the median needs a boundary, not a total order

The median is the middle of the sorted stream, but nobody needs to know the internal order of either half — **only the two elements straddling the boundary**. Full order is waste; the boundary is the requirement. The architecture grows out of that observation:

| Heap | Holds | Type | Its top is… |
|---|---|---|---|
| `maxHeap` | the **smaller** half | max-heap | the largest of the smalls — the boundary's **left** neighbor |
| `minHeap` | the **larger** half | min-heap | the smallest of the larges — the boundary's **right** neighbor |

**The median permanently lives at the two tops.** Same philosophy as LC 347/703 — *put the element you'll access at the top* — except here **both doors face the middle**, clamping the boundary from either side.

Two invariants carry all correctness:

1. **Order:** every element in `maxHeap` ≤ every element in `minHeap`.
2. **Size:** the heaps differ by at most 1, and — *this version's convention* — **`minHeap` is the majority**: with an odd count, the extra element sits in `minHeap`, so the median is `minHeap`'s top.

---

## ⚠️ A landmine in this file — the big comment is a MIRROR of the code

The block comment above `addNum` describes: *route through `maxHeap` → skim its largest into `minHeap` → if `minHeap` oversized, move one back* — with `maxHeap` as the majority. The **code does the exact opposite on every axis**:

| | The comment says | The code does |
|---|---|---|
| Newcomer enters | `maxHeap` | **`minHeap`** |
| Skim across | maxHeap's **largest** → minHeap | **minHeap's smallest → maxHeap** |
| Rebalance when | `minHeap` oversized | **`maxHeap` oversized** |
| Majority heap | `maxHeap` | **`minHeap`** |

Both conventions are valid — the comment is simply the *other* variant's manual pasted onto this one. Reading them together is guaranteed confusion (and likely why this solution felt forgotten). The field comment `// A min-heap for the right side (larger half)` *is* correct. **Fix the block comment to match the code** — a live specimen of "a stale comment is worse than no comment."

---

## Solution

```java
class MedianFinder {
    private PriorityQueue<Integer> maxHeap;   // smaller half; top = boundary's left neighbor

    // A min-heap for the right side (larger half) of the numbers
    private PriorityQueue<Integer> minHeap;   // larger half + majority; top = boundary's right neighbor

    public MedianFinder() {
        maxHeap = new PriorityQueue<>((Integer a, Integer b) -> {
            return Integer.compare(b, a);     // reversed order = max-heap
        });
        minHeap = new PriorityQueue<>();
    }

    public void addNum(int num) {
        minHeap.add(num);                     // 1) EVERYONE enters the larger half first
        maxHeap.add(minHeap.poll());          // 2) skim minHeap's minimum into the smaller half
        if (maxHeap.size() > minHeap.size()) {
            minHeap.add(maxHeap.poll());      // 3) smaller half oversized -> its top flows back
        }
    }

    public double findMedian() {
        if (maxHeap.size() == minHeap.size()) {
            if (maxHeap.size() == 0) {
                return 0.0;                   // defensive; LC guarantees non-empty calls
            }
            return (maxHeap.peek() + minHeap.peek()) / 2.0;   // even: average the two tops
        }
        return minHeap.peek() * 1.0;          // odd: the majority heap's top
    }
}
```

---

## `addNum` — the three-step wash, the heart of the problem

The rejected instinct: *"compare `num` with the tops, insert into the correct side, then rebalance."* Workable, but it needs empty-heap checks and two-direction rebalancing — branches everywhere. The wash-through does it **with zero comparisons**:

**Why the order invariant holds automatically:**

- Step 2 skims `x` = the **minimum** of `minHeap` → `x` ≤ everything left in `minHeap` ✓; the old `maxHeap` elements were already ≤ old min(`minHeap`) ≤ everything remaining ✓ — so `x` landing in `maxHeap` keeps *left ≤ right* intact.
- Step 3 flows back `y` = the **maximum** of `maxHeap` → everything remaining in `maxHeap` ≤ `y`, and `y` ≤ all of `minHeap` (invariant just re-established) — intact again.

Invariant by construction, not by branching.

**The wrong-door self-correction — the trick's best moment:** a tiny number (which belongs in the smaller half) enters `minHeap` first — but it is instantly `minHeap`'s minimum, and step 2 skims it straight into `maxHeap`. **The wrong door exists for one instant; the wash re-files it, no comparison ever written.**

**The size ledger** (let `maxHeap` = S, both possible pre-states):

```text
pre (S, S):    steps 1-2 → (S+1, S) → step 3 fires → (S, S+1)   minHeap +1  ✓
pre (S, S+1):  steps 1-2 → (S+1, S+1) → step 3 idle → equal      ✓
```

Sizes oscillate between *equal* and *minHeap-plus-one*, never diverging — which is exactly what lets `findMedian` read the answer off `size()` alone.

---

## `findMedian` — read the doors by the ledger

- **Equal sizes** → average the two tops. `/ 2.0` forces real division — `/ 2` would silently truncate.
- **Unequal** → the majority heap `minHeap`'s top.
- `size() == 0 → 0.0` is defensive (LC guarantees non-empty); `* 1.0` is optional (int auto-widens to double on return) — kept for explicitness.
- Footnote: `peek() + peek()` adds two ints — safe under this problem's ±10⁵ bounds; for general use, `a/2.0 + b/2.0` dodges overflow.

---

## Walkthrough — `[5, 15, 1]`

| add | ① enters minHeap | ② skim min → maxHeap | ③ flow back? | Final max / min | Median |
|---|---|---|---|---|---|
| 5 | {5} | 5 → max{5}, min{} | (1>0) 5 flows back | {} / {5} | **5** ✓ |
| 15 | {5,15} | 5 → max{5}, min{15} | equal, idle | {5} / {15} | (5+15)/2 = **10.0** ✓ |
| 1 | {1,15} | **1 → max{5,1}**, min{15} | (2>1) **5 flows back** | {1} / {5,15} | min top = **5** ✓ |

Row 3 is the whole show in one frame: **1 enters the wrong door and is skimmed out instantly** (it belongs in the smaller half), while step 3 carries **5 across the boundary** — the old boundary element switches sides. Sorted check `1, 5, 15` → median 5 ✓.

---

## Complexity — and the key contrast with LC 703

| Metric | Cost |
|---|---|
| `addNum` | **O(log n)** — two to three heap ops on heaps of ~n/2 |
| `findMedian` | **O(1)** — peek the doors |
| Space | **O(n)** |

The contrast worth saying out loud: 703 pins memory at O(k) because *evicted = gone forever* (the bar only rises). **Here nothing can ever be discarded** — a future flood of small numbers drags the median into territory you thought was dead. **The median is a moving position; every historical element may return to duty** → O(n) is forced, not lazy. One line: *whether you may discard data depends on whether the position you watch can move.*

---

## Famous follow-ups (one sentence each)

1. **All numbers in [0, 100]** → a counting array of 101 buckets; find the median by a prefix scan — no heaps at all.
2. **99% of numbers in [0, 100]** → buckets for the bulk + **two overflow heaps** for the outliers.

---

## Self-test — why `>` must be strict

Change step 3's condition to `>=` and walk `[1, 2]`: `add(1)` ends (0, 1), fine; `add(2)` reaches (1, 1) after steps 1–2, `>=` fires anyway → (0, 2); `findMedian` sees unequal sizes → returns `minHeap` top = 1. **Correct answer: 1.5 — wrong on the second number.** With `>=`, the equal state also flows back, `minHeap` inflates forever and `maxHeap` starves. **The strict `>` is the gatekeeper of the size oscillation; one equals sign collapses the whole design.**
