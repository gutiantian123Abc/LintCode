# 703. Kth Largest Element in a Stream

**Easy** · `Heap` · `Design` · [LeetCode ↗](https://leetcode.com/problems/kth-largest-element-in-a-stream/)

Design a class that keeps receiving numbers and can always report the **k-th largest** element — by sorted position, **duplicates count** (not the k-th distinct value).

| API | Behavior |
|---|---|
| `KthLargest(k, nums)` | Initialize with the numbers seen so far |
| `add(val)` | A new number arrives; return the **current** k-th largest |

It is guaranteed the stream holds at least k elements whenever `add` returns.

```text
KthLargest(3, [4,5,8,2])
add(3)→4   add(5)→5   add(10)→5   add(9)→8   add(4)→8
```

---

## The idea — a k-seat VIP room (size-k min-heap)

Keep a **min-heap that only ever holds the k largest numbers seen so far** — a VIP room with exactly k seats:

- **Heap top = the weakest person inside = the k-th largest overall.** The answer permanently stands at the door; `peek()` grabs it in O(1).
- Admission rule: **walk in first, then if the room is over capacity, evict the weakest** (`add`, then `poll`). Eviction always targets the *smallest* — which is exactly why finding the **largest** k uses a **min**-heap: put the one you'll evict where it's cheapest to remove. (Same trick as [LC 347](https://leetcode.com/problems/top-k-frequent-elements/), where the heap held numbers compared by frequency.)

**Why streaming makes this *the* answer:** the stream is append-only — once a number is evicted, k numbers already beat it, and the door's bar only rises from there. **Evicted means gone forever**, so steady-state memory is O(k) regardless of how long the stream runs. Contrast with the Leaderboard ([LC 1244](https://leetcode.com/problems/design-a-leaderboard/)): `reset` there means old data can *come back*, so a resident top-k heap is impossible and `top` must rebuild. **Whether a resident heap is viable depends on whether data can return.**

---

## Solution

```java
class KthLargest {
    int k;
    PriorityQueue<Integer> queue;

    public KthLargest(int k, int[] nums) {
        this.k = k;
        this.queue = new PriorityQueue<>();
        for (int num : nums) {
            queue.add(num);
        }
        
    }
    
    public int add(int val) {
        queue.add(val);
        while (queue.size() > k) {
            queue.poll();
        }

        return queue.peek();
    }
}
```

### Design note — in this version, the `while` is load-bearing

This constructor **defers trimming**: it loads *all* of `nums` into the heap and never evicts. So right after construction the heap may hold n > k elements — and the **first** `add` call drains all the extras in one go. That is why the trim is a `while`, not an `if`: change it to `if` and this version breaks on its very first call. (The untrimmed state is never observed from outside — only `add` returns values — so correctness holds.)

The common alternative trims inside the constructor (e.g. by reusing `add` per element). Honest trade-off:

| | This version (defer to first `add`) | Trim in constructor |
|---|---|---|
| Trim in `add` | **`while` required** | `if` suffices (size never exceeds k+1) |
| Constructor cost | O(n log n) — heap grows to n | O(n log k) |
| Peak memory | O(n) until the first `add` | **O(k)** always |
| Correctness | ✓ | ✓ |

Both pass everything here (n ≤ 10⁴). For an interview, mention the trade-off — that one sentence is worth more than either choice.

---

## Walkthrough — `KthLargest(3, [4,5,8,2])`

Constructor loads everything: heap = `{2, 4, 5, 8}` (min-heap, top = 2 — *four* elements, untrimmed).

| `add` | After entry | Evictions (`while`) | Heap (k = 3) | Return (top) |
|---|---|---|---|---|
| 3 | {2,3,4,5,8} | **poll 2, poll 3** — the deferred trim drains twice | {4,5,8} | **4** |
| 5 | {4,5,5,8} | poll 4 | {5,5,8} | **5** |
| 10 | {5,5,8,10} | poll 5 | {5,8,10} | **5** |
| 9 | {5,8,9,10} | poll 5 | {8,9,10} | **8** |
| 4 | {4,8,9,10} | poll 4 | {8,9,10} | **8** |

Output `[4,5,5,8,8]` ✓. Row 1 shows the `while` earning its keep — two evictions in one call. Row 2 shows **duplicates each taking a seat**: a second 5 enters, the 4 leaves, and the 3rd largest becomes 5 — exactly what "by sorted position, not distinct" means.

---

## Complexity

| Metric | Cost |
|---|---|
| Constructor | O(n log n) — heap grows to n (alternative: O(n log k)) |
| First `add` | drains up to n − k extras, amortized |
| Steady-state `add` | **O(log k)** — one offer + one poll |
| Space | O(n) peak before the first trim; **O(k)** steady state |

Versus naive stream handling — re-sorting O(n log n) per query or sorted-list insertion O(n) — both degrade as the stream grows; the heap stays at log k forever.

---

## Two spoken bonus points

**1 · Doorman optimization.** Once full, a newcomer who can't beat the heap top shouldn't even enter:

```java
public int add(int val) {
    if (queue.size() < k) {
        queue.add(val);              // warm-up: seats still open
    } else if (val > queue.peek()) {
        queue.poll();                // beat the doorman -> swap in
        queue.add(val);
    }
    return queue.peek();
}
```

Same O(log k), visibly better constants when the stream is full of small numbers. (The `size < k` branch must come first — during warm-up, everyone gets in unconditionally. This variant assumes a trimmed constructor.)

**2 · The family map.** [LC 215](https://leetcode.com/problems/kth-largest-element-in-an-array/) is the *one-shot array* version — quickselect, average O(n), is its signature move. 703 is the *stream* version — data keeps arriving, queries keep coming, so a resident size-k heap is the right shape. One sentence draws the whole map: **"static → quickselect; streaming → size-k min-heap."**
