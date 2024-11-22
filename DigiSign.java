import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Scanner;

public class DigiSign {

    private static final SecureRandom random = new SecureRandom();

    public static boolean isPrime(BigInteger n) {
        return n.isProbablePrime(1);
    }

    public static String hashMessage(String msg) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(msg.getBytes());
            StringBuilder hexStr = new StringBuilder();
            for (byte b : hash) {
                hexStr.append(String.format("%02x", b));
            }
            return hexStr.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Generate random prime p
        BigInteger p = BigInteger.probablePrime(256, random);
        System.out.println("Generated prime p: " + p);

        // Generate random divisor q (must be a prime divisor of p-1)
        BigInteger q;
        do {
            q = BigInteger.probablePrime(160, random);
        } while (!p.subtract(BigInteger.ONE).mod(q).equals(BigInteger.ZERO));
        System.out.println("Generated prime divisor q: " + q);

        // Generate random h and calculate g
        BigInteger h;
        BigInteger g;
        do {
            h = new BigInteger(p.bitLength(), random);
            g = h.modPow(p.subtract(BigInteger.ONE).divide(q), p);
        } while (h.compareTo(BigInteger.ONE) <= 0 || h.compareTo(p) >= 0 || g.equals(BigInteger.ONE));
        System.out.println("Generated g: " + g);

        // Generate random private key x
        BigInteger x = new BigInteger(q.bitLength(), random).mod(q);
        System.out.println("Generated private key x: " + x);

        // Compute public key y
        BigInteger y = g.modPow(x, p);
        System.out.println("Generated public key y: " + y);

        // Get message input from user
        System.out.print("Enter message to sign: ");
        String msg = scanner.nextLine();

        // Generate random k
        BigInteger k;
        do {
            k = new BigInteger(q.bitLength(), random).mod(q);
        } while (k.equals(BigInteger.ZERO));
        System.out.println("Generated random k: " + k);

        // Calculate signature (r, s)
        BigInteger r = g.modPow(k, p).mod(q);
        BigInteger hash = new BigInteger(hashMessage(msg), 16);
        BigInteger s = k.modInverse(q).multiply(hash.add(x.multiply(r))).mod(q);

        System.out.println("Signature: r = " + r + ", s = " + s);

        // Verification
        BigInteger sinv = s.modInverse(q);
        BigInteger u1 = hash.multiply(sinv).mod(q);
        BigInteger u2 = r.multiply(sinv).mod(q);
        BigInteger v = g.modPow(u1, p).multiply(y.modPow(u2, p)).mod(p).mod(q);

        System.out.println("Verification: " + (r.equals(v) ? "Verified" : "Not verified"));
    }
}
