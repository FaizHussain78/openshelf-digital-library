package library;

/** A catalog record. ISBN is the unique key used by the tree indexes. */
public record Book(String isbn, String title, String author, String genre, double rating,
                   String description, int year, String location) {
    public Book(String isbn, String title, String author, String genre, double rating,
                String description, int year) {
        this(isbn, title, author, genre, rating, description, year, "Shelf 1, Row 1");
    }
    public Book {
        isbn = clean(isbn); title = clean(title); author = clean(author); genre = clean(genre);
        description = description == null ? "" : description.strip();
        location = clean(location);
        if (isbn.isBlank() || title.isBlank() || author.isBlank() || genre.isBlank())
            throw new IllegalArgumentException("ISBN, title, author and genre are required");
        if (location.isBlank()) throw new IllegalArgumentException("Shelf location is required");
        if (!Double.isFinite(rating) || rating < 0 || rating > 5) throw new IllegalArgumentException("Rating must be between 0 and 5");
    }
    private static String clean(String s) { return s == null ? "" : s.strip(); }
    Book withLocation(String newLocation) { return new Book(isbn,title,author,genre,rating,description,year,newLocation); }
    public String json() {
        return "{\"isbn\":"+q(isbn)+",\"title\":"+q(title)+",\"author\":"+q(author)+",\"genre\":"+q(genre)+",\"rating\":"+rating+",\"description\":"+q(description)+",\"year\":"+year+",\"location\":"+q(location)+"}";
    }
    static String q(String s) {
        StringBuilder b=new StringBuilder("\"");
        for(char c:s.toCharArray()) switch(c) { case '"' -> b.append("\\\""); case '\\' -> b.append("\\\\"); case '\n' -> b.append("\\n"); case '\r' -> b.append("\\r"); case '\t' -> b.append("\\t"); default -> { if(c<32)b.append(String.format("\\u%04x",(int)c)); else b.append(c); } }
        return b.append('"').toString();
    }
}
