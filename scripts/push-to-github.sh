#!/usr/bin/env bash
set -euo pipefail

REPO="${1:-HengXin666/android-permission-lab-by-gpt}"
VISIBILITY="${2:---public}"

if ! command -v gh >/dev/null 2>&1; then
  echo "GitHub CLI 'gh' is required: https://cli.github.com/"
  exit 1
fi

if [ ! -d .git ]; then
  git init
  git add .
  git commit -m "Initial permission lab"
fi

gh repo create "$REPO" "$VISIBILITY" --source . --remote origin --push
