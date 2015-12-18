import org.mindrot.jbcrypt.*;

public class CreateUser {
	public static void main(String args[]) {
		String password = "test";
		String salt = BCrypt.gensalt();
		System.out.println(BCrypt.hashpw(password, salt));
	}
}
