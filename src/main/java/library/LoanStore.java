package library;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/** File-backed loan history; text fields are Base64 encoded to keep tab-separated rows safe. */
final class LoanStore {
    private final Path file = Path.of("data", "loans.tsv");

    synchronized List<Loan> load() throws Exception {
        Files.createDirectories(file.getParent());
        if (!Files.exists(file)) return new ArrayList<>();
        List<Loan> loans = new ArrayList<>();
        for (String row : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            if (row.isBlank()) continue;
            String[] p = row.split("\t", -1);
            if (p.length != 8) continue;
            loans.add(new Loan(dec(p[0]), dec(p[1]), dec(p[2]), dec(p[3]), dec(p[4]), dec(p[5]), dec(p[6]), dec(p[7])));
        }
        return loans;
    }

    synchronized void save(Collection<Loan> loans) throws Exception {
        Files.createDirectories(file.getParent());
        List<String> rows = new ArrayList<>();
        for (Loan loan : loans) rows.add(String.join("\t", enc(loan.id()), enc(loan.isbn()), enc(loan.title()),
                enc(loan.userEmail()), enc(loan.userName()), enc(loan.borrowedOn()), enc(loan.dueOn()), enc(loan.returnedOn())));
        Path tmp = file.resolveSibling("loans.tsv.tmp");
        Files.write(tmp, rows, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        try { Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE); }
        catch (AtomicMoveNotSupportedException e) { Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING); }
    }

    private static String enc(String s) { return Base64.getEncoder().encodeToString((s == null ? "" : s).getBytes(StandardCharsets.UTF_8)); }
    private static String dec(String s) { return new String(Base64.getDecoder().decode(s), StandardCharsets.UTF_8); }
}
