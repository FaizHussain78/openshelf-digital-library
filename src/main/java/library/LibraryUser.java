package library;

import java.util.Set;

/** Locally registered library account and its recommendation preferences. */
record LibraryUser(String name, String email, String salt, String passwordHash, String role,
                   Set<String> genres, Set<String> authors) {
    LibraryUser {
        genres = Set.copyOf(genres);
        authors = Set.copyOf(authors);
    }
    String publicJson() {
        return "{\"name\":" + Book.q(name) + ",\"email\":" + Book.q(email)
                + ",\"role\":" + Book.q(role) + ",\"genres\":" + stringArray(genres) + ",\"authors\":" + stringArray(authors) + "}";
    }
    private static String stringArray(Set<String> values) {
        return "[" + String.join(",", values.stream().sorted().map(Book::q).toList()) + "]";
    }
}
