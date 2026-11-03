# OpenShelf — Digital Library

A small full-stack Java project for the DSA-II PBL topic **Digital Library Search and Book Recommendation**. It uses a browser frontend, a Java HTTP backend, and a catalog stored by Java in `data/books.tsv`.

## Run in IntelliJ IDEA

1. Extract the ZIP and open its project folder in IntelliJ IDEA.
2. Choose **Open as Project** and allow IntelliJ to load the Maven project.
3. Set the Project SDK to JDK 17 or newer if IntelliJ asks.
4. Open the **OpenShelf** run configuration and click Run. If that configuration does not appear, open `src/main/java/library/LibraryServer.java` and run `main`.
5. Open <http://localhost:8080>.

The checked-in IntelliJ run configuration includes the required `jdk.httpserver` module argument. Frontend files are loaded from the Java classpath, so the app also works when IntelliJ launches compiled classes from its output directory.

## Run on Windows

1. Install a JDK 17 or newer and ensure `java` and `javac` are on PATH.
2. Double-click `run.bat` (or run it from this folder).
3. Open <http://localhost:8080>.

On first run, the app creates a sample catalog with books from a range of genres. If you already have the earlier sample catalog, new sample books are added once without replacing your books. Existing books are assigned shelf locations during the data upgrade. The app also creates an admin account and prints its email and one-time generated password in the IntelliJ Run console. Save that password. Users can create Reader or Admin accounts from the sign-in panel. Stop the server with Ctrl+C in its terminal window.

## Features

- Search by ISBN, title, author, or genre; filter the catalog by genre; open any book card to read its description.
- Register or sign in from the profile control; accounts and saved reading preferences persist locally.
- Sign in using the Reader or Admin option. Readers can browse, search, read descriptions, get recommendations, and explore connections. Only Admin accounts can add or remove catalog books.
- Choose favorite genres and authors to personalize top-K recommendations, with reader ratings as the ranking baseline.
- Add and remove catalog entries. ISBN is unique and serves as the tree key.
- Save a shelf and row location for every book. The catalog and book details show that location to readers and admins.
- Borrow an available copy for 14 days and view current and past loans. Readers see their own loans; only admins can process returns, and admins can see all loans.
- BST and AVL ISBN indexes, with their heights shown in the dashboard.
- Bounded max-priority heap for top-rated recommendations (O(n log k)).
- Description Huffman-code analysis from each book card, with original and encoded bit counts plus the generated character-code table.
- Weighted book relationship graph with BFS, DFS, Dijkstra shortest paths, and Prim minimum spanning forest.
- Responsive frontend served by the same Java process; no Node/npm/database service required.

## API

| Method and route | Purpose |
| --- | --- |
| `GET /api/books?q=...` | Catalog search |
| `POST /api/books` | Add a book; JSON fields include `isbn`, `title`, `author`, `genre`, `rating`, `description`, `year`, and `location` |
| `DELETE /api/books/{isbn}` | Remove a book if it has no active loan |
| `GET /api/loans` | List the signed-in reader's loans, or all loans for an admin |
| `POST /api/loans` | Borrow an available book by ISBN; due date is 14 days after checkout |
| `POST /api/loans/{id}/return` | Admin-only action to mark a loan returned |
| `GET /api/recommendations?k=5&genre=...` | Personalized top-rated books for a signed-in user; rating-only results remain available without a session |
| `POST /api/auth/register` / `POST /api/auth/login` | Create a Reader or Admin account, or sign in; the backend issues a local session cookie |
| `GET /api/auth/me` / `POST /api/auth/logout` | Read the current account or sign out |
| `PUT /api/preferences` | Save newline-separated `genres` and `authors` choices for the signed-in account |
| `GET /api/graph?isbn=...` | BFS, DFS, Dijkstra, Prim results |
| `GET /api/stats` | Catalog and index statistics |
| `GET /api/compress?isbn=...` | Huffman description bit-count estimate and code table |

## DSA mapping

The report and syllabus slides identify Unit I (Trees) and Unit II (Graphs) as the current project scope. The report names BST, AVL rotations, binary heap, Huffman coding, adjacency-list graphs, BFS/DFS, Dijkstra, and Prim/MST. These concepts are implemented in the Java backend. Later report goals (edit-distance search, dynamic programming, knapsack, backtracking and branch-and-bound) are not presented as completed features.

## Notes

This teaching project uses local files instead of a production database. Catalog data and shelf locations are stored in `data/books.tsv`; accounts and saved preferences are stored in `data/users.tsv`; borrowing history is stored in `data/loans.tsv`. Existing seven-column catalog files are upgraded automatically and assigned sample shelf locations. Account passwords are stored as salted PBKDF2 hashes. The server listens only on the local computer. The included sample titles and ratings are demonstration data. Each catalog title represents one copy; account registration is intended for this local demonstration, not an internet-facing service. Graph edge weights are generated from shared authors (1) and shared genres (2), matching the report's manually assigned relationship-weight idea.

