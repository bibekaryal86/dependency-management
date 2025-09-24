# dependency-management

* Things to add:
    * Add Pagination to MongoRepoController GET lookup
    * Add More options to tasks
      * kill tasks
        * work has started, but not implemented
      * skip tasks
        * programmable so that it can be updated after adding
    * Tests
    * Improved Documentation

* System Requirements:
    * Java (https://adoptium.net/temurin/releases/)
    * git (https://git-scm.com/downloads)
    * NPM (https://nodejs.org/en/download)

* Technologies Used:
  * Java
  * Netty
  * MongoDb
  * GitHub

* App Arguments
  * Required
    * self_username: user name for basic auth
    * self_password: password for basic auth
    * db_host: mongodb connection url
    * db_name: mongodb database name
    * db_username: mongodb login username
    * db_password: mongodb login password
    * gh_owner: owner of github repos
    * gh_token: github Public Access Token for github rest apis
    * send_email: flag to send email with process summary and application logs
    * mg: public api key of mailjet email service
    * mj_private: private api Key of mailjet email service
    * mj_email: email address that the email will be sent from
