#!/usr/bin/bash
# The above bash location was retrieved using `which bash` in raspberry pi

# Location of the repo home
# echo "Process Id--$$"
repo_home="$1"
is_reset="$2"
is_pull="$3"

# Give access to current user
current_user=$(whoami)
chown -R "$current_user" "$repo_home"

# Go to repo location or exit with message
cd "$repo_home" || { echo "Repo Location Not Found"; exit 1; }

# echo "Current User--$current_user"
# echo "Current Location--$PWD"
# echo "Repo Home--$repo_home"

# Keeping this as fallback check
if [ "$PWD" != "$repo_home" ]; then
    echo "Current Location and Repo Home are different"
    exit 1
fi

# Iterate through all subdirectories
for dir in "$repo_home"/*
do
    # If the current directory is a directory
    if [ -d "$dir" ]
    then
        if [[ ! "$dir" == *"logs"* ]]; then
          # Change to the subdirectory
          cd "$dir" || { echo "Error 1"; exit 1; }
          # Iterate through all subdirectories of the current subdirectory
          for sub_dir in "$dir"/*
          do
              # If the current subdirectory is a directory
              if [ -d "$sub_dir" ]
              then
                  # Change to the subdirectory
                  cd "$sub_dir" || { echo "Error 2"; exit 2; }
                  echo "$sub_dir"

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
                  # Change back to the current subdirectory
                  cd "$dir" || { echo "Error 3"; exit 3; }
              fi
          done
          # Change back to the current directory
          cd "$repo_home" || { echo "Error 4"; exit 4; }
        fi
    fi
done

echo "GitHub Reset Pull Finished"
