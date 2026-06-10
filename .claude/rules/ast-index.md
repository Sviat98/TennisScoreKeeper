# ast-index RulesТ


## Mandatory Search Rules

1. **ALWAYS use ast-index FIRST** for any code search task
2. **NEVER duplicate results** — if ast-index found usages/implementations,
   that IS the complete answer
3. **DO NOT run grep "for completeness"** after ast-index returns results
4. **Use grep/Search ONLY when:**
   - ast-index returns empty results
   - searching for regex patterns (ast-index uses literal match)
   - searching for string literals inside code (`"some text"`)
   - searching in comments content

## Why ast-index

ast-index is much faster than grep on large repos and returns structured,
accurate results.

## Common Command Reference

| Task | Command |
|------|---------|
| Universal search | `ast-index search "query"` |
| Find type/class | `ast-index class "Name"` |
| Find symbol | `ast-index symbol "Name"` |
| Find usages | `ast-index usages "Name"` |
| Find implementations | `ast-index implementations "Interface"` |
| Call hierarchy | `ast-index call-tree "function" --depth 3` |
| Find callers | `ast-index callers "functionName"` |
| Module deps | `ast-index deps "module-name"` |
| File outline | `ast-index outline "path/to/file.ext"` |
| File imports | `ast-index imports "path/to/file.ext"` |

## Kotlin / Android Commands

| Task | Command |
|------|---------|
| Suspend functions | `ast-index suspend "Name"` |
| Flow usages | `ast-index flows "Name"` |
| Kotlin extensions | `ast-index extensions "Name"` |
| Composable functions | `ast-index composables "Name"` |
| Koin provides | `ast-index provides "Name"` |
| XML usages | `ast-index xml-usages "name"` |
| Resource usages | `ast-index resource-usages "name"` |

## Kotlin Multiplatform Notes

- Treat `commonMain`, `commonTest`, and platform source sets (`androidMain`,
  `desktopMain`, `wasmJsMain`) as first-class code, not support files.
- When explaining behavior, consider both Kotlin `expect`/`actual` edges and
  platform-specific implementations.
- Do not default to Android-only guidance in a KMP repo.

## Common use cases

- `ast-index usages "BaseViewModel"` — every ViewModel that extends the base
  class (constructors, DI registrations).
- `ast-index implementations "SetTemplateRepository"` — concrete implementations
  of the repository interface.
- `ast-index callers "observeMatch"` — who calls this function, without
  the noise of definition lines.
- `ast-index call-tree "observeMatch" -d 3` — transitive caller tree up
  to depth 3. Use when tracing a bug back to its user-facing entry point.
- `ast-index changed` — symbols modified in your current branch vs trunk.
  Great for "what am I actually changing?" summaries in PR descriptions.
- `ast-index outline AddTournamentScreen.kt` — structure of a file before
  reading it.
- `ast-index todo` — all TODO / FIXME / HACK comments, grouped.
- `ast-index deprecated` — every use of `@Deprecated` across the project.

## Scoping searches

All symbol-returning commands accept scope filters:

```bash
ast-index usages "Config" --module core/config        # only within one module
ast-index search "retry" --in-file HttpClient.kt      # only inside this file
ast-index symbol "User" --type class                  # only class-kind symbols
```

## When `ast-index` returns empty

Legitimate reasons:

- Symbol genuinely doesn't exist in the codebase.
- Index is stale — run `ast-index update` and retry.
- Symbol is behind a macro or preprocessor directive — ast-index doesn't
  expand macros. Fall back to Grep.
- You're searching for a string literal, not a symbol — use Grep.

Do **not** fall back to bulk `Read` of files in these cases. Use Grep with
a specific pattern.

## Mandatory read rules

1. **Before `Read`-ing any file over 500 lines, run `ast-index outline
   <file>` first.**
2. Use the outline to locate the specific symbol / line range you need,
   then `Read` that slice via `offset` / `limit`.
3. Never bulk-read large files without an outline — it wastes the agent's
   context window and produces worse answers.

## Rules for subagents

When you spawn a subagent for code search (via the Agent/Task tool), the
subagent does **not** inherit this file. Include the block below verbatim
in the subagent's prompt:

```
Use `ast-index` via Bash for code search (NOT grep / the Grep tool):
  ast-index search "query"           — universal search
  ast-index file "Name"              — find a file by name fragment
  ast-index symbol "Name"            — find a symbol definition
  ast-index class "Name"             — find a class / interface / struct
  ast-index usages "Name"            — every usage of a symbol
  ast-index callers "func"           — functions that call this one
  ast-index implementations "Iface"  — concrete implementers of an interface
  ast-index refs "Name"              — cross-references (defs + imports + usages)
Use Grep ONLY if ast-index returned empty.

Before Read-ing any file over 500 lines, FIRST run
  ast-index outline <file>
to get its structure, then Read only the targeted slice via offset/limit.
Never bulk-read large files.
```

## Index Management

- `ast-index rebuild` — Full reindex (run once after clone)
- `ast-index update` — After git pull/merge
- `ast-index stats` — Show index statistics