# Naturallang

## BuNaMo Verb Data
- Default path: `vendor/BuNaMo/verb`
- System property: `-Dbunamo.verb.dir=/absolute/path/to/verb`
- System property: `-Dbunamo.root=/absolute/path/to/BuNaMo`
- Environment: `BUNAMO_VERB_DIR=/absolute/path/to/verb`
- Environment: `BUNAMO_ROOT=/absolute/path/to/BuNaMo`
- `BuNaMoVerbLookup` tries the overrides in order and falls back to the default.

## CLI
- Build: `./gradlew installDist`
- Interactive (Gradle): `./gradlew run`
- Script-friendly: `scripts/run.sh --script`
- Interactive via script: `scripts/run.sh --interactive`
- Script-friendly (direct binary): `build/install/naturallang/bin/naturallang --script`
- Script-friendly with input file: `build/install/naturallang/bin/naturallang --script --max-questions=5 < answers.txt`
- Flag: `--script` disables color/prompt noise for piping.
- Flag: `--max-questions=N` exits after N prompts.
- Flag: `--strict` uses strict grammar matching (default).
- Flag: `--no-pronouns` omits explicit Irish pronouns in answers.
- Flag: `--pronouns=always|prefer-synthetic|strict|omit` controls pronoun inclusion.
- Arg: a numeric arg sets the prompt color, e.g. `36`.
