Forking
=======

To start building a new muService you need to fork the template project. 
gives you an choice of where to fork the project. If your a member of any team namespaces this would be the best place, 
otherwise fork it into your personal namespace as it can be moved later.

* [Fork](https://cm-git.concurtech.net/101040/muServiceTemplate/fork/new) the template into your team namespace if exists or into you personal one. You can move the fork later if needed.
* Open up the settings page of the fork, the link is in the left-hand sidebar.
* Go to the rename "Rename repository" section half way down this page and name the project name and path to something sensible for your service.
* Edit the "Project settings" at the top of this page to sensible for your service. If your using jira you can disable the Issues feature and the wiki if you want your documentation in the project (makes the documentation "code").
* Follow the [Setup GitLab for CI](CI.md) instructions and add this to the Deploy keys
* If using jira enable the integration in the Services tab
* Setup some protected branches that model your SDLC. A good start can be found [here](SDLC.md)

You can now clone the project onto your dev environment. 
The project is runnable `./gradlew clean service:bootRun` and the [swagger test page](http://localhost:8080/index.html) will show the example resource. 
You will need to rename the swbservice namespaces to your project name and start building out your [api](API.md) and [service logic](SERVICE.md).

The next step is setting up [jenkins](CI.md) and [puppet](DEPLOY.md).