AI FItness Tracker is an application developed by me as a project for University.

This is an Android application. I used Kotlin as the programming language for the functionalities and XML files for the UI.

The application has quite a few features and functionalities:
- Meals tracking
   - With kcalories, proteins, carbohidrates, fats, fibres, etc. 
- Meals receipes and plan generating
- Profile customisation
- Weight tracking
- Workouts generating
- Sign up and Log in

The databses are being held using Firebase:
- Autehntication database
- Real Time databases

The application features many AI modules
- 1 Agent: Specialised as being a nutritionist and workouts proffesionist. The user can CHAT with this agent and ask him everything. The Agent knows details about the user, from name and age, to what the user plans
to do with his weight and muscles, the user's opening to workouts, the user's alimentar prefferences and many more such details that the user is introducing in the application after creating the account.
- 1 Agent: Specialised in workouts and such plans related to working out, exercising and doing sports. The user can not chat with this agent, but can see the workouts that he is generating fo each week,
and can require the workout per the current week to be re-generated, if the user does not like it.
- 1 Computer Vision Model, used for image recognition. This AI feature scans an image and identifies what aliments are in the image. Fot this part, a LLM that is capable to work with images (Bitmaps) is being used. As
a result, the aliments that have been identified, and maybe modified by the user, can be fed to another LLM, whose job is to generate receipes with these aliments.

The applciation has some main activities and fragments:
- First time installed, when launching the application, an activity with short descriptions of the application, and how the application can help the user will be displayed, and after the user goes through all these screens
it is closing and redirecting the user to the Sign up/ Log in actiivity.
- The Sign up and Log in activities' functionalities are being implemented with Firebase, the Signup, the Login and the Forgot my password features.
- If someone is Signing up, and creating a new account, the first next thing that happens is an ACtiviity used for inforamtion about the user gathering. THese are 10 - 12 questions to whom the user must respond,
information that will help us understand what the user desires to do. This information is being held in the database, and out Agents know these things when the user is interating with them.
- After this, and also, after Logging in, the user gets to the main activity.
- The main actiivty consists of 3 important fragments, the home fragment, for meal per days tracking and nutrients recording and tracking, the AI Chat fragment, which is a chat with one of the agents that the applciation
comes with and the Profile fragment, where the user can see a graph with the recorded by him weight from the past days, details about him taht he had recorded about himself after creating the acoount and the Log out button,
feature that is also implemented with firebase.
- Another Activity is the Melas activity, where we load 3 meals for Breakfast, 3 meals for Lunch and 3 meals for Dinner dayly, randomly, from our database, where we keep 10 such receipes for each meal of the day.
Each of these shown meals have details about preparing, cooking, nutrients and steps for cooking and prepearing it. From here, we can go to the part where we can take a picture with some ingredients, or just write a list
of them and fed them to a LLM for receipes generating, which we can save and use again everytime we want to.
- We have the Workouts activity, where a working out plan is being geenrated for each week, by a LLM. The user have the option to read and see each workout for every day, or can regenerate the workouts for the whole week,
thing that is also being done by the LLM.


Overall, the application is great for use in general and it works as is, with great potential to being extended.
