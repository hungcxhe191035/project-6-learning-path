
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Test {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        boolean matches1 = encoder.matches("123456", "$2a$12$9nYTgwKTwY931.EcLZ7OyuDon1jamWCxJD7kDuapIMMWPSPKeek8q");
        boolean matches2 = encoder.matches("12345678", "$2a$12$9nYTgwKTwY931.EcLZ7OyuDon1jamWCxJD7kDuapIMMWPSPKeek8q");
        boolean matches3 = encoder.matches("password", "$2a$12$9nYTgwKTwY931.EcLZ7OyuDon1jamWCxJD7kDuapIMMWPSPKeek8q");
        System.out.println("123456: " + matches1);
        System.out.println("12345678: " + matches2);
        System.out.println("password: " + matches3);
    }
}

