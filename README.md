## eGov Mobile Application (MeGov)

#### Issue Tracking
Report issues via the [MeGov Opensource JIRA][].
#### License
eGov Mobile Application is released under version 3.0 of the [GPL][].

### Steps to run the android application

1) Start Eclipse

2) Import the android source code 
  File->Import->Android-> Existing Android Code into Workspace

3) Import the library (Google play Services Library)
  File->Import->Android-> Existing Android Code into Workspace

4) Right click the android source code and click on ‘Properties’ and then click ‘Android’ and select the android library (google_play_services_lib) using ‘Add’ button

5) Select the project by clicking the Project in the menu then click on ‘Clean’ .In the clean popup, select the project and click on ‘Ok’ to clean the project.

6) Right click the android source code and click Run As->Run configuration->In Android Tab->select the project.

7) In Target tab select ‘Always point to pick a device’ to run the application in the mobile devices
Or
Launch on all compatible devices/AVD'S to run the application in android emulator


[MeGov Opensource JIRA]: http://issues.egovernments.org/browse/MEGOV
[GPL]: http://www.gnu.org/licenses/
