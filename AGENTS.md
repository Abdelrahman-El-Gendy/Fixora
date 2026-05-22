# Fixora AI Agent Guide

## Project Overview

Fixora is a multi-module Android application for booking home services (like plumbing, electrical work, etc.). It uses **Clean Architecture** with strict separation of concerns across distinct Gradle modules.

**Tech Stack:**
- Kotlin 2.3.0, Compose (Material3), Hilt (dependency injection)
- Retrofit + kotlinx-serialization for networking, Room for local persistence
- MutableStateFlow for state management in ViewModels
- MinSdk 26, TargetSdk 35, Java 17

## Module Architecture

The project follows a **layered module structure**, NOT a feature-module hierarchy:

```
settings.gradle.kts includes:
├── app (main application, orchestrates all features)
├── core:common (utilities, Result sealed class, DispatcherProvider)
├── core:model (domain data classes: User, ServiceProvider, Booking, etc.)
├── core:domain (repository interfaces, use cases)
├── core:data (repository implementations, Hilt bindings)
├── core:network (Retrofit API, MockInterceptor, NetworkModule)
├── core:database (Room DAOs, entities, FixoraDatabase)
├── core:designsystem (reusable Compose components: FixoraButton, FixoraTextField, GlassCard)
├── feature:auth (LoginScreen, RegisterScreen, AuthViewModel)
├── feature:home (HomeScreen, HomeViewModel, service browsing)
├── feature:provider (ProviderDetailScreen, ProviderViewModel)
├── feature:booking (BookingScreen, BookingViewModel)
└── feature:profile (ProfileScreen, ProfileViewModel)
```

**Key dependency flow:** `feature:* → core:domain/designsystem → core:data → core:network/database → core:model/common`

## Critical Architecture Patterns

### 1. **Result Pattern for Async Operations**

Located in `core:common/result/Result.kt`:
```kotlin
sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>
    data class Error(val exception: Throwable? = null, val message: String? = null) : Result<Nothing>
    object Loading : Result<Nothing>
}
```
Extension function `Flow<T>.asResult()` converts flows to handle loading/error states. Though currently not widely used in view models, this pattern should be adopted for state management consistency.

### 2. **Clean Architecture with Repository Pattern**

**Domain layer** (`core:domain/`) defines repository interfaces:
```kotlin
// Example: core:domain/repository/AuthRepository.kt
interface AuthRepository {
    fun getAuthenticatedUser(): Flow<User?>
    suspend fun login(email: String, password: String): User
    suspend fun register(name: String, email: String, password: String): User
    suspend fun logout()
}
```

**Data layer** (`core:data/`) implements repositories and provides Hilt bindings in `DataModule.kt`:
```kotlin
@Module @InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds @Singleton
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository
}
```

This keeps domain logic independent of implementation details (network vs. database).

### 3. **Use Cases as Single-Responsibility Operators**

Located in `core:domain/usecase/`, each use case is an injectable class with `invoke` operator:
```kotlin
class LoginUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): User {
        return authRepository.login(email, password)
    }
}
```
Features inject use cases into ViewModels. **Keep use cases thin** — they should orchestrate repositories, not contain business logic.

### 4. **ViewModel State Management Pattern**

All feature ViewModels use `MutableStateFlow` + sealed UI state interfaces:
```kotlin
sealed interface AuthUiState {
    object Idle : AuthUiState
    object Loading : AuthUiState
    data class Success(val userName: String) : AuthUiState
    data class Error(val message: String) : AuthUiState
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    fun login(email: String, password: String) {
        viewModelScope.launch { /* call use case, update _uiState */ }
    }
}
```
**Convention:** Private `_uiState` backing field + public `uiState` StateFlow. Input validation happens in ViewModel methods before calling use cases.

### 5. **Network Layer with Mock Interceptor**

`core:network/api/MockInterceptor.kt` intercepts HTTP requests and serves mock JSON responses offline. This enables development without a backend. The interceptor is added to OkHttpClient in `NetworkModule.kt`:
```kotlin
.addInterceptor(MockInterceptor()) // Must come before FixoraApi instantiation
```
When implementing API endpoints, ensure mock responses are comprehensive in MockInterceptor to unblock feature development.

### 6. **Designsystem Components for UI Consistency**

