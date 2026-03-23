[![CI](https://img.shields.io/github/actions/workflow/status/kuneiphorm/kuneiphorm-runtime/ci.yml?branch=master&label=CI)](https://github.com/kuneiphorm/kuneiphorm-runtime/actions)
[![kuneiphorm-runtime](https://img.shields.io/github/v/release/kuneiphorm/kuneiphorm-runtime?include_prereleases&label=kuneiphorm-runtime)](https://github.com/kuneiphorm/kuneiphorm-runtime/releases)
![License](https://img.shields.io/badge/License-Apache_2.0-blue)
![Java](https://img.shields.io/badge/Java-21-blue)

# kuneiphorm-runtime

Core runtime primitives for kuneiphorm-generated and interpreted lexers and parsers.

This module is the foundation of the kuneiphorm toolkit. Generated lexers and parsers depend on it at runtime, while the compilation modules produce code that targets its interfaces. It has **zero external dependencies**.

## Package overview

```
org.kuneiphorm.runtime
├── charflow      Character-level stream reading
├── token         Token data model and production
├── tokenflow     Token stream consumption
├── parser        Parse interface
└── exception     Error hierarchy
```

## Packages

### `charflow` : Character-level stream

Provides character-by-character reading with single-character lookahead and position tracking.

| Class | Description |
|---|---|
| `CharFlow` | Wraps a `Reader` with single-character lookahead, line/column tracking, and an optional source name. |
| `CharFlowUtils` | Static utilities for common scanning patterns: `skipBlanks`, `skipComment` (Java-style `//` and `/* */`), `skipTrivia` (blanks + comments), `readWhile` (consume characters matching a predicate). |

`CharFlow` is the primary input abstraction. Tokenizers consume characters from it and produce tokens.

### `token` , Token data model and production

Defines the data types that tokenizers produce.

| Class | Description |
|---|---|
| `TokenResult<L>` | Sealed interface , the sum type of `Token<L>` and `Eof<L>`. |
| `Token<L>` | Record holding a token's `label`, source position (`line`, `column`, `size`), and optional `data` payload. |
| `Eof<L>` | Record marking end of input, carrying the position where EOF was reached. |
| `Tokenizer<L>` | `@FunctionalInterface` , reads the next `Token<L>` from a `CharFlow`. |

The type parameter `L` is the label type , typically an enum representing the language's token categories (keywords, operators, literals, etc.).

### `tokenflow` , Token stream consumption

Provides the token-level reading interface used by parsers.

| Class | Description |
|---|---|
| `TokenFlow<L>` | Interface defining the token stream contract: `peek`, `next`, `expect`, `accept`, `hasMore`, `getSource`. |
| `SimpleTokenFlow<L>` | Default implementation backed by a `CharFlow` and a `Tokenizer`. Lazily produces tokens on demand. |
| `PushbackTokenFlow<L>` | Decorator that allows previously consumed tokens to be pushed back onto the stream in LIFO order. Useful for multi-token lookahead and speculative parsing. |
| `FilteredTokenFlow<L>` | Decorator that silently skips tokens matching a predicate. Useful for hiding whitespace and comments from the parser. |

`TokenFlow<L>` is the interface that parsers consume. The decorator pattern allows composing behaviors:

```java
var chars = new CharFlow(reader, "example.lang");
var raw   = new SimpleTokenFlow<>(chars, tokenizer);
var clean = new FilteredTokenFlow<>(raw, t -> t.label() == WS || t.label() == COMMENT);
var flow  = new PushbackTokenFlow<>(clean);
// Parser sees only meaningful tokens, with pushback capability
```

### `parser` , Parse interface

| Class | Description |
|---|---|
| `Parser<L, R>` | `@FunctionalInterface` , transforms a `TokenFlow<L>` into a result of type `R`. |

`Parser` is intentionally minimal. It imposes no constraints on the result type , it could be an AST, a value, or a side effect.

### `exception` , Error hierarchy

All parse-time errors extend `SyntaxException`, which carries an optional source name, line, column, and size.

| Class | Description |
|---|---|
| `SyntaxException` | Abstract base class for all syntax errors. |
| `UnexpectedCharException` | Thrown by tokenizers , expected character X, found Y. |
| `UnexpectedEndOfInputException` | Thrown when EOF is reached unexpectedly. |
| `UnexpectedTokenException` | Thrown by parsers , expected token label X, found Y. |

Error messages include the source name when available:

```
example.lang: line 3, column 7 (size: 1), expected 'a' but found 'b'
```

## Key design decisions

### Every character belongs to exactly one token

The `Tokenizer` contract requires that the tokenizer never skips characters. Every character in the input is consumed and assigned to a token. This invariant makes `CharFlow.hasMore()` a reliable EOF signal , `SimpleTokenFlow` can check for EOF without involving the tokenizer.

This means whitespace and comments must be tokenized (not skipped). Use `FilteredTokenFlow` to hide them from the parser.

### TokenFlow is an interface

`TokenFlow<L>` is an interface, not a class. This enables the decorator pattern (`PushbackTokenFlow`, `FilteredTokenFlow`) and allows users to implement custom wrappers without subclassing.

### Source name propagation

Source names flow through the entire pipeline:

```
CharFlow("example.lang")
  → SimpleTokenFlow.getSource() returns "example.lang"
    → FilteredTokenFlow.getSource() delegates to SimpleTokenFlow
      → PushbackTokenFlow.getSource() delegates to FilteredTokenFlow
        → Exceptions include "example.lang" in their message
```

Wrappers delegate to the underlying flow for both source name resolution and exception throwing. They never duplicate exception logic the delegate already handles.

## Requirements

- Java 21+
- No external dependencies
