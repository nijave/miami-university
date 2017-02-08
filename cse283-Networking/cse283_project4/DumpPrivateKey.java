import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyStore;

import sun.misc.BASE64Encoder;

/**
 * This is an utility program that reads the keystore file specified in the
 * parameter and dumps to the standard output the private key encoded in Base64
 * 
 */
public class DumpPrivateKey {
  /**
   * Main method. Invoked from command line. This method open the jks file
   * specified in the parameter to get the private key, transforms it in
   * Base64 format and write it to the standard output. @Usage@:
   * java DumpPrivateKey keystore.jks alias storepassword keypassword
   * 
   * @param args
   *            List of strings containing the input parameters.
   */
  static public void main(String[] args) {
    try {
      if (args.length != 4) {
        System.err
            .println("Usage java DumpPrivateKey keystore.jks alias storepassword keypassword");
        System.exit(1);
      }
      KeyStore ks = KeyStore.getInstance("jks");
      String keystore = args[0];
      String alias = args[1];
      String storepass = args[2];
      String keypass = args[3];

      ks.load(new FileInputStream(keystore), storepass.toCharArray());
      Key key = ks.getKey(alias, keypass.toCharArray());
      if (key == null) {
        System.err.println("No key found for alias:" + alias
            + " and keypass:" + keypass);
        System.exit(1);
      }

      BASE64Encoder myB64 = new BASE64Encoder();
      String b64 = myB64.encode(key.getEncoded());

      System.out.println("-----BEGIN PRIVATE KEY-----");
      System.out.println(b64);
      System.out.println("-----END PRIVATE KEY-----");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}