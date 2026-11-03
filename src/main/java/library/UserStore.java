package library;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.*;

/** File-backed local accounts. Passwords are stored as salted PBKDF2 hashes. */
final class UserStore {
    static final String ADMIN_EMAIL = "admin@openshelf.local";
    private final Path file = Path.of("data", "users.tsv");
    private final Map<String, LibraryUser> users = new HashMap<>();
    private final SecureRandom random = new SecureRandom();
    private String bootstrapAdminPassword;

    UserStore() throws Exception {
        Files.createDirectories(file.getParent());
        if (Files.exists(file)) {
            for (String row : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                String[] p = row.split("\t", -1);
                if (p.length != 6 && p.length != 7) continue;
                String role = p.length == 7 ? p[6] : "READER";
                LibraryUser u = new LibraryUser(dec(p[1]), dec(p[0]), p[2], p[3], role, decodeSet(p[4]), decodeSet(p[5]));
                users.put(u.email(), u);
            }
        }
        if (users.values().stream().noneMatch(u -> u.role().equals("ADMIN"))) bootstrapAdminPassword = createBootstrapAdmin();
    }

    synchronized LibraryUser register(String name, String email, String password, String requestedRole) throws Exception {
        String key = normalizeEmail(email);
        String role = requestedRole == null ? "READER" : requestedRole.strip().toUpperCase(Locale.ROOT);
        if (!role.equals("READER") && !role.equals("ADMIN")) throw new IllegalArgumentException("Choose Reader or Admin account type");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Enter your name");
        if (!key.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) throw new IllegalArgumentException("Enter a valid email address");
        if (password == null || password.length() < 8) throw new IllegalArgumentException("Password must be at least 8 characters");
        if (users.containsKey(key)) throw new IllegalArgumentException("An account with this email already exists. Sign in instead.");
        byte[] salt = new byte[16]; random.nextBytes(salt);
        LibraryUser user = new LibraryUser(name.strip(), key, Base64.getEncoder().encodeToString(salt), hash(password.toCharArray(), salt), role, Set.of(), Set.of());
        users.put(key, user); save(); return user;
    }

    synchronized LibraryUser authenticate(String email, String password) throws Exception {
        LibraryUser user = users.get(normalizeEmail(email));
        if (user == null || password == null) return null;
        byte[] salt = Base64.getDecoder().decode(user.salt());
        byte[] actual = HexFormat.of().parseHex(hash(password.toCharArray(), salt));
        byte[] expected = HexFormat.of().parseHex(user.passwordHash());
        return MessageDigest.isEqual(actual, expected) ? user : null;
    }

    synchronized LibraryUser get(String email) { return users.get(email); }
    String bootstrapAdminPassword() { return bootstrapAdminPassword; }

    /** Resets only the admin password and preserves every account and preference. */
    synchronized String resetAdminPassword() throws Exception {
        LibraryUser old = users.get(ADMIN_EMAIL);
        if (old == null || !old.role().equals("ADMIN")) {
            throw new IllegalStateException("The admin account was not found.");
        }
        String password = generatePassword();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        LibraryUser updated = new LibraryUser(old.name(), old.email(),
                Base64.getEncoder().encodeToString(salt), hash(password.toCharArray(), salt),
                old.role(), old.genres(), old.authors());
        users.put(old.email(), updated);
        save();
        return password;
    }

    synchronized LibraryUser savePreferences(String email, Set<String> genres, Set<String> authors) throws Exception {
        LibraryUser old = users.get(email);
        if (old == null) throw new IllegalArgumentException("Account not found");
        LibraryUser updated = new LibraryUser(old.name(), old.email(), old.salt(), old.passwordHash(), old.role(), genres, authors);
        users.put(email, updated); save(); return updated;
    }

    private String hash(char[] password, byte[] salt) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(password, salt, 120_000, 256);
        try { return HexFormat.of().formatHex(SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded()); }
        finally { spec.clearPassword(); Arrays.fill(password, '\0'); }
    }

    private void save() throws Exception {
        List<String> rows = new ArrayList<>();
        for (LibraryUser u : users.values()) rows.add(String.join("\t", enc(u.email()), enc(u.name()), u.salt(), u.passwordHash(), encodeSet(u.genres()), encodeSet(u.authors()), u.role()));
        Path tmp = file.resolveSibling("users.tsv.tmp");
        Files.write(tmp, rows, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        try { Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE); }
        catch (AtomicMoveNotSupportedException e) { Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING); }
    }
    private String createBootstrapAdmin() throws Exception {
        String password = generatePassword();
        byte[] salt = new byte[16]; random.nextBytes(salt);
        LibraryUser admin = new LibraryUser("Library Admin", ADMIN_EMAIL, Base64.getEncoder().encodeToString(salt), hash(password.toCharArray(), salt), "ADMIN", Set.of(), Set.of());
        users.put(admin.email(), admin); save(); return password;
    }
    private String generatePassword() {
        String alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789!@#";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 16; i++) password.append(alphabet.charAt(random.nextInt(alphabet.length())));
        return password.toString();
    }
    private String normalizeEmail(String email) { return email == null ? "" : email.strip().toLowerCase(Locale.ROOT); }
    private String encodeSet(Set<String> values) { return enc(String.join("\n", values)); }
    private Set<String> decodeSet(String value) { String s = dec(value); return s.isBlank() ? Set.of() : new LinkedHashSet<>(Arrays.asList(s.split("\n"))); }
    private String enc(String s) { return Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8)); }
    private String dec(String s) { return new String(Base64.getDecoder().decode(s), StandardCharsets.UTF_8); }
}
