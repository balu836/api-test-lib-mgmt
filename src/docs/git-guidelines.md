## Team Git Guidelines

We have some guidelines (and a few rules) that we hope developers on this repo to follow.
Can discuss changes to these guidelines with the team on the `#cfems-test-automation` Slack channel.

### Branch Structure

We have two **protected** branches that correspond to the two versions of the CFEMS systems we are testing.

- The `master` branch contains code for testing the **master** branch of the CFEMS system.

- The `cfems-release-6.0` branch contains code for testing the **release 6.0** branch of the CFEMS system.

Both branches are protected - which means changes to them can only be made by authorised users via merge requests.

**Important:**

- If you are writing tests that are specific to the release 6.0 CFEMS system 
then create your feature branches from the `cfems-release-6.0` branch and merge them back into the `cfems-release-6.0` branch.

- Otherwise, all tests that will be shared between both CFEMS should be done on the `master` branch.
We will then copy the commits over to the `cfems-release-6.0` as required.


### Our Git Workflow

This are the steps developers should take when using Git on this project.

1. Before making changes:

    - Create a feature branch off the `master` or `cfems-release-6.0` branch.

        - Try not to branch off other branches if you can help it. Would be much easier to branch off `master` or `release-6.0`.
    
    - Use the following naming convention so it's easy to know which protected branch a given feature branch was created from:
    
        - Use `main/fwm-<ticket-number>-<branch-name>` for features off the `master` branch. For example `main/fwm-2342-rit-field-validation`
        
        - Use `r6.0/fwm-<ticket-number>-<branch-name>` for features off the `cfems-release-6.0` branch. For example `r6.0/fwm-2342-rit-field-validation`
    
    - Push your newly created branch to the remote repository.
    
1. While making code changes on your feature branch, try to pull in changes from your protected branch (`master` or `cfems-release-6.0`)
and merge them into your feature branch as frequently as you can.
This way you can reconcile breaking changes easily.
    
    
1. Merging changes to `master` or `cfems-release-6.0`:

    - **Important:** Only merge a feature branch back into the branch from which it was created.
    That is, all `main/*` branches to `master` and all `r6.0/*` branches to `cfems-release-6.0`.
        
    - When you're wanting to merge back into a protected branch (`master` or `cfems-release-6.0`),
    switch to the protected branch and pull down all changes.
    
    - Switch back to your feature branch and merge in all changes from the protected branch (`master` or `cfems-release-6.0`) onto your feature branch (and resolve any conflicts).
    
    - Push any outstanding changes on your local feature branch branch to remote repository.
    
    - Run the "main scenarios" tests from your feature branch through Jenkins
    
        - You can use the [tagged-scenarios-run](http://jenkins-nonprod.cfems.awsfed-dev.dwpcloud.uk:8080/job/QA/job/cfems-e2e-tests-common/job/tagged-scenarios-run/) build job with the `TAG_NAME=MainScenarios` option.
        
        - Because the grid has limited resources, please check to see if other test jobs are currently running before kicking off your job.
     
        - This can take a while (about 20 mins) so you may not always have to do this - especially if the changes you are merging are unlikely to affect the tests. Use your judgement on this one :-)
        
    - When you're happy your changes can be merged into the protected branch (`master` or `cfems-release-6.0`),
    raise a merge request to merge your branch into the protected branch.
    
    - Make sure your merge request passes the merge pipeline. Fix any issues that may arise if not.
    
    - Go through the "Changes" tab on your merge requests to make sure all the changes to be applied to the protected branch are as expected.
    
    - Ask someone else on the team to merge complete the merge request.
    
        - This way someone else can have a quick scan through and may pick up things you've missed.
         
        - If you are confident your changes are okay (e.g. documentation changes, or slight code changes) and you have permissions to merge into master, you may choose to do so yourself.


### Handling Merge Requests

Some developers have permissions to merge into the protected branches (`master` or `cfems-release-6.0`).
Please follow these steps/guidelines when merging into a protected branch.

- Check that the merge pipeline associated with the merge request has passed.
If there are any failures, fix them or raise with the developer who submitted the merge request.

- Have a quick look at the changes on the merge request (on the Gitlab UI) and raise any issues you find with the developer.

- Check that the protected branch is not behind the protected branch (`master` or `cfems-release-6.0`).
You can do this on the Gitlab UI by comparing both branches or on your local machine by running:

    ```bash
    git rev-list --left-only --count origin/master...origin/<feature-branch>
    ```
    Or
    ```bash
    git rev-list --left-only --count origin/cfems-release-6.0...origin/<feature-branch>
    ```

    If it is, try to merge in the changes from the protected branch onto the feature branch first and push.
    This will trigger the merge pipeline again and it should then pass.

- Once the merge pipeline has passed and you are happy with the changes, merge the changes on the Gitlab GUI.
    

### Some Useful Links

- [Rebase vs Merge?](https://www.youtube.com/watch?v=CRlGDDprdOQ)

- [DWP Engineering Practive Branching Strategy](https://confluence.service.dwpcloud.uk/display/EN/Git+Release+and+Branching+Strategy)

- [Feature branch workflow](https://www.atlassian.com/git/tutorials/comparing-workflows/feature-branch-workflow)
