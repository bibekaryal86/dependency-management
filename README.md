# dependency-management

* Things to add:
    * Add Pagination to MongoRepoController GET lookup
    * Add More options to tasks
      * kill tasks
        * this is initialized, but not implemented
      * skip tasks
    * Tests
    * Documentation

* System Requirements:
    * Java (https://adoptium.net/temurin/releases/)
    * git (https://git-scm.com/downloads)
    * github cli (https://cli.github.com/)

* App Arguments
    * Required
        * repo_home: Hard disk location where repos are cloned
        * mongo_user: User name of mongo database where plugins and dependencies are stored
        * mongo_pwd: Password of the mongo database
    * Optional
        * send_email: Flag to send email of current log file at the end of scheduled update
        * mj_public: Public API Key of MailJet Email Service
            * Required if send_email is `true`
        * mj_private: Private API Key of MailJet Email Service
            * Required if send_email is `true`
        * mj_email: Email address that the email will be sent from
            * Required if send_email is `true`

* Example:
    * java -jar -Drepo_home=/home/pi/zava/projects -Ddb_host=some-host -Ddb_name=some-name -Ddb_username=some_user -Ddb_password=some_password dep-mgmt.jar


options to view which tasks running
    needs enhancement to view from actual task queues
