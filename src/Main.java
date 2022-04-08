import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
//        String[] res = Mnemonic.generateMnemonic(16);
//        System.out.println(Arrays.toString(res));
//        System.out.println(res.length);


//        String[] mnemonic = {"knock", "tiny", "exact", "absent", "meat", "world", "under", "student", "spike", "bird", "canvas", "battle", "job", "ability", "dentist", "rigid", "head", "output", "stem", "leader", "cup", "minimum", "knee", "clump",};
        String[] mnemonic = {"cradle", "other", "need", "maple", "shell", "liberty", "kid", "zebra", "crush", "thought", "special", "capital", "frequent", "cost", "attend", "owner", "scan", "marine", "inspire", "aerobic", "select", "yard", "bench", "creek",};
        System.out.println(
            Mnemonic.bytesToHex(Mnemonic.mnemonicToSeed(mnemonic, ""))
        );
    }
}