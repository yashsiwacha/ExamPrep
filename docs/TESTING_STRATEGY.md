# ExamPrep OS — Testing Strategy

**Version:** 1.0  
**Last Updated:** September 2025

---

## Testing Philosophy

> **Test behavior, not implementation.** Tests should break when observable behavior changes, not when internal refactoring occurs.

The testing pyramid for ExamPrep OS:

```
         /  E2E  \          ← Few, slow, high-confidence (Phase 2+)
        / UI Tests \         ← Screenshot tests, Compose testing (Phase 2)
       / Integration \       ← Room in-memory, ViewModel + Repo (Phase 1)
      /  Unit Tests   \      ← Domain engine tests (NOW — all phases)
     /─────────────────\
```

---

## Layer-by-Layer Testing

### 1. Domain Layer (:domain) — Unit Tests

**Priority: CRITICAL.** The domain layer must be exhaustively tested.  
All tests run on JVM — no emulator needed, sub-second execution.

```
domain/src/test/kotlin/com/examprep/domain/
├── engine/
│   ├── StudyPlanningEngineTest.kt
│   ├── MasteryEngineTest.kt
│   ├── QuizScoringEngineTest.kt
│   ├── RevisionEngineTest.kt
│   └── RecommendationEngineTest.kt
└── model/
    └── MarkingSchemeTest.kt
```

**Coverage target: 90%** for all engine implementations.

**Example — MasteryEngine:**
```kotlin
@Test
fun `topic with 85% accuracy and 5+ attempts should be MASTERED`() {
    val attempts = buildAttempts(correct = 17, total = 20)
    val result = engine.calculateMastery(notStartedRecord, attempts)
    assertEquals(MasteryState.MASTERED, result.masteryState)
}

@Test
fun `topic cannot be MASTERED with fewer than 5 attempts even at 100% accuracy`() {
    val attempts = buildAttempts(correct = 4, total = 4)
    val result = engine.calculateMastery(notStartedRecord, attempts)
    assertNotEquals(MasteryState.MASTERED, result.masteryState)
}
```

---

### 2. Data Layer (:data) — Integration Tests (Room in-memory)

**Priority: HIGH.** Validate DAO queries, mapper correctness, repository behavior.

```
data/src/test/kotlin/com/examprep/data/
├── dao/
│   ├── StudyPlanDaoTest.kt
│   ├── MasteryRecordDaoTest.kt
│   └── QuestionDaoTest.kt
├── mapper/
│   └── MapperTest.kt
└── repository/
    └── ExamRepositoryImplTest.kt
```

**Setup pattern:**
```kotlin
@RunWith(AndroidJUnit4::class)
class StudyPlanDaoTest {
    private lateinit var db: ExamPrepDatabase
    
    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ExamPrepDatabase::class.java
        ).allowMainThreadQueries().build()
    }
    
    @After
    fun teardown() { db.close() }
}
```

**Dependencies:** `androidx.room:room-testing`, `androidx.test:core`

---

### 3. ViewModel Tests

**Priority: MEDIUM.** Test state machines and event handling.

Use `TestCoroutineDispatcher` + `Turbine` for Flow testing:

```kotlin
@Test
fun `emitting StartSession event transitions to Focusing state`() = runTest {
    viewModel.uiState.test {
        viewModel.onEvent(FocusSessionEvent.StartSession)
        val state = awaitItem()
        assertTrue(state.isRunning)
        cancelAndConsumeRemainingEvents()
    }
}
```

---

### 4. UI Tests — Compose (Phase 2)

**Priority: LOW for MVP.** Add after core screens are stable.

```kotlin
@get:Rule
val composeTestRule = createComposeRule()

@Test
fun dashboardShowsExamCountdown() {
    composeTestRule.setContent {
        ExamPrepTheme { DashboardScreen(...) }
    }
    composeTestRule.onNodeWithText("days").assertIsDisplayed()
}
```

---

## Test Data Builders

All tests use builder functions — **never use production seed data** in tests:

```kotlin
// In test source set: TestBuilders.kt
fun buildQuizAttempt(
    accuracy: Float = 0.7f,
    totalQuestions: Int = 10
): QuizAttempt = QuizAttempt(
    id = UUID.randomUUID().toString(),
    quizId = "test_quiz",
    userId = "test_user",
    accuracy = accuracy,
    score = accuracy * totalQuestions * 4,
    totalQuestions = totalQuestions,
    correctAnswers = (accuracy * totalQuestions).toInt(),
    // ... other fields with sensible defaults
)
```

---

## CI/CD Test Gates (Phase 2)

When CI is configured (GitHub Actions / Bitrise):

| Gate | Runs | Blocks merge? |
|------|------|--------------|
| Unit tests (`:domain`) | Every PR | ✅ Yes |
| Integration tests (`:data`) | Every PR | ✅ Yes |
| ViewModel tests (`:app`) | Every PR | ✅ Yes |
| Compose UI tests | Daily | ⚠️ Warning only |
| Full instrumented test suite | Pre-release | ✅ Yes |

---

## Dependency Reference

```toml
# gradle/libs.versions.toml

[versions]
mockk = "1.13.12"
turbine = "1.1.0"
coroutines = "1.9.0"

[libraries]
mockk = { group = "io.mockk", name = "mockk", version.ref = "mockk" }
turbine = { group = "app.cash.turbine", name = "turbine", version.ref = "turbine" }
kotlinx-coroutines-test = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-test", version.ref = "coroutines" }
androidx-room-testing = { group = "androidx.room", name = "room-testing", version.ref = "room" }
```
