<h1 align="center">Chess System</h1>
The project was developed in <b>Java</b> with the objective of testing my skills in developing larger projects.
<br>
So far, it has been implemented using only the object-oriented programming (OOP) concepts of the language itself, without any frameworks like <b>Spring Boot</b> or <b>JavaFX</b> for a GUI. However, the plan is to add one in the future.
<br>
The game works normally and includes special moves like Castling, En Passant, and Promotion.

## Installation and How to Use
Prerequisites: You need to have <a href="https://www.azul.com/downloads/?package=jdk#zulu">Java</a> installed on your machine and ensure the compiler is working correctly.

1. Clone the repository.
2. Open the terminal and navigate to the folder where the repository is.
3. If your system supports Makefiles, simply run <code>make</code> to compile and execute the program.
4. If you don't have Make installed, follow these manual steps:
```sh
javac -d bin -sourcepath src src/application/Program.java
java -cp bin application.Program
```
