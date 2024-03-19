# Freelancing Platform Database Management System

## Introduction
This repository contains the implementation of a Freelancing Platform Database System. It includes the following components:

1. ER modeling of the database.
2. Translation of the ER model into a relational model.
3. Implementation of the database using SQLite.
4. Development of a corresponding RESTful Web Service.

## Application Scenario
The objective of this practical exercise is to develop a system for managing a database of a Freelancing Platform. The explicit task for this assignment is outlined below.

### Description

The database system should represent the following scenarios:

1. **User Management:**
   - Each user has a unique email address and a password.

2. **Project Management:**
   - Every project has a unique project name, a preliminary project deadline, and a project leader.
   - Every project leader is a user who leads exactly one project and receives a fixed salary.
   - Projects consist of multiple tasks, with each task belonging to exactly one project.
   - Tasks have their own deadline, description, status, and priority.
   
3. **Customer Management:**
   - Customers can request projects and leave reviews for projects.
   - Each customer is a user with a unique phone number.
   - Projects are requested by customers and assigned to exactly one customer.

4. **Specialist Management:**
   - Specialists are users who work on projects.
   - Specialists can be developers or designers.
   - Developers have programming language proficiency levels (beginner, experienced, expert) and unique identifiers.
   - Designers have specifications (e.g., digital or graphic) and additional competencies.

### ER Model
The ER model depicts the relationships between entities in the database.

### Relational Model
The relational model translates the ER model into a set of relational tables.

### SQLite Implementation
The database is implemented using SQLite, providing a lightweight and portable solution.

### RESTful Web Service
A RESTful Web Service is developed to interact with the database, enabling CRUD operations and other functionalities.

For more details on each component and the implementation process, refer to the respective directories and files in this repository.