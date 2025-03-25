#!/usr/bin/bash
# The above bash location was retrieved using `which bash` in raspberry pi

# Location of the repo
# echo "Process Id--$$"
repo_loc="$1"
is_reset="$2"
is_pull="$3"

# Give access to current user
current_user=$(whoami)
chown -R "$current_user" "$repo_loc"

# Go to repo location or exit with message
cd "$repo_loc" || { echo "Repo Location Not Found"; exit 1; }

# echo "Current User--$current_user"
# echo "Current Location--$PWD"
# echo "Repo Location--$repo_loc"

# Keeping this as fallback check
if [ "$PWD" != "$repo_loc" ]; then
    echo "Current Location and Repo Location are different"
    exit 1
fi

if [ "$is_reset" = "true" ]; then
  echo "Checkout main branch"
  git checkout main 2>&1

  echo "Reset branch"
  git reset --hard 2>&1
fi

if [ "$is_pull" = "true" ]; then
  echo "Prune old branches"
  git fetch --prune 2>&1

  echo "Pull changes"
  git pull 2>&1
fi

echo "GitHub Reset Pull Finished: $repo_loc $is_reset $is_pull"
