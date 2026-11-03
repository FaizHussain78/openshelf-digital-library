package library;
import java.util.*;

/** Bounded min-heap keeps only the best K matching books: O(n log k). */
final class RecommendationEngine {
    static List<Book> topK(Collection<Book> books,int k,String genre){
        PriorityQueue<Book> heap=new PriorityQueue<>(Comparator.comparingDouble(Book::rating).thenComparing(Book::title));
        for(Book b:books){if(genre!=null&&!genre.isBlank()&&!b.genre().equalsIgnoreCase(genre))continue;heap.offer(b);if(heap.size()>k)heap.poll();}
        List<Book> out=new ArrayList<>(heap);out.sort(Comparator.comparingDouble(Book::rating).reversed().thenComparing(Book::title));return out;
    }

    private record Ranked(Book book, double score, List<String> reasons) {}

    /** Personalization adds explicit genre/author match bonuses to the rating score. */
    static List<Map<String,Object>> personalized(Collection<Book> books, int k, LibraryUser user) {
        Comparator<Ranked> rank = Comparator.comparingDouble(Ranked::score)
                .thenComparingDouble(r -> r.book().rating()).thenComparing(r -> r.book().title());
        PriorityQueue<Ranked> heap = new PriorityQueue<>(rank);
        for (Book b : books) {
            List<String> reasons = new ArrayList<>();
            double score = b.rating();
            if (containsIgnoreCase(user.genres(), b.genre())) { score += 2.0; reasons.add("Your genre: " + b.genre()); }
            if (containsIgnoreCase(user.authors(), b.author())) { score += 2.5; reasons.add("Your author: " + b.author()); }
            if (reasons.isEmpty()) reasons.add("Reader rating");
            heap.offer(new Ranked(b, score, reasons));
            if (heap.size() > k) heap.poll();
        }
        List<Ranked> ranked = new ArrayList<>(heap);
        ranked.sort(rank.reversed());
        List<Map<String,Object>> result = new ArrayList<>();
        for (Ranked r : ranked) {
            Book b = r.book(); Map<String,Object> item = new LinkedHashMap<>();
            item.put("isbn", b.isbn()); item.put("title", b.title()); item.put("author", b.author());
            item.put("genre", b.genre()); item.put("rating", b.rating()); item.put("description", b.description());
            item.put("year", b.year()); item.put("location", b.location()); item.put("matchScore", r.score()); item.put("matchReasons", r.reasons());
            result.add(item);
        }
        return result;
    }

    private static boolean containsIgnoreCase(Collection<String> choices, String value) {
        return choices.stream().anyMatch(choice -> choice.equalsIgnoreCase(value));
    }
}
