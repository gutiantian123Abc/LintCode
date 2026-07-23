# 353. Design Snake Game

**Medium** · `Design` · `Deque` · `Hash Set` · [LeetCode ↗](https://leetcode.com/problems/design-snake-game/)

> 📄 Paste-ready code: [`Solution.java`](./Solution.java)

Design a Snake game on a `height × width` screen. The snake starts at `(0, 0)` with length 1. Food appears **one piece at a time, in the given order** — piece *i + 1* only appears after piece *i* is eaten, and food never spawns on the snake.

| API | Behavior |
|---|---|
| `SnakeGame(width, height, food)` | Initialize the board and the food sequence |
| `move(direction)` | Move one step (`"U" / "D" / "L" / "R"`). Return the score, or **-1** on wall hit / self bite. Eating grows the snake by 1 and scores 1 |

**Example**

```text
SnakeGame(3, 2, [[1,2],[0,1]])

move : R   D   R        U   L        U
ret  : 0   0   1 (ate)  1   2 (ate)  -1 (wall)
```

---

## Design — two structures, one truth

`move` needs two abilities that no single structure provides:

| Structure | Owns | Why |
|---|---|---|
| `Deque<Location> body` | **Order** — head at `First`, tail at `Last` | The snake is FIFO: head enters one end, tail leaves the other. `getFirst()` reads the current head |
| `Set<Location> occupied` | **Membership** — O(1) self-collision lookup | `Deque.contains` would be O(snake length) |
| `List<Location> foods` | The food **sequence** | Food is consumed *in order* → `List + index` is its natural shape. Never pour it into a Set |

Every head-in / tail-out mutates **both** `body` and `occupied` — the same *dual-structure sync* pattern as LRU Cache ([LC 146](https://leetcode.com/problems/lru-cache/): HashMap + doubly linked list).

The raw `int[][] food` is adapted to `List<Location>` **at the boundary** (in the constructor): adapt dirty input once, use domain types everywhere inside.

---

## The ordering trap — tail leaves first

Head and tail move **simultaneously**. Stepping into the cell the tail *currently* occupies is legal, because the tail vacates on the same tick the head arrives.

```text
2×2 board, length-4 snake (head = 1 … tail = 4), circling clockwise:

   ┌───┬───┐      Next move: head steps onto 4's cell.
   │ 1 │ 2 │      Legal — 4 vacates on the same tick.
   ├───┼───┤
   │ 4 │ 3 │      Check collision BEFORE the tail leaves,
   └───┴───┘      and this legal loop is wrongly killed. ✗
```

> **Rule:** on a non-eating move, the tail leaves **both** structures **first** — only then run the body-collision check.
> When eating, the tail stays (that *is* the growth), and the check still runs.

```java
if (eating) {
    score++; foodIndex++;              // tail stays -> snake grows
} else {
    Location tail = body.pollLast();   // 1) tail vacates FIRST
    occupied.remove(tail);
}
if (occupied.contains(newHead)) {      // 2) THEN the collision check
    return -1;
}
```

---

## `Location` — why `equals` **and** `hashCode`, as a pair

Every `move` news a fresh `Location`, while `occupied` holds objects from earlier moves. Default `equals` is reference identity (`==`) and default `hashCode` derives from the address — so `contains()` never matches same-coordinate objects, and the snake **silently phases through itself**.

`HashSet` looks up in two steps:

```text
contains(newHead)
   ├─ 1) hashCode(newHead) ─► pick the bucket      "which shelf?"
   └─ 2) equals(...) inside that bucket            "is this the book?"
```

Override only one and the lookup still fails — wrong shelf, or the right shelf but `==` inside.

> **Iron law:** `equals`-equal objects **must** share a `hashCode` → hash exactly the fields that `equals` compares (`x`, `y`).

Two footnotes worth saying out loud in an interview:

- The parameter must stay `Object`. Typing it `equals(Location o)` would **overload**, not override — and `HashSet` calls `equals(Object)`.
- The `equals` below is the **interview simplification**: no `instanceof` / null guard, safe because it is only ever invoked against other `Location`s (private, type-closed usage). Production code adds the guard — or uses a Java 16+ `record`, which generates both methods automatically.

---

## Solution

```java
class SnakeGame {
    class Location {
        int x, y;

        public Location(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            Location other = (Location) o;
            return other.x == x && other.y == y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }


    private int width;
    private int height;
    private List<Location> foods;
    private int foodIndex;
    private int score;
    private Deque<Location> body;
    private Set<Location> occupied;


    public SnakeGame(int width, int height, int[][] food) {
        this.width = width;
        this.height = height;
        this.foods = new ArrayList<>();
        for (int[] f : food) {
            int r = f[0];
            int c = f[1];
            foods.add(new Location(r, c));
        }
        this.foodIndex = 0;
        this.score = 0;
        this.body = new ArrayDeque<>();
        this.occupied = new HashSet<>();
        Location start = new Location(0, 0);
        this.body.addFirst(start);
        this.occupied.add(start);
    }

    public int move(String direction) {
        Location head = body.getFirst();
        int r = head.x;
        int c = head.y;

        switch (direction) {
            case "U":
                r--;
                break;
            case "D":
                r++;
                break;
            case "L":
                c--;
                break;
            case "R":
                c++;
                break;
        }

        if (r < 0 || r >= height || c < 0 || c >= width) {
            return -1;
        }

        Location newHead = new Location(r, c);

        boolean eating = foodIndex < foods.size() && foods.get(foodIndex).equals(newHead);

        if (eating) {
            score++;
            foodIndex++;
        } else {
            Location tail = body.pollLast();
            occupied.remove(tail);
        }

        if (occupied.contains(newHead)) {
            return -1;
        }

        body.addFirst(newHead);
        occupied.add(newHead);

        return score;
    }
}
```

---

## Complexity

| Metric | Cost |
|---|---|
| `move` | **O(1)** — Deque end ops + Set ops, amortized constant |
| Constructor | O(food.length), one-time adaptation |
| Space | O(snake length + food); snake at most O(width × height) |

---

## 30-second interview script

> "Deque keeps body **order**, HashSet gives O(1) **self-collision** — two structures, one truth, kept in sync (the LRU pattern). Key ordering: on a non-eating move the **tail vacates before** the collision check, or a legal tail-chasing loop gets killed. `Location` overrides `equals` + `hashCode` **as a pair** — hash picks the bucket, equals confirms inside; either alone fails silently."

*Optional design flourish:* a real API would add a `dead` flag so every `move` after game-over returns -1 — locking the terminal state.