#!/usr/bin/bash
# The above bash location was retrieved using `which bash` in raspberry pi

# Location of the repo
# echo "Process Id--$$"
repo_loc="$1"
branch_name="$2"

# Give access to current user
current_user=$(whoami)
chown -R "$current_user" "$repo_loc"

# Go to repo location or exit with message
cd "$repo_loc" || { echo "Repo Location Not Found"; exit 1; }

# echo "Current User--$current_user"
# echo "Current Location--$PWD"
# echo "Repo Location--$repo_loc"
# echo "Branch Name--$branch_name"

# Keeping this as fallback check
if [ "$PWD" != "$repo_loc" ]; then
    echo "Current Location and Repo Location are different"
    exit 1
fi

# this is generally not needed because reset step in init_exit does checkout main
echo "Checkout main branch"
git checkout main 2>&1

# Create new branch for updates
echo "Creating new branch"
git checkout -b "$branch_name" 2>&1

# Commit and push
echo "Committing and pushing"
branch_pushed="no"
if ! git status | grep "nothing to commit" > /dev/null 2>&1; then
	git add . 2>&1
	git commit -am 'Dependencies Updated (https://bit.ly/app-dependency-update)' 2>&1
	git push origin -u "$branch_name" 2>&1
	branch_pushed="yes"
fi

# Cleanup
echo "Cleaning up"
git checkout main 2>&1
if [ $branch_pushed = "yes" ]; then
  git branch -D "$branch_name" 2>&1
fi

echo "Finished: $repo_loc"
