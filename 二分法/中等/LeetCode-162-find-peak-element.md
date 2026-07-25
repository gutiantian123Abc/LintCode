# 162. Find Peak Element

**Medium** · `Binary Search` · `Array` · [LeetCode ↗](https://leetcode.com/problems/find-peak-element/)

Find the index of **any** peak — an element strictly greater than both neighbors — in **O(log n)**.

Two constraints hold up the entire solution:

| Constraint | What it buys |
|---|---|
| `nums[-1] = nums[n] = -∞` | Virtual cliffs at both ends → **a peak must exist** (the max is one), and any climb must eventually turn down |
| No two adjacent elements are equal | Every position has exactly one of **four local shapes** — uphill, downhill, peak, valley. No plateaus — without this, binary search has nothing to decide on |

```text
[1, 2, 1, 3, 5, 6, 4]  →  1 or 5   (nums[1]=2 and nums[5]=6 are both peaks)
[1, 2, 3, 1]           →  2
```

---

## The core puzzle — the array isn't sorted, so why does binary search work?

> **Binary search never needed "sorted." It needs one thing: a rule that reliably discards half.**

Sorted arrays use "compare with target." This problem uses **the slope under `mid`**:

```text
nums[mid] < nums[mid+1]   (mid is on an UPHILL)
        →  a peak MUST exist on the right.

Why: keep walking right — the values rise, but the array ends at a -∞ cliff.
A rising walk must turn down before the cliff; the first turning point IS a peak.
```

The mirror holds for downhill → peak on the left. This **"every climb must summit"** guarantee — provided jointly by the −∞ borders and the no-equal-neighbors rule — is this problem's substitute for sortedness. (The left half may also contain peaks; the problem wants *any one*, so discarding it loses nothing.)

---

## The template — `left + 1 < right`, two survivors

This solution uses the **two-candidate template**: shrink until exactly **two adjacent candidates** remain, then settle it outside the loop. Its three properties each hit a pain point of this exact problem:

1. **`mid`'s neighbors can never go out of bounds.** The loop guard keeps the window ≥ 3 wide, so `left < mid < right` always — reading `nums[mid-1]` and `nums[mid+1]` needs **zero boundary checks**. (The standard template must separately argue `mid+1 ≤ right`.)
2. **`left = mid` / `right = mid` can never infinite-loop.** `mid` is strictly interior, so every assignment shrinks the window. (In a `while (left < right)` template, `left = mid` is a classic infinite loop.)
3. **Small arrays are free.** For n = 1 or 2 the loop never runs — the final two-way comparison handles them.

---

## Solution

```java
class Solution {
    public int findPeakElement(int[] nums) {
        int left = 0, right = nums.length - 1;

        while (left + 1 < right) {                    // window stays >= 3 wide
            int midIndex = left + (right - left)/2;   // overflow-safe midpoint
            int midVal = nums[midIndex];
            int leftVal = nums[midIndex - 1];         // safe: left < mid
            int rightVal = nums[midIndex + 1];        // safe: mid < right
            if (leftVal < midVal && rightVal < midVal) {
                return midIndex;                      // PEAK — early exit
            } else if (leftVal < midVal) {
                left = midIndex;                      // UPHILL — peak is right
            } else if (rightVal < midVal) {
                right = midIndex;                     // DOWNHILL — peak is left
            } else {
                right = midIndex;                     // VALLEY — peaks on BOTH sides; go left
            }
        }

        if (nums[left] > nums[right]) {               // endgame: two candidates,
            return left;                              // the bigger one is the peak
        }

        return right;
    }
}
```

### The four branches

With no equal neighbors, `mid`'s local shape is exactly one of four — the branches are exhaustive:

| Shape | Test | Action | Why |
|---|---|---|---|
| **Peak** | both neighbors `< mid` | `return mid` | Hit the answer — early exit is this template's bonus |
| **Uphill** | left `< mid` (right is then forced `> mid`) | `left = mid` | Climb guarantee: peak on the right; keep `mid` as the new floor |
| **Downhill** | right `< mid` (left forced `> mid`) | `right = mid` | Mirror: peak on the left |
| **Valley** | both neighbors `> mid` | `right = mid` | Peaks exist on **both** sides (came down into it → left has one; rises out of it → right has one). Either direction is correct; this code goes left |

*"Forced" note:* reaching the uphill branch means the peak test failed, so the right neighbor is **not** smaller — with no-equals, it is strictly greater. The last two branches share one action and could merge into a single `else`; keeping them separate documents the two distinct situations. Style call, both correct.

---

## Walkthroughs

**`[1, 2, 1, 3, 5, 6, 4]`**

| Round | Window | mid | left / mid / right vals | Shape | Action |
|---|---|---|---|---|---|
| 1 | [0, 6] | 3 | 1 / 3 / 5 | uphill | `left = 3` |
| 2 | [3, 6] | 4 | 3 / 5 / 6 | uphill | `left = 4` |
| 3 | [4, 6] | 5 | 5 / 6 / 4 | **peak** | `return 5` ✓ |

**`[3, 2, 1]`** — reaching the endgame

Round 1: mid = 1, vals 3/2/1 → downhill → `right = 1`. Window [0, 1], loop exits.
Endgame: `nums[0]=3 > nums[1]=2` → `return 0` ✓ (3 has −∞ on its left, 2 on its right — a peak).

---

## Why "the bigger of the two" is always a peak — the two promises

The window shrinks while carrying two invariants:

> **Left-edge promise:** `nums[left-1] < nums[left]` — *"the walk climbs INTO the window from the left."*
> Holds initially (left = 0, −∞ outside). Only the uphill branch moves `left`, and it moves it to a `mid` with exactly `nums[mid-1] < nums[mid]` — promise renewed. ✓

> **Right-edge promise:** `nums[right] > nums[right+1]` — *"the walk descends OUT of the window on the right."*
> Holds initially (right = n−1, −∞ outside). The downhill branch renews it identically. ✓

At the endgame the two candidates are adjacent. Whichever is **bigger**: its inward neighbor is the loser (already smaller), and its outward neighbor is pinned smaller by the matching promise — **smaller on both sides → peak.**

**The one bend — and how it catches itself.** The valley branch moves `right` onto a valley floor, which *breaks* the right-edge promise (its right neighbor is bigger). But a valley floor **can never win the endgame**: by definition it is smaller than its left neighbor — and the endgame is precisely a comparison against that left neighbor → the left candidate must win → and the left candidate is a peak by the left-edge promise plus its beaten inward neighbor. The exception is absorbed by the very comparison that follows it. Watertight.

---

## Template comparison

The standard one-liner version:

```java
while (left < right) {
    int mid = left + (right - left) / 2;
    if (nums[mid] < nums[mid + 1]) left = mid + 1;  // uphill -> peak is right
    else right = mid;                                // downhill/peak -> keep mid
}
return left;
```

| | Two-candidate (`left+1 < right`) | Standard (`left < right`) |
|---|---|---|
| Per round | Reads both neighbors, 4 branches | One compare (`mid` vs `mid+1`), 2 branches |
| Bounds | Neighbors safe for free | Must argue `mid+1 ≤ right` |
| Infinite-loop risk | Immune | `left = mid` loops forever — must write `mid + 1` |
| Early exit | Returns on hitting a peak | None — converges fully |
| Endgame | Explicit two-way pick | Implicit inside the loop |

**The standard version is shorter; the two-candidate version is foolproof** — no ±1 subtleties to memorize. Under interview pressure, the foolproof one is the steadier gun; know both, say the trade-off.

---

## Complexity

| Metric | Cost |
|---|---|
| Time | **O(log n)** — the window roughly halves each round |
| Space | **O(1)** |

---

## Self-test

Walk `[2, 1, 3, 1]` by hand: round 1 hits mid = 1 with neighbors 2 and 3 — a **valley**. Which side does the code choose? What does the endgame return? And if the valley branch were changed to `left = mid` (go right instead), what would be returned then? *(Both final answers must be legal peaks — the valley branch's "either side works" claim, verified with your own hands.)*