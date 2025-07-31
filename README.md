# **ingredient-generator**
A Java-based project to generate a list of ingredients for at most 3 different recipes based on user preferences. This repository is for the course project of CSC207 (Software Design) taken at the University of Toronto.

Members:
- Aryaman Bansal
- Ellen Liu
- Tommy Shen
- Caroline Xiao
- Miki Wakabayashi Zheng

## **Repository Structure**

Below is the structure of the repository, which follows the Clean Architecture principles. The project is organized into different folders, each serving a specific purpose:

```bash
├── src/main/java/

│   ├── app/
│   │   ├── Main.java

│   ├── data_access/
│   │   ├── FileUserRepository.java
│   │   ├── InMemoryUserRepository.java

│   ├── entity/
│   │   ├── Recipe.java
│   │   ├── RegularUser.java

│   ├── interface_adapter/
│   │   ├── controller
│   │   │   ├── LoginController.java
│   │   │   ├── SignupController.java
│   │   ├── presenter
│   │   │   ├── LoginPresenter.java
│   │   │   ├── SignupPresenter.java

│   ├── org/example/
│   │   ├── MealPreferences.java
│   │   ├── MongoConnectionDemo.java

│   ├── use_case/
│   │   ├── gateway/
│   │   │   ├── RecipeGateway.java
│   │   │   ├── UserRepository.java
│   │   ├── login/
│   │   │   ├── LoginInteractor.java
│   │   │   ├── LoginOutputBoundary.java
│   │   │   ├── LoginRequestModel.java
│   │   │   ├── LoginResponseModel.java
│   │   │   ├── LoginUseCase.java
│   │   ├── signup/
│   │   │   ├── SignupInteractor.java
│   │   │   ├── SignupOutputBoundary.java
│   │   │   ├── SignupRequestModel.java
│   │   │   ├── SignupResponseModel.java
│   │   │   ├── SignupUseCase.java

│   ├── view/
│   │   ├── Demo2.java
│   │   ├── EasterEgg.java
│   │   ├── HistoryFrame.java
│   │   ├── LoginFrame.java
│   │   ├── LoginView.java
│   │   ├── SignupFrame.java
│   │   ├── SignupView.java
│   │   ├── TopRecipesFrame.java
│   │   ├── UserProfileFrame.java
```


## **Introduction**

This project is designed to help users find recipes based on their meal preferences. It allows users to log in, sign up, and view their meal preferences. The application generates a list of ingredients for up to three different recipes based on the user's preferences.
