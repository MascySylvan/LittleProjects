# Project Structure

```
src/
├── ai_project/
│   └── self_avoiding_walk/    # Random walk simulation
│       ├── Canvas.java        # JFrame + rendering + game loop
│       └── Walk.java          # Walk logic and coordinate tracking
├── games/
│   └── snake/
│       ├── Canvas.java        # Manual snake game (JFrame + rendering)
│       ├── Snake.java         # Snake state and movement logic
│       ├── Food.java          # Food placement logic
│       └── ai/
│           ├── Canvas.java    # AI snake game (JFrame + rendering)
│           ├── Snake.java     # Snake with Hamiltonian cycle AI
│           ├── Food.java      # Food placement for AI variant
│           └── Menu.java      # Swing menu with grid size settings
└── simple_machine/
    └── cube_timer/
        └── CubeTimer.java     # Stopwatch timer (single-file app)
```

## Conventions

- **Package naming**: lowercase, underscore-separated segments matching folder structure (e.g. `games.snake.ai`).
- **Entry points**: Each sub-project has a class with `public static void main` — typically the `Canvas` or primary class.
- **Architecture pattern**: Each visual app follows a similar structure:
  - A `Canvas` class extending `JFrame` that owns the game loop (`javax.swing.Timer`), handles input (`KeyAdapter`), and performs rendering.
  - Separate model classes for game entities (e.g. `Snake`, `Food`, `Walk`) holding state and logic.
- **Rendering**: Double-buffered painting via `paint()` → off-screen image → `paintComponent()`. The AI snake variant uses `JPanel` with `paintComponent` directly.
- **No shared code between sub-projects** — each is fully self-contained.
