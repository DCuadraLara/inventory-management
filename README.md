# inventory-management

Desktop application developed in **Java Swing** for managing household appliance information through a structured graphical interface.

The project focuses on form validation, user interaction, clean code organization and object-oriented programming.

## Features

- Create household appliance records.
- Validate user input before saving data.
- Highlight invalid fields visually.
- Automatically focus the first incorrect field.
- Confirmation dialogs for important actions.
- Detect whether the user has modified the form before closing it.
- Manage different article properties such as:
  - Code
  - Name
  - Brand
  - Product range
  - Appliance type
  - Energy efficiency
  - Unit price
  - Stock
  - Entry date
  - Warranty
  - Product status
  - Additional services
  - Description
- Form reset and error clearing.
- Custom desktop interface built with Swing components.

## Technologies

- Java
- Java Swing
- Java AWT
- Object-Oriented Programming
- NetBeans
- Apache Ant

## Project Structure

```text
src/
├── model/
│   └── Articulo.java
│
└── ui/
    ├── PantallaPrincipal.java
    └── DialogoAltaArticulo.java
```

The application separates the data model from the graphical interface to keep the project easier to maintain and extend.

## Validation

The form includes several validation rules to ensure that the information entered by the user is correct.

Some examples include:

- Required fields cannot be empty.
- Text fields have maximum character limits.
- Numeric fields are validated before processing.
- Combo boxes must contain a valid selection.
- Dates cannot be earlier than the current date.
- Incorrect fields are visually highlighted.
- The first invalid component automatically receives focus.

This provides clear feedback to the user and improves the overall user experience.

## Swing Components

The interface makes use of multiple Swing components, including:

- `JFrame`
- `JDialog`
- `JTextField`
- `JTextArea`
- `JComboBox`
- `JCheckBox`
- `JRadioButton`
- `ButtonGroup`
- `JSpinner`
- `JOptionPane`
- `JLabel`
- `JPanel`

## What I Learned

This project helped me improve my understanding of:

- Java desktop application development.
- Event-driven programming.
- Swing interfaces.
- Form validation.
- User input management.
- Object-oriented design.
- Separation between UI and data models.
- Improving user experience through visual feedback and confirmation dialogs.

## Future Improvements

Some possible improvements for future versions:

- Persist article data using JSON or a SQL database.
- Add an article listing screen.
- Allow editing and deleting existing articles.
- Implement search and filtering.
- Improve the UI design.
- Separate business logic into dedicated service classes.
- Add automated tests.

## Screenshots

### Main Window

![Main Window](docs/main-window.png)

### New Article Form

![New Article Form](docs/new-article-form.png)

### Form Validation

![Form Validation](docs/form-validation.png)

## Author

**David Cuadra Lara**

DAM Student | Java Backend Developer in training

Currently focused on improving my skills in **Java, SQL, Git, Spring Boot and backend development**.
