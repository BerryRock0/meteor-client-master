#!/usr/bin/env bash
# Builds Meteor Client once per supported Minecraft version.
#
# Every version needs a descriptor in gradle/mc-versions/<version>.properties and its own
# sources in src/mc/<version>/. See COMPAT.md.
set -euo pipefail

cd "$(dirname "$0")/.."

if [ $# -gt 0 ]; then
  versions=("$@")
else
  mapfile -t versions < <(
    find gradle/mc-versions -name '*.properties' -printf '%f\n' | sed 's/\.properties$//' | sort -V
  )
fi

if [ ${#versions[@]} -eq 0 ]; then
  echo "No version descriptors found in gradle/mc-versions/." >&2
  exit 1
fi

status=0
GRADLE_TIMEOUT="${GRADLE_TIMEOUT:-600}"
for version in "${versions[@]}"; do
  echo "=== Building for Minecraft $version ==="
  if timeout "$GRADLE_TIMEOUT" ./gradlew build -PminecraftVersion="$version" --no-daemon --no-parallel -Dorg.gradle.jvmargs="-Xmx6G"; then
    echo "--- OK: $version"
  else
    echo "--- FAILED: $version" >&2
    status=1
  fi
done

exit "$status"
