
# CS6300 Summer 2023, Assignment 5 - Design Description

## When the app is started, the user is presented with the main menu, which allows the user to 
(1) enter or edit current job details, (2) enter job offers, (3) adjust the comparison settings, or (4) compare job offers (disabled if no job offers were entered yet1).
- To achieve this, the MainMenu Class will serve as the application's entry point.
Upon starting the app, the ShowMenu() method will display the main menu,
providing options for the user to (1) enter current job details, (2) enter job offers,
(3) adjust comparison settings, and (4) compare job offers.

## When choosing to enter current job details, a user will:
a. Be shown a user interface to enter (if it is the first time) or edit all the details of
their current job, which consists of:
i. Title
ii. Company
iii. Location (entered as city and state)
iv. Cost of living in the location (expressed as an index)
v. Yearly salary
vi. Yearly bonus
vii. Training and Development Fund
viii. Leave Time
ix. Telework Days per Week
- When the user selects the option to enter current job details, the GetJobDetail() method will
verify if a current job is already stored within the Job class. The Job class includes a Boolean
attribute "isCurrentJob" to distinguish the current job from other job offers. If no current job
record exists, the user will be prompted to enter the details (e.g., title, salary) of their current job
using the AddJobDetail() and SaveJobDetail() methods.

b. Be able to either save the job details or cancel and exit without saving, returning
in both cases to the main menu.  
- If the CurrentJob record exists, the detail will be displayed, allowing the user either to edit them
by using the EditJobDetail() and SaveJobDetail() methods or return to the main menu by calling
the ShowMenu() method. For the current job, the attribute isCurrentJob will have a default value
of “true”.

## When choosing to enter job offers, a user will:
a. Be shown a user interface to enter all the details of the offer, which are the same
ones listed above for the current job.  
b. Be able to either save the job offer details or cancel.
- If a CurrentJob record is found, its details will be displayed, giving the user the option to edit
them using the EditJobDetail() and SaveJobDetail() methods or return to the main menu by
calling the ShowMenu() method. The isCurrentJob attribute for the current job will have a default
value of "true".

c. Be able to (1) enter another offer, (2) return to the main menu, or (3) compare the
offer (if they saved it) with the current job details (if present).  
- After entering and saving the job offer details, the user will have three options: (1) continue
entering additional job offers using the same method, (2) return to the main menu by calling the
ShowMenu() method, or (3) compare the newly entered offer with the current job details by
invoking the CompareJob() method. The GetJobDetails() method will display the record where
the isCurrentJob attribute is set to true.
