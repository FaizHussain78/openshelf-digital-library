package library;

/** One saved borrowing record. A blank returnedOn value means the book is still on loan. */
record Loan(String id, String isbn, String title, String userEmail, String userName,
            String borrowedOn, String dueOn, String returnedOn) {
    boolean active() { return returnedOn == null || returnedOn.isBlank(); }
    Loan returned(String date) {
        if (!active()) throw new IllegalArgumentException("This book has already been returned");
        return new Loan(id, isbn, title, userEmail, userName, borrowedOn, dueOn, date);
    }
    String json() {
        return "{\"id\":"+Book.q(id)+",\"isbn\":"+Book.q(isbn)+",\"title\":"+Book.q(title)
                +",\"userEmail\":"+Book.q(userEmail)+",\"userName\":"+Book.q(userName)
                +",\"borrowedOn\":"+Book.q(borrowedOn)+",\"dueOn\":"+Book.q(dueOn)
                +",\"returnedOn\":"+(active()?"null":Book.q(returnedOn))
                +",\"status\":"+Book.q(active()?"Borrowed":"Returned")+"}";
    }
}
