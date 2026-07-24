# 717. 1-bit and 2-bit Characters

**Easy** · `Array` · `Simulation` · [LeetCode ↗](https://leetcode.com/problems/1-bit-and-2-bit-characters/)

An encoding has exactly two kinds of characters:

| Character | Bits |
|---|---|
| one-bit | `0` |
| two-bit | `10` or `11` |

Given an array `bits` that **ends with `0`**, return whether the **last character must be the standalone one-bit `0`**.

```text
[1, 0, 0]     →  decodes as  10 | 0        →  true   (final 0 stands alone)
[1, 1, 1, 0]  →  decodes as  11 | 10       →  false  (final 0 got captured)
```

---

## The key insight — this encoding has zero ambiguity

At any decoding position, look at the current bit:

```text
bit == 0  →  the only codeword starting with 0 is "0" itself   →  ALWAYS one-bit,  jump 1
bit == 1  →  "10" and "11" are both two bits                    →  ALWAYS two-bit,  jump 2
```

**Every step is forced.** Compare with Word Break (LC 139): a dictionary like `{"a", "aa"}` creates real branching, so DP must track every possibility. Here the "dictionary" `{0, 10, 11}` has a special property — **the first bit alone tells you the character's length** (this is exactly why prefix codes / Huffman codes decode instantly). No branching → the decoding path is unique → a single simulation pass is the whole answer.

> **Lesson for the toolbox:** before reaching for DP on a segmentation problem, ask —
> *"is the split actually ambiguous?"* No ambiguity → it collapses into one linear scan.

---

## Approach — jump the pointer, stop before the last bit

Walk from the left: `1` jumps 2, `0` jumps 1. **Deliberately stop before the last position**, then read the landing spot:

```text
landed ON n-1   →  the final 0 forms its own character  →  true
jumped TO n     →  the final 0 was captured as the 2nd bit of a "10"  →  false
```

---

## Solution

```java
class Solution {
    public boolean isOneBitCharacter(int[] bits) {
        int n = bits.length;
        if (n == 1) {                    // defensive: single element
            return bits[0] == 0;
        }

        int i = 0;

        while (i < n - 1) {              // deliberately stop before the last bit
            if (bits[i] == 1) {
                i += 2;                  // '1' always leads a two-bit char (10 / 11)
            } else { // bits[i] == 0
                i++;                     // '0' is always a standalone one-bit char
            }
        }

        if (i > n - 1) {                 // overshot: the final 0 was captured
            return false;
        }

        return bits[i] == 0;             // landed on the last bit: check it
    }
}
```

---

## Walkthroughs

**`[1, 0, 0]`** — n = 3, loop runs while `i < 2`:

| `i` | `bits[i]` | action | new `i` |
|---|---|---|---|
| 0 | 1 | jump 2 | 2 → loop ends |

Landing spot `i = 2 = n - 1` → **true** ✓

**`[1, 1, 1, 0]`** — n = 4, loop runs while `i < 3`:

| `i` | `bits[i]` | action | new `i` |
|---|---|---|---|
| 0 | 1 | jump 2 | 2 |
| 2 | 1 | jump 2 | 4 → loop ends |

Landing spot `i = 4 ≠ 3` — the final 0 was dragged into a `10` by `bits[2]` → **false** ✓

---

## Defensive vs. lean — what the extra checks are guarding

Three spots in the solution are logically redundant **given the problem's guarantees**, and all three guard the same assumption — *"the array ends with 0"*:

| Check | Why it can be dropped |
|---|---|
| `n == 1` special case | The general flow covers it: loop `i < 0` never runs, falls through to the same `bits[0] == 0` |
| final `bits[i] == 0` | Landing on `n-1` means reading the last element — **guaranteed to be 0** by the problem |
| `i > n - 1` as a separate branch | The landing spot is only ever `n-1` or `n`, so `return i == n - 1` covers both |

The lean version, trusting the constraint fully:

```java
class Solution {
    public boolean isOneBitCharacter(int[] bits) {
        int i = 0;
        int n = bits.length;
        while (i < n - 1) {
            i += (bits[i] == 1) ? 2 : 1;
        }
        return i == n - 1;               // relies on: bits always ends with 0
    }
}
```

| | Defensive (above) | Lean |
|---|---|---|
| Trusts "ends with 0" | No — validates it | Fully |
| Handles malformed input like `[1,1,1]` | Yes | Would mis-answer |
| Interview move | Safe, but reads as not having digested the constraint | Write this, **and say out loud**: *"`return i == n-1` relies on the guaranteed trailing 0; for untrusted input I'd add a `bits[n-1] == 0` check"* |

Saying the reliance out loud gets both the simplicity points **and** the rigor points.

---

## Bonus — the parity trick

The answer depends only on the **parity of the run of 1s hugging the final 0**. That run is preceded by a `0` (or the array start), and a `0` can never be the first bit of a two-bit char — so the decoder **must** enter the run exactly at its start, eating 1s two at a time:

```text
even count  →  pairs off as 11 11 …      →  lands on the final 0  →  true
odd  count  →  one 1 left over, grabs the final 0 as "10"          →  false
```

```java
class Solution {
    public boolean isOneBitCharacter(int[] bits) {
        int ones = 0;
        for (int j = bits.length - 2; j >= 0 && bits[j] == 1; j--) {
            ones++;                      // count the 1s hugging the final 0
        }
        return ones % 2 == 0;
    }
}
```

Verify: `[1,1,1,0]` → three 1s, odd → false ✓ · `[1,0,0]` → zero 1s, even → true ✓

Still O(n) worst case, but usually touches only the tail. **Write the simulation, mention the parity trick** — direct + observant, best of both.

---

## Complexity

| Metric | Cost |
|---|---|
| Time | **O(n)** — each position visited at most once |
| Space | **O(1)** |
