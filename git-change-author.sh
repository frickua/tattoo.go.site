#!/bin/zsh

git filter-branch --force --commit-filter '
        if [ "$GIT_AUTHOR_EMAIL" = "anton_solianyk@epam.com" ];
        then
                GIT_AUTHOR_NAME="Anton Solianyk";
                GIT_AUTHOR_EMAIL="frick.ua@gmail.com";
                git commit-tree "$@";
        else
                git commit-tree "$@";
        fi' HEAD
