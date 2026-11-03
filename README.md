# OpenShelf — Digital Library

OpenShelf is a full-stack Java digital library project for searching books and recommending titles based on a reader’s choices. It includes a browser-based interface, a Java HTTP server, and local file storage.

## Features

- Search the catalog by ISBN, title, author, and genre; filter by genre and read book descriptions.
- Create Reader or Admin accounts and sign in. Readers can browse and borrow books; Admins can add or remove books and process returns.
- Get recommendations based on favorite genres and authors, with book ratings used in the ranking.
- Borrow an available book for 14 days, see loan history, and view each book’s shelf and row location.
- Explore book connections and view catalog and index statistics.
- View Huffman coding analysis for a book description.

## Technology

- **Backend:** Java 17+ and the built-in `HttpServer` API
- **Frontend:** HTML, CSS, and JavaScript
- **Build setup:** Maven project, with a Windows `run.bat` option
- **Storage:** Local TSV files; no external database or API key is required

## Run the project

### IntelliJ IDEA

1. Clone or download this repository.
2. Open the project folder in IntelliJ IDEA and load it as a Maven project.
3. Set the project SDK to Java 17 or newer.
4. Run the **OpenShelf** configuration. If it is not listed, open `src/main/java/library/LibraryServer.java` and run `main`.
5. Visit <http://localhost:8080> in a browser.

### Windows

Install a JDK 17 or newer, then double-click `run.bat` in the project folder. When the server starts, visit <http://localhost:8080>. Stop the server with **Ctrl+C** in its console window.

On first launch, OpenShelf creates a sample catalog and a bootstrap Admin account. The Admin email and one-time password are printed in the server console. Keep that password for signing in. You can also create Reader or Admin accounts from the sign-in panel.

## Data storage

OpenShelf saves data in the project’s `data` folder when it runs:

| File | Data |
| --- | --- |
| `books.tsv` | Book records, descriptions, and shelf locations |
| `users.tsv` | Account details and reading preferences |
| `loans.tsv` | Borrowing and return records |

Passwords are saved as salted PBKDF2 hashes. Local data files are excluded from this GitHub repository, so each new local run creates its own data files.

## DSA concepts used

- **Binary Search Tree and AVL tree:** index books by ISBN.
- **Heap / priority queue:** select the top recommendations efficiently.
- **Graph with adjacency lists:** represent relationships between books.
- **BFS and DFS:** traverse book connections.
- **Dijkstra’s algorithm:** find short weighted paths between books.
- **Prim’s algorithm:** build a minimum spanning forest of book connections.
- **Huffman coding:** analyze the encoded bit length of book descriptions.

## How recommendations work

The recommender starts with reader ratings, then gives extra weight to books matching the reader’s favorite genres and authors. It returns the highest-ranked results requested by the reader.

## Project structure

```text
src/main/java/library/       Java backend and DSA implementations
src/main/resources/           Browser interface (HTML, CSS, JavaScript)
.run/OpenShelf.run.xml        IntelliJ run configuration
pom.xml                       Maven project configuration
run.bat                       Windows build-and-run script
```

## Notes

OpenShelf is an educational local demo. It listens on the local computer and stores data in local files; it is not configured as a production or internet-facing service. Book records and ratings are sample data for demonstration. Each title represents one available copy.

