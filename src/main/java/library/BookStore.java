package library;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/** Small Java-owned persistent store. Each row is tab-separated Base64 UTF-8. */
final class BookStore {
    private final Path file = Path.of("data", "books.tsv");
    private final Path sampleVersion = Path.of("data", "catalog-sample-v2.done");
    synchronized List<Book> load() throws Exception {
        Files.createDirectories(file.getParent());
        List<Book> out=new ArrayList<>();
        boolean migrate = false;
        if (!Files.exists(file)) { out.addAll(sample()); save(out); }
        else {
            for(String line:Files.readAllLines(file, StandardCharsets.UTF_8)) {
                if(line.isBlank())continue;
                String[] p=line.split("\t",-1); if(p.length!=7&&p.length!=8)continue;
                String location=p.length==8?dec(p[7]):locationFor(out.size());
                if(p.length==7)migrate=true;
                out.add(new Book(dec(p[0]),dec(p[1]),dec(p[2]),dec(p[3]),Double.parseDouble(p[4]),dec(p[5]),Integer.parseInt(p[6]),location));
            }
        }
        // Add the expanded demonstration catalog once to projects created with the earlier 10-book sample.
        if (!Files.exists(sampleVersion)) {
            Set<String> existing=new HashSet<>();for(Book b:out)existing.add(b.isbn());
            for(Book b:sample())if(existing.add(b.isbn()))out.add(b.withLocation(locationFor(out.size())));
            save(out);Files.writeString(sampleVersion,"v2",StandardCharsets.UTF_8,StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING);
        } else if(migrate) save(out);
        return out;
    }
    synchronized void save(Collection<Book> books) throws Exception {
        Files.createDirectories(file.getParent()); List<String> rows=new ArrayList<>();
        for(Book b:books) rows.add(String.join("\t",enc(b.isbn()),enc(b.title()),enc(b.author()),enc(b.genre()),Double.toString(b.rating()),enc(b.description()),Integer.toString(b.year()),enc(b.location())));
        Path tmp=file.resolveSibling("books.tsv.tmp"); Files.write(tmp,rows,StandardCharsets.UTF_8,StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING);
        try { Files.move(tmp,file,StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.ATOMIC_MOVE); } catch(AtomicMoveNotSupportedException e) { Files.move(tmp,file,StandardCopyOption.REPLACE_EXISTING); }
    }
    private String enc(String s){return Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8));}
    private String dec(String s){return new String(Base64.getDecoder().decode(s),StandardCharsets.UTF_8);}
    private static String locationFor(int index){return "Shelf "+(index/8+1)+", Row "+(index%8+1);}
    private List<Book> sample(){List<Book> books=new ArrayList<>(List.of(
        new Book("978-0132350884","Clean Code","Robert C. Martin","Software",4.7,"A practical guide to writing readable, maintainable code.",2008),
        new Book("978-0262033848","Introduction to Algorithms","Thomas H. Cormen","Computer Science",4.6,"Comprehensive algorithms textbook with rigorous analysis.",2009),
        new Book("978-0201633610","Design Patterns","Erich Gamma","Software",4.5,"Reusable object-oriented software design patterns.",1994),
        new Book("978-0131103627","The C Programming Language","Brian W. Kernighan","Programming",4.4,"Classic introduction to C programming.",1988),
        new Book("978-1491950357","Designing Data-Intensive Applications","Martin Kleppmann","Computer Science",4.8,"Architecture for reliable, scalable data systems.",2017),
        new Book("978-0596007126","Head First Design Patterns","Eric Freeman","Software",4.3,"Visual introduction to common software patterns.",2004),
        new Book("978-0321125217","Domain-Driven Design","Eric Evans","Software",4.2,"Model complex software around the business domain.",2003),
        new Book("978-0134685991","Effective Java","Joshua Bloch","Programming",4.8,"Best practices for the Java programming language.",2017),
        new Book("978-1617294945","Grokking Algorithms","Aditya Bhargava","Computer Science",4.6,"Illustrated introduction to algorithms.",2016),
        new Book("978-0262046305","Introduction to Machine Learning","Ethem Alpaydin","AI",4.1,"Foundations and methods for machine learning.",2020),
        new Book("978-0441172719","Dune","Frank Herbert","Science Fiction",4.6,"A young nobleman faces politics, survival, and prophecy on a desert planet.",1965),
        new Book("978-0547928227","The Hobbit","J. R. R. Tolkien","Fantasy",4.7,"Bilbo Baggins joins a quest to reclaim a mountain home and its treasure.",1937),
        new Book("978-0141034355","The Adventures of Sherlock Holmes","Arthur Conan Doyle","Mystery",4.5,"A collection of cases solved through observation and logical deduction.",1892),
        new Book("978-0141439518","Pride and Prejudice","Jane Austen","Classic Fiction",4.6,"A witty story about family, first impressions, and marriage in England.",1813),
        new Book("978-0062316097","Sapiens","Yuval Noah Harari","History",4.4,"A broad account of the development of humans and societies.",2011),
        new Book("978-0374533557","Thinking, Fast and Slow","Daniel Kahneman","Psychology",4.3,"An introduction to fast intuition and slower deliberate thinking.",2011),
        new Book("978-0735211292","Atomic Habits","James Clear","Self-Help",4.5,"Practical ideas for building useful habits through small changes.",2018),
        new Book("978-0857197689","The Psychology of Money","Morgan Housel","Finance",4.4,"Short stories about behavior, risk, and decisions involving money.",2020),
        new Book("978-0399590504","Educated","Tara Westover","Biography",4.5,"A memoir about education, family, and finding a path beyond a strict upbringing.",2018),
        new Book("978-0553380163","A Brief History of Time","Stephen Hawking","Science",4.3,"An accessible tour of major questions about the universe and time.",1988),
        new Book("978-0062315007","The Alchemist","Paulo Coelho","Literary Fiction",4.2,"A shepherd travels in search of a treasure and a deeper sense of purpose.",1988),
        new Book("978-0590353427","Harry Potter and the Sorcerer's Stone","J. K. Rowling","Children's Fantasy",4.7,"A young boy discovers a hidden school of magic and a new world.",1997),
        new Book("978-0156012195","The Little Prince","Antoine de Saint-Exupery","Children's Classic",4.6,"A pilot meets a young traveler whose questions reveal adult habits.",1943),
        new Book("978-0743273565","The Great Gatsby","F. Scott Fitzgerald","Classic Fiction",4.2,"A narrator observes wealth, longing, and reinvention in 1920s New York.",1925),
        new Book("978-0143130727","Ikigai","Hector Garcia","Wellness",4.1,"Reflections on purpose, daily routines, and long-lived communities.",2016),
        new Book("978-0143127741","The Book Thief","Markus Zusak","Historical Fiction",4.5,"A girl in wartime Germany finds comfort and courage in books.",2005)
    ));for(int i=0;i<books.size();i++)books.set(i,books.get(i).withLocation(locationFor(i)));return books;}
}