`core:designsystem/` provides reusable Compose components:
- `FixoraButton` — standardized button styling
- `FixoraTextField` — input field with design system theme
- `GlassCard` — glassmorphism card container
- `RatingBar` — provider rating display

**Always use these** over raw Material3 components when available to maintain consistency.

## Build & Compilation Commands

All commands run from project root (`D:\Android\Fixora`):

```bash
# Assemble debug APK
./gradlew assembleDebug

# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Check lint errors
./gradlew lint

# Build and run specific module
./gradlew :feature:auth:assembleDebug

# Clean build (use when facing stale cache issues)
./gradlew clean build
```

**Gradle Configuration** (`gradle.properties`):
- Parallel & incremental compilation enabled for speed
- Configuration caching enabled (pre-configured tasks)
- KSP incremental processing enabled (faster annotation processing for Hilt)
- Max JVM heap: 4GB

**Note:** Use `./gradlew` (Linux/Mac) or `./gradlew.bat` (Windows). On Windows, you may need to set execution policy for PowerShell.

## Data Flow Example: Booking a Service

1. **UI** (`feature:booking/BookingScreen.kt`) — User submits booking form
2. **ViewModel** (`BookingViewModel`) — Validates input, calls `CreateBookingUseCase`
3. **Use Case** (`core:domain/usecase/CreateBookingUseCase`) — Orchestrates repository
4. **Repository** (`core:data/repository/BookingRepositoryImpl`) — Calls network API & updates local database
5. **Network** (`core:network/api/FixoraApi.POST /bookings`) — MockInterceptor returns mock response in dev
6. **Database** (`core:database/FixoraDatabase`) — Room persists booking locally
7. **Flow emits** back through layers → ViewModel updates UI state → Compose recomposes

**Cross-module communication:** Features communicate through shared domain use cases & repositories only. Direct feature-to-feature imports are prohibited.

## Dependency Injection (Hilt) Conventions

- **Singleton scope:** Repository implementations, API client, Database
- **ViewModel scope:** Feature-specific use cases injected into @HiltViewModel classes
- **Module locations:**
  - `core:network/di/NetworkModule` — Retrofit, OkHttpClient
  - `core:database/di/DatabaseModule` — Room database provider
  - `core:data/di/DataModule` — Repository bindings
  - Features don't define modules; they inject via @HiltViewModel

**Module binding pattern:**
```kotlin
@Module @InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds @Singleton
    abstract fun bind[Interface](impl: [Implementation]): [Interface]
}
```

## Testing Conventions

- **Unit tests:** Place in `src/test/java/`, test use cases & repository logic
- **UI tests:** Place in `src/androidTest/java/`, use Espresso + Compose test utils
- Use `kotlinx-coroutines-test` for suspending function tests
- Mock repositories in ViewModel tests to isolate UI logic

Test files mirror source structure: `src/test/java/com/fixora/core/domain/usecase/LoginUseCaseTest.kt` for `src/main/java/.../LoginUseCase.kt`.

## Project-Specific Patterns to Remember

1. **No Kotlin Serialization defaults:** Json configured with `ignoreUnknownKeys = true` & `coerceInputValues = true` for robustness
2. **Sealed UI states:** Every feature screen has its own `sealed interface [FeatureName]UiState` in ViewModel
3. **Mock-first network development:** MockInterceptor allows building features without backend; implement real endpoints incrementally
4. **Cross-layer validation:** Input validation in ViewModels before hitting repositories; repository layer assumes valid input
5. **No framework logic in domain:** Core:domain contains only interfaces & use cases; keeps it testable & framework-agnostic

## When Adding New Features

1. Create new module under `feature:*` with compose dependency
2. Define repository interfaces in `core:domain/repository/`
3. Implement repository in `core:data/repository/`, add Hilt binding to `DataModule`
4. Create use cases in `core:domain/usecase/`
5. Build ViewModel with sealed UI state, inject use cases
6. Implement Compose screens, use `core:designsystem` components
7. Add mock endpoints to MockInterceptor for offline dev
8. Update `app/build.gradle.kts` to include new feature module

---

**Last Updated:** May 21, 2026 | **Architecture:** Clean Architecture with layered modules | **Build System:** Gradle with KTS

