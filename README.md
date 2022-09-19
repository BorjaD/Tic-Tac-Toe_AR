# Tic-tac-toe_AR

Tic tac toe a application to test AR features in a virtual device.

# How it works

When the app runs, available surfaces are detected and the user is prompted to place a tic tac toe board. Tapping on a detected surface brings up a board.

Then, the image of the cross changes to a green background to indicate that it is the turn of the crosses. When the user taps on a cell of the board, there will appear a cross 3D object.

Now it is the turn of the circles, so the image of the circle has now a green background, and the background of the cross becomes transparent. When the user taps on a cell of the board, there will appear a circle 3D object. Next, it's the turn of the crosses, and then the circles.

When two people play, one of them will go with the crosses, and the other one with the circles, and the winner will be the one who manages to align three pieces of their type horizontally, vertically or diagonally.

# Work to be done

- Make the pieces display higher than the board height.

- Reduce the size of the board and pieces.

- Mathematically put a piece in the center of a cell when the user taps that cell.

- Implement the tic tac toe logic: Whoever gets a line of their type of pieces wins.

- Change the name of the deployed application (for now it is EasyLearn).

- Develop tests for the application.

# Testing suggestions

These are some testing suggestions to take into account when testing the AR application. If necessary, additional bugs can be included to enrich the validation process.

- When rotating the camera 90º, we are not be able to see the inserted 3D objects.

- A piece is placed it in the center of the selected cell.

- A piece is placed with the correct rotation.

- A piece is placed on top of the board and not inside it.

- There is not possible to place more than a pice in a cell.

- The board is well placed on a surface, and does not float in the air.

- If it is the turn of one type of object (board, X, O), touching the screen should not bring up an object of another type.

- The item is fully rendered: When tapping the screen to place a 3D object, check that the object is placed (in code and visually in the real world).
	- Visual test: Just before and after tapping the screen to place a 3D object, count the number of "x_color" pixels (if the objects are "x_color"), and see if it has increased. (Problem: 3D objects usually have different shades of colors).