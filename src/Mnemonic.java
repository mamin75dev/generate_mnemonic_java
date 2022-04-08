import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

public class Mnemonic {

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    private static String generateEntropy(int size) {
        if (size < 16 || size > 32) {
            return "";
        }
        if (size % 2 != 0) {
            return "";
        }
        SecureRandom secureRandom = new SecureRandom();
        byte[] entropy = new byte[size];
        secureRandom.nextBytes(entropy);
        int[] entropy_int = convertByteArrayToIntArray(entropy);
        return addChecksumToEntropy(entropy, convertIntArrayToBinaryString(entropy_int));
    }

    private static int[] convertByteArrayToIntArray(byte[] byteArr) {
        IntBuffer intBuf =
            ByteBuffer.wrap(byteArr)
                .order(ByteOrder.BIG_ENDIAN)
                .asIntBuffer();
        int[] intArr = new int[intBuf.remaining()];
        intBuf.get(intArr);

        return intArr;
    }

    private static String convertIntArrayToBinaryString(int[] intArr) {
        StringBuilder builder = new StringBuilder();
        for (int int_elem : intArr) {
            builder.append(String.format("%32s", Integer.toBinaryString(int_elem))
                .replace(" ", "0"));
        }
        return builder.toString();
    }

    private static String addChecksumToEntropy(byte[] entropy, String entropyString) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedEntropy = digest.digest(entropy);
            String hashedEntropyString = convertIntArrayToBinaryString(convertByteArrayToIntArray(hashedEntropy));
            entropyString += hashedEntropyString.substring(0, hashedEntropyString.length() / 32);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return entropyString;
    }

    public static String[] generateMnemonic(int size) {
        String entropy = generateEntropy(size);
        String[] words = new String[entropy.length() / 11];
        for (int i = 0; i < entropy.length() / 11; i++) {
            words[i] = MnemonicEnglishDictionary.words[Integer.parseInt(next11Bits(entropy, i), 2)];
        }

        return words;
    }

    private static String next11Bits(String entropy, int offset) {
        return entropy.substring(offset * 11, ((offset + 1) * 11) - 1);
    }

    public static byte[] mnemonicToSeed(String[] mnemonic_arr, String password) {
        SecretKeyFactory skf = null;
        SecretKey key;
        String mnemonic = makeStringFromStringArray(mnemonic_arr);
//        String mnemonic = "cradle other need maple shell liberty kid zebra crush thought special capital frequent cost attend owner scan marine inspire aerobic select yard bench creek";
        System.out.println(Arrays.toString(mnemonic.toCharArray()));
        try {
            skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            PBEKeySpec spec = new PBEKeySpec(mnemonic.toCharArray(), ("mnemonic" + password).getBytes(), 2048, 512);
            key = skf.generateSecret(spec);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        return key.getEncoded();
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    private static String makeStringFromStringArray(String[] str_arr) {
        StringBuilder sb = new StringBuilder();
        for (String str : str_arr) {
                sb.append(str);
            if (str != str_arr[str_arr.length - 1])
                sb.append(" ");
        }
        return sb.toString();
    }
}


