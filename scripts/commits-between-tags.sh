#!/bin/bash

# get the latest two tags
tags=$(git tag --sort=-creatordate | head -n 2)

# split tags into an array
tag_array=($tags)

# check if there are enough tags
if [ ${#tag_array[@]} -lt 2 ]; then
    echo "Not enough tags in the repository. Need at least two tags."
    exit 1
fi

# get the latest two tags
latest_tag=${tag_array[0]}
second_latest_tag=${tag_array[1]}

# extract short hashes and full commit messages without pager
git --no-pager log --pretty=format:"%h - %B" $second_latest_tag..$latest_tag
