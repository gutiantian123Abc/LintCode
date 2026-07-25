# IXL Interview — Text Processor (Size of the Last Character)

**Phone-screen frequent** · `Array Scan` · `Parity` · `Java byte` · reskin of [LC 717 ↗](https://leetcode.com/problems/1-bit-and-2-bit-characters/)

> **One-liner:** LC 717 with bits→bytes and the flag→127. Same skeleton, three extra layers: the **big-file follow-up**, the **signed-byte trap**, and **hex literacy**.

A text file uses a special encoding — every character is 1 or 2 bytes:

| First byte | Character size |
|---|---|
| **≤ 127** | one byte, stands alone |
| **> 127** | leads a **two-byte** character; the 2nd byte can be **any** value (0–255) |

Return the size (1 or 2) of the **last** character. The file is well-formed.

```text
int getSizeOfLastChar(int[] data)

[127, 128, 126]  →  2     parsed as  127 | (128, 126)
[122, 123, 124]  →  1     parsed as  122 | 123 | 124
```

**Mapping to LC 717:** bit → byte · `0` → `≤127` · `1` → `>127` · boolean → int(1/2). The core insight carries over unchanged: **the first byte alone tells you the character's length → every parsing step is forced → zero ambiguity → one linear scan, no DP.** (This is exactly how prefix codes / UTF-8 achieve instant decoding.)

---

## Solution 1 — forward scan (write this first)

Simulate parsing: `≤127` jumps 1, `>127` jumps 2, but **never consume the last byte** (loop while `i < n-1`). Then read the landing spot:

```text
landed ON n-1   →  the final byte forms its own character   →  1
jumped TO n     →  it was captured as someone's 2nd byte    →  2
```

```java
int i = 0;
while (i < data.length - 1) {
    i += (data[i] > 127) ? 2 : 1;
}
return (i == data.length - 1) ? 1 : 2;
```

---

## Solution 2 — backward scan (the follow-up answer)

> **Count the run of `>127` bytes immediately before the last byte.
> Even count → 1. Odd count → 2.**

### Worked examples

Notation: `B` = byte `>127` (can lead a pair) · `s` = byte `≤127` (always standalone).

| Array | Shape | Bs before last | Parity | Answer |
|---|---|---|---|---|
| `[100, 200, 201, 202, 203, 50]` | `s B B B B s` | 4 | even | **1** — `100 \| (200,201) \| (202,203) \| 50` |
| `[100, 200, 201, 202, 50]` | `s B B B s` | 3 | odd | **2** — `100 \| (200,201) \| (202,50)` |
| `[100, 200, 150]` | `s B B` | 1 | odd | **2** — `100 \| (200,150)` |
| `[130, 131, 100, 50]` | `B B s s` | 0 | even | **1** — `(130,131) \| 100 \| 50` |

Row 3 kills the classic wrong instinct: **150 > 127, but it is NOT a new character's first byte** — a byte's role is decided by *where the parser stands when it reaches it*, not by its own value ("the 2nd byte can be any value" includes >127).

Row 4 shows why `k = 0` belongs with "even": the byte before the last is an `s`, which finishes as its own character — the parser lands cleanly on the last byte, same ending as any even count.

### Why counting alone is enough — the entry point is locked

```text
index:   ...   s    B  B  B  B   [last]
               ↑    ↑
               │    └── the parser MUST enter the run exactly here
               └── ≤127: always its own character,
                   and can never be anyone's FIRST byte
```

1. Look at the byte **before** the run (an `s`, or the array start). An `s` always forms its own character — so after consuming it, the parser stands **exactly on the run's first `B`**. Could that first `B` instead be some pair's *second* byte? No — its leader would have to be the `s` before it, but leaders must be `>127`. **Contradiction → entry point locked.**
2. From there every step is mechanical: standing on a `B` → lead a pair → eat 2. The pairing has **no second possibility**, so only the run's length parity matters:

```text
even:   [B B] [B B] ... [B B] │ last     → lands ON last → stands alone → 1
odd:    [B B] [B B] ... [B ??]           → lone leader grabs last as 2nd byte → 2
```

### Why the count starts at `length - 2`

```java
int i = data.length - 2;                      // first WITNESS, just before the last byte
while (i >= 0 && (data[i] & 0xFF) > 127) {    // walk left through the run
    count++;
    i--;
}
```

The last byte (`length - 1`) is the **defendant, not a witness** — its fate is decided by the bytes before it, so it never joins the count. Counter-example: `[100, 200, 150]` — include the last byte and count = 2 → even → wrong "1"; start at `n-2` and count = 1 → odd → correct "2". The `i >= 0` guard is the `-2`'s paired seatbelt: for a single-element array, `i = -1`, the loop never runs, count = 0 → 1 ✓.

### Why this is the follow-up answer

The problem says *file*. For a multi-GB file the forward scan must read from byte 0; the backward scan just **seeks to the end and reads back until the first `≤127`** — usually a handful of bytes. That is the answer to *"what if the file is huge?"*, and it's the real-world motivation behind self-synchronizing encodings like UTF-8 (worth saying out loud).

---

## The Java `byte` trap

### One byte = one number (cells, not bits)

The file is just **a list of numbers, each 0..255 — one number per cell**:

```text
[ 100 ] [ 200 ] [ 150 ]     ← 3 cells, 3 numbers, length == 3
```

`length` counts **cells**, not bits. The same 100-byte file stored as `byte[]` or `int[]` gives `length == 100` either way — `byte[]` just spends 1 byte per cell instead of 4 (memory, not count). So all index logic (`- 2`, `i--`, `i >= 0`) is **cell-level and identical** in both versions. The type matters at exactly one moment: **reading the number out of a cell.**

### Two ways to read the same 8 bits

Bits carry no inherent meaning — the reading convention does:

| Bits | Unsigned (the problem's meaning) | Java `byte` (signed two's complement) |
|---|---|---|
| `00000000`..`01111111` | 0..127 | 0..127 — **identical** |
| `10000000`..`11111111` | 128..255 | **−128..−1 — all negative** (value − 256) |

The top bit carries weight **−128** in Java's reading. **Java has no unsigned byte** — `byte` is always the signed convention. So file byte 200 (`11001000`) is stored fine, but *read out* as **−56**.

### The bug — `data[i] > 127` is vacuously false

A Java `byte`'s maximum value **is 127**. On `byte[]`, the condition can never be true — every byte gets treated as one-byte, the function **returns 1 for every input**, with **no compile error, no exception, no warning**. Tests that only use values ≤127 stay green. Correct algorithm, broken comparison, total silence — the worst class of bug.

```text
[200, 201]  →  stored as −56, −55  →  −56 > 127? false  →  jump 1  →  returns 1  ✗ (true answer: 2)
```

### Fix 1 — `(data[i] & 0xFF) > 127`: switch back to the unsigned reading

Comparison first **promotes** the byte to `int`, and promotion is **sign-extending** (upper 24 bits copy the sign bit). `& 0xFF` zeroes those 24 bits and keeps the low 8 — the original unsigned value returns, inside an `int` big enough to hold it:

```text
byte −56:                              11001000
int  −56:   11111111 11111111 11111111 11001000    ← sign-extended
        AND 00000000 00000000 00000000 11111111    ← 0xFF
        =   00000000 00000000 00000000 11001000    =  200  ✓
```

**`& 0xFF` = "give me these 8 bits read as unsigned."** The standard incantation wherever raw bytes enter Java (file I/O, sockets, pixels). Note it converts the **value being read**, never the array — each loop iteration translates its own `data[i]` on the spot, so forward and backward scans use it identically.

### Fix 2 — `data[i] < 0`: read the sign bit directly

```text
unsigned ≥ 128   ⟺   top bit is 1   ⟺   Java reads it as negative
```

Three descriptions of the same bit patterns, so `data[i] < 0` selects exactly the bytes the problem calls `>127`. Shorter, zero bit-twiddling — **but it only works because the threshold (127/128) sits precisely on the sign boundary**. Change the threshold to 100 and `< 0` is useless while `(b & 0xFF) > 100` still works. Say that trade-off out loud for full credit.

**Never mix the two fixes:** `(data[i] & 0xFF) < 0` is always false (after masking, values are 0..255 — never negative). Translate-then-compare-`>127`, or don't-translate-and-compare-`<0`. Pick one lane.

### The layered picture

```text
storage:   [11001000]           8 bits in a cell            ┐  type-independent —
cells:     cell #i; length counts cells                     ┘  identical for byte[]/int[]

reading:   signed glasses   →  −56    (Java's default)      ┐  the ONLY layer where
           unsigned glasses →  200    (& 0xFF switches)     ┘  byte[] differs
```

### The real interview move

Ask **before writing line one**: *"Is the input `int[]` or `byte[]`?"* — the answer decides how the core comparison is written. Ask → hear `byte[]` → narrate the trap and the fix. The trap becomes your stage.

---

## Hex sidebar — what `0xFF` actually is

`0x` prefix = **hexadecimal** (base 16, digits `0-9` then `A-F` = 10..15). `0xFF = 15×16 + 15 = 255` — same number, different notation.

Why programmers love hex: **one hex digit = exactly 4 bits** (16 = 2⁴), so hex is binary shorthand — a byte is exactly **two hex digits**, and every bit stays visible:

| Hex | Binary | Decimal | Meaning here |
|---|---|---|---|
| `0x00` | `00000000` | 0 | minimum |
| `0x7F` | `01111111` | 127 | **the threshold** — sign bit still 0 |
| `0x80` | `10000000` | 128 | one past it — **sign bit flips** |
| `0xC8` | `11001000` | 200 | the example byte |
| `0xFF` | `11111111` | 255 | maximum; **the 8-bit mask** |

Every key number in this problem *looks meaningful* in hex — `0x7F/0x80` visibly straddle the sign boundary, which decimal 127/128 hides. And `& 0xFF` vs `& 255`: identical to the machine, but `0xFF` (two F's = eight 1s = one byte wide) tells the human **"keep the low 8 bits"** at a glance. Hex is for readers, not compilers.

---

## Reference implementation

<details>
<summary><b>Full code — forward + backward + <code>byte[]</code> version, 11 tests</b></summary>

```java
import java.util.*;

public class TextProcessor {

    /** Forward scan O(n) — write this first in the interview. */
    public static int getSizeOfLastCharForward(int[] data) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("data must be non-empty");
        }
        int i = 0;
        while (i < data.length - 1) {
            if (data[i] > 127) {
                i += 2;   // two-byte character
            } else {
                i += 1;   // one-byte character
            }
        }
        return i == data.length - 1 ? 1 : 2;
    }

    /** Backward scan — the big-file follow-up: only reads the tail. */
    public static int getSizeOfLastCharBackward(int[] data) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("data must be non-empty");
        }
        int count = 0;
        int i = data.length - 2;              // first witness; the last byte never counts
        while (i >= 0 && data[i] > 127) {
            count++;
            i--;
        }
        return count % 2 == 0 ? 1 : 2;
    }

    /** byte[] version — the signed-byte trap: (b & 0xFF) > 127, never b > 127. */
    public static int getSizeOfLastChar(byte[] data) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("data must be non-empty");
        }
        int count = 0;
        int i = data.length - 2;
        while (i >= 0 && (data[i] & 0xFF) > 127) {
            count++;
            i--;
        }
        return count % 2 == 0 ? 1 : 2;
    }

    // ---------------- tests ----------------
    private static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    private static void verifyBoth(int[] data, int expected, String name) {
        check(getSizeOfLastCharForward(data) == expected, name + " forward failed");
        check(getSizeOfLastCharBackward(data) == expected, name + " backward failed");
    }

    public static void main(String[] args) {
        verifyBoth(new int[]{127, 128, 126}, 2, "case1");        // 127 sits ON the threshold
        verifyBoth(new int[]{122, 123, 124}, 1, "case2");        // all single
        verifyBoth(new int[]{100}, 1, "case3");                  // single element
        verifyBoth(new int[]{200, 201}, 2, "case4");             // exactly one pair
        verifyBoth(new int[]{200, 200, 200, 200}, 2, "case5");   // all >127: two pairs
        verifyBoth(new int[]{100, 200, 150}, 2, "case6");        // 2nd byte also >127
        verifyBoth(new int[]{126, 128, 128, 126}, 1, "case7");   // pair in the middle
        verifyBoth(new int[]{200, 128, 128, 126}, 2, "case8");   // run parity
        verifyBoth(new int[]{128, 0}, 2, "case9");               // 2nd byte can be 0

        // signed-byte trap, live fire: (byte) 200 is −56
        check(getSizeOfLastChar(new byte[]{(byte) 200, (byte) 201}) == 2, "byte[] case failed");
        check(getSizeOfLastChar(new byte[]{100, 101}) == 1, "byte[] case2 failed");

        // defensive boundaries — and PROOF they fire
        try { getSizeOfLastCharForward(null); check(false, "null should throw"); }
        catch (IllegalArgumentException e) { /* expected */ }
        try { getSizeOfLastCharBackward(new int[]{}); check(false, "empty should throw"); }
        catch (IllegalArgumentException e) { /* expected */ }

        System.out.println("all tests passed");
    }
}
```

</details>

Every test guards a specific hole: `127` presses the threshold edge (a stray `>=` dies here) · `[100,200,150]` guards "2nd byte may be >127" · `[128,0]` guards "2nd byte may be 0" · the `(byte) 200` case fires the signed trap for real · null/empty **verify the defenses actually throw** (defense unproven is defense absent). `verifyBoth` cross-checks two independent algorithms against each other — stronger than checking either alone.

---

## Clarify before coding

- [ ] Input type: `int[]` or `byte[]`? *(decides the comparison — the trap lives here)*
- [ ] `null` / empty: throw? return a sentinel? *(this implementation throws; IXL always probes it)*
- [ ] Return form: 1/2, boolean, enum?
- [ ] Is well-formed guaranteed? If not, how to report a dangling first byte?
- [ ] File too large for memory? *(→ backward scan: seek to end, read back)*

---

## Complexity

| Metric | Forward | Backward |
|---|---|---|
| Time | O(n) | O(n) worst, **O(tail run)** typical — only the end of the file |
| Space | O(1) | O(1) |

---

## 30-second interview script

> "First byte ≤127 → one-byte char; >127 → leads a two-byte char — **every step is forced, zero ambiguity**, so it's a single scan, no DP. Forward: jump 1 or 2, stop before the last byte, the landing spot decides. For huge files, backward: the run of >127 bytes before the last one has a **locked entry point** (the ≤127 before it must stand alone), pairs consume it two at a time, so **parity alone decides** — seek to the end, read a few bytes. And if the input is `byte[]`: Java bytes are signed, `>127` is vacuously false — I'd use `(b & 0xFF) > 127`, or `b < 0` since this threshold sits exactly on the sign boundary."

**Think about it:** `[130, 130, 130]` is *not* well-formed (three `>127` bytes can't pair off). Both solutions still answer 1, in agreement, without noticing. Where does each one silently *use* the well-formed guarantee? *(Forward: "the landing spot is only ever n−1 or n." Backward: "a lone leader can always grab a next byte.")*