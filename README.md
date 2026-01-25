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

LOG IN
![login](https://github.com/user-attachments/assets/3aea4bb3-b50a-4917-8855-8847236af9d8)

SIGN UP
![signup](https://github.com/user-attachments/assets/2cc56b7c-dae4-4a77-a079-5cbc25f042f6)

HOME
![home](https://github.com/user-attachments/assets/3c296478-0e3a-4369-b7b8-15c8550a902c)

MEALS
![meals](https://github.com/user-attachments/assets/fb9d9d80-3898-46d9-9892-325561a67b70)
MEALS
![meals v2](https://github.com/user-attachments/assets/8b8c613b-b9ef-4a45-960f-5e9833f523e6)
MEALS
![meals v3](https://github.com/user-attachments/assets/fa88f1d3-15d2-46e5-ada2-0e8a7e852cdd)
MEALS
![meals v4](https://github.com/user-attachments/assets/f3d9236f-30ec-46e4-82b9-7ae287c81504)
MEALS
![meals v5](https://github.com/user-attachments/assets/64536e64-f0ae-449a-9356-b491ea1aff1c)
MEALS
![meals v6](https://github.com/user-attachments/assets/3c21b3b5-d341-46c0-a2d2-3122e9f9ade3)

PROFILE
![profile v2](https://github.com/user-attachments/assets/273f4861-f44f-424e-a551-6bae6370d7a5)
PROFILE
![profile](https://github.com/user-attachments/assets/f06ea3d4-f5dc-4d7b-947f-af1b34e34eea)

AI COACH
![ai coach](https://github.com/user-attachments/assets/5975be24-d4ae-4764-bdad-d379c4c7125d)

WORKOUTS
![workouts v0](https://github.com/user-attachments/assets/23e8be56-7b43-4bde-bb79-339661b53d72)
WORKOUTS
![workouts](https://github.com/user-attachments/assets/39f83be1-3c61-4375-ba1f-0eb36daa32e7)
WORKOUTS
![workouts v3](https://github.com/user-attachments/assets/fe4cf1f0-c37c-42e5-b961-80bba13a2e36)
