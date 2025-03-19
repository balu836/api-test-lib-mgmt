## Sharing code between protected branches

We can cherry-pick commits between the protected branches in order to reduce the overall divergence between them.

So if a feature branch `feature-1` is created off the `master` branch and merged back in,
we can move all the changes in `feature-1` into the `cfems-release-6.0` branch if required.

Obviously because `feature-1` was written on the bases of the `master` codebase
we may need to do some modifications to get it to work against the `cfems-release-6.0` branch.

Not all features will need to be shared between both protected branches.
Tests that are for features only present in the `master` branch do not need to be copied to the `cfems-release-6.0` branch.

Given the following scenario:

- We have a feature branch `feature-1` created off the `master` branch and merged back in.
- We want to share those changes with the `cfems-release-6.0` branch.

Follow these steps to move the code across:

- Inspect the merge request for the `feature-1` branch and go to the merge commit (See the statement "The changes were merged into master with <merge-commit-sha>").

- The merge commit is likely to have 2 parents - One will be the previous head of the `master` branch.
The other will either be the last commit on `feature-1` branch or a squashed commit (if the merge train did a squash during merging).
A good way to check is to compare this commit sha with the last entry in the "Commits" tab of the merge request.

- If the `feature-1` branch was squashed before merging, then you don't need to squash it again.
You now have your candidate for cherry-picking into the `cfems-release-6.0` branch.

- If it was not, then follow the steps below to squash the `feature-1` commits first.

    - Create a new branch off the `master` branch (e.g. `feature-1-squashed`).

    - On the `feature-1-squashed` branch, check that the merge request commits exist
    
        ```bash
        git log | grep <commit-sha>
        ```
    
    - On the `feature-1-squashed` branch, squash all the commits in the merge request into a single commit.
    See [here](https://stackoverflow.com/questions/39023360/git-squash-commits-in-the-middle-of-a-branch/39023568) for guidance.
    
        ```bash
        git rebase -i HEAD..N
        ```

    - Do a git log to get the commit sha of the squashed commit.
    
- At this point, you have a single squashed commit (either in `master` or `feature-1-squashed`)
that needs to be copied over to the `cfems-release-6.0` branch.

- Create a branch off the `cfems-release-6.0` branch (e.g. `feature-1-release-6.0`).

    ```bash
    git checkout cfems-release-6.0
    git pull
    git branch feature-1-release-6.0
    git checkout feature-1-release-6.0
    ```

- On the `feature-1-release-6.0`, cherry-pick the commit into the working directory.

    ```bash
    git cherry-pick <commit-sha> --no-commit
    ```

- Review the code changes and see that there are in-line with the content of the merge request.
You will likely have conflicts that need resolving as you're bringing in a commit that was based on another protected branch.

- Fix any conflicts and commit the changes (making sure you indicate that it is a cherry-pick of a specified sha).

    ```bash
    git add .
    git commit -m "Cherry-picking <commit-sha> from merge request <merge-request-id>: <merge-request-title>"
    ```

- Push the changes on `feature-1-release-6.0` to  remote server and run tests on Jenkins (if required)

- When tests pass, merge into `cfems-release-6.0`

    - Add the label `cherry-picked` to the merge request.
    
    - Set the merge train to delete the `feature-1-release-6.0` afterwards.
