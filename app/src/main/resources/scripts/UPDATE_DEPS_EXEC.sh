#!/usr/bin/bash
# The above bash location was retrieved using `which bash` in raspberry pi

# Location of the repo
# echo "Process Id--$$"
repo_loc="$1"
branch_name="$2"
repo_type="$3"

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

# Run npm install if repo type is npm
if [ "$repo_type" = "NODE" ]; then
    echo "Running npm install for NODE repo"
    npm install --package-lock-only 2>&1
fi

# Commit and push
echo "Committing and pushing"  2>&1
echo git status 2>&1

# Check if there are uncommitted changes
if ! git diff-index --quiet HEAD --; then
	git add . 2>&1
	git commit -am 'Dependencies Updated (https://bit.ly/dep-mgmt)' 2>&1
	git push origin -u "$branch_name" 2>&1
	echo "Pushed new branch"
fi

# Cleanup
echo "Cleaning up"
git checkout main 2>&1
git branch -D "$branch_name" 2>&1

echo "Finished: $repo_loc"  2>&1
