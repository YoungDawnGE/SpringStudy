import org.junit.Test;

/**
 * Created by GYC
 * 2020/11/13 10:43
 */
public class charTest {

    @Test
    public void charTest01() {
        String s = "Gyc";
        char[] chars = s.toCharArray();
        chars[0] = (char) (chars[0] + 32);
        System.out.println(String.valueOf(chars));

    }
}
