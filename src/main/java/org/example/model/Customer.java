package org.example.model;

public class Customer {
    private final int id;
    private final String firstName;
    private final String lastName;
    private final String email;

    public Customer(int id, String firstName, String lastName, String email) {
        if (id <= 0) {
            throw new IllegalArgumentException("L'id doit être positif");
        }
        this.id = id;

        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("Le prénom ne peut pas être vide");
        }
        this.firstName = firstName;

        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Le nom ne peut pas être vide");
        }
        this.lastName = lastName;

        if (email == null || !email.contains("@") || !email.contains(".")) {
            throw new IllegalArgumentException("L'email doit contenir : @ et . ");
        }
        this.email = email;
    }
    // Getters pour un client : Je suppose qu'un client ne change pas de Prénom, Nom, ID ou addresse mail donc pas besoin de setters ici
    public int getId() {return this.id;}
    public String getFirstName() {return this.firstName;}
    public String getLastName() {return this.lastName;}
    public String getEmail() {return this.email;}

    @Override
    public String toString() {
        return "Customer{id=" + id + ", name=" + getFullName() + ", email=" + email + "}";
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
