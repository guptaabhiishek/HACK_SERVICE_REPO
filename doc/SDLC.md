Software Development Lifecycle
==============================

Protected Branches
------------------

*	master Represents the code that has been sent to CM for deployment to production
*	staging This branch holds the "Done Done" stories that are ready for releasing
	to production whilst the business UAT's them
*	current The work for the current sprint, stories should be branched from here
*	hotfix A temporary branch created from master when an emergency hotfix is
	required that does not want to go through the normal sdlc

Story branches
--------------

To work on a new feature a "Story Branch" should be created from current. This should be named with a reference to the
story on the project board i.e. JIRA-2048. All the work should be regularly pushed to origin to keep it safe but will
fail the CI processes until it has been completed and signed off.

Diagram
-------

	JIRA-XXXX   +----------==========-------------
	            |         /          \
	JIRA-IIII   +----===========------------------
	            |   /    /      \     \
	Current     +---===================-----------
	            |  /                   \
	Staging     +--=============================--
	            | /                             \
	HotFix      +---------------------------------
	            |/                               \
	Master      +=================================

Development Workflow
--------------------

	 	+-----------------+          +------------+          +-------------+          +--------------------+
		| Start Story     |          | Split up   |          |             |          | Create Unit Tests  |
		| (Branching From +--------->| Story Into +--------->| Design      +--------->| Create Integration |
		| Current)        |          | Tasks      |          | Api/Classes |          | Tests              |
	 	+-----------------+          +------------+          +-------------+          +----------+---------+
		                                                                                         |
                                                                                                 v
	 	+--------------------------------------------------------------------------------------------------+
		|                                                                                                  |
	+-->|                                             CODE                                                 |
	|	|                                                                                                  |
	| 	+------+-------------------------------------------------------------------------------------------+
	|          |
	|          v
	| 	+--------------+          +-------------+          +--------------------+          +--------------+
	|	| Check-In To  |          | Jenkins     |  pass    | Jenkins            |  pass    | Jenkins      |
	|	| Story Branch +--------->| build & run +--------->| run integration    +--------->| Deploy Clean |
	|   |              |          | unit tests  |          | test against mocks |          | Environment  |
	| 	+--------------+          +------+------+          +----------+---------+          +-------+------+
	|                 fail               |                            |                            |
	+------------------------------------+----------------------------+-+---------------------+    |
                                                                        |                     |    v
    	+-----------+          +--------------------+          +--------+--------+          +-+---------------------+
        | Story     |          | Merge Request      |   pass   | Create GitLab   |   pass   | Jenkins               |
        | Completed |<---------+ Accepted And       |<---------+ Merge Request   |<---------+ Run Integration Tests |
        |           |          | Applied To Current |          | For Code Review |          | Against Clean         |
        +-----------+          +--------------------+          +-----------------+          +-----------------------+

