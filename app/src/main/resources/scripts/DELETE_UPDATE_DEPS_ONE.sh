#!/usr/bin/bash
# The above bash location was retrieved using `which bash` in raspberry pi

# Location of the repo home
# echo "Process Id--$$"
repo_loc="$1"
delete_update_dependencies_only="$2"

# Give access to current user
current_user=$(whoami)
chown -R "$current_user" "$repo_loc"

# Go to repo location or exit with message
cd "$repo_loc" || { echo "Repo Location Not Found"; exit 1; }

# echo "Current User--$current_user"
# echo "Current Location--$PWD"
# echo "Repo Home--$repo_home"

# Keeping this as fallback check
if [ "$PWD" != "$repo_loc" ]; then
  echo "Current Location and Repo Location are different"
  exit 1
fi

# git checkout main and pull
git checkout main 2>&1
git pull 2>&1
# get all branches
IFS=$'\n' branches=($(git branch -a))
# Save to arrays for remote and local branches
remote_branches=()
local_branches=()

for branch in "${branches[@]}";
do
  # Remove leading whitespace
  branch="${branch#"${branch%%[![:space:]]*}"}"
  # Remove trailing whitespace
  branch="${branch%"${branch##*[![:space:]]}"}"

  if [[ "$branch" == *"/origin/"* ]];
  then
    remote_branches+=("$branch")
  else
    # Remove '* ' prefix from local branch
    branch="${branch#\* }"
    local_branches+=("$branch")
  fi
done

for branch in "${remote_branches[@]}";
do
  if [[ "$branch" != "remotes/origin/main" ]] && [[ "$branch" != *"remotes/origin/HEAD"* ]];
  then
    branch="${branch#remotes/origin/}"
    if [[ "$delete_update_dependencies_only" == "true" ]];
    then
      if [[ "$branch" == *"update_dependencies"* ]];
      then
        git push origin -d "$branch" 2>&1
      fi
    else
      git push origin -d "$branch" 2>&1
    fi
  fi
done

for branch in "${local_branches[@]}";
do
  if [[ "$branch" != "main" ]];
  then
    if [[ "$delete_update_dependencies_only" == "true" ]];
    then
      if [[ "$branch" == *"update_dependencies"* ]];
      then
        git branch -D "$branch" 2>&1
      fi
    else
      git branch -D "$branch" 2>&1
    fi
  fi
done

# prune old branches and pull again
git fetch --prune 2>&1
git pull 2>&1

echo "Finished branches delete: $repo_loc $delete_update_dependencies_only"
