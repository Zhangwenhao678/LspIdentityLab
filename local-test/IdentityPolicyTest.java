import com.example.identitylab.IdentityPolicy;
public class IdentityPolicyTest {
 private static void check(boolean a, boolean e, String n){if(a!=e)throw new AssertionError(n);}
 public static void main(String[] args){
  check(IdentityPolicy.isValidCustomMid("0123456789abcdef"),true,"valid");
  check(IdentityPolicy.isValidCustomMid("0123456789ABCDEF"),false,"uppercase");
  check(IdentityPolicy.isValidCustomMid("0123456789abcde"),false,"short");
  check(IdentityPolicy.isValidCustomMid("0123456789abcdef0"),false,"long");
  check(IdentityPolicy.isValidCustomMid(" 0123456789abcdef "),true,"trim");
  check(IdentityPolicy.isValidCustomMid(null),false,"null");
  System.out.println("IdentityPolicy dynamic JVM test: PASS (6 cases)");
 }
}
