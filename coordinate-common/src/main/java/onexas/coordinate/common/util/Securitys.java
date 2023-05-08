package onexas.coordinate.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.pkcs.RSAPrivateKeyStructure;
import org.bouncycastle.openssl.PEMReader;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.common.util.PublicKeyReaderUtil.PublicKeyParseException;

/**
 * Security utility that use Bouncy Castle Crypto package (terrible sun's
 * default implement, it didn't display any useful message when error)
 * 
 * @author Dennis Chen
 * 
 */
public class Securitys {

	static private final String BC_RPOVIDER = "BC";
	static {
		if (java.security.Security.getProvider(BC_RPOVIDER) == null) {
			try {
				@SuppressWarnings("rawtypes")
				Class clz = Class.forName("org.bouncycastle.jce.provider.BouncyCastleProvider");
				//install bc provider if not found;
				try{
					java.security.Security.addProvider((Provider)clz.newInstance());
				}catch(Exception x){
					throw new IllegalStateException(x.getMessage(),x);
				}
			} catch (ClassNotFoundException e) {
			}
		}
	}

	public static String md5String(String input) {
		if (null == input)
			return null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(input.getBytes("UTF8"), 0, input.length());
			return new BigInteger(1, md.digest()).toString(16);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public static byte[] md5(String input) {
		if (null == input)
			return null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(input.getBytes("UTF8"), 0, input.length());
			return md.digest();
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public static String md5String(InputStream is) {
		if (null == is)
			return null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] buffer = new byte[100];
			int i;
			while ((i = is.read(buffer)) != -1) {
				md.update(buffer, 0, i);
			}
			return new BigInteger(1, md.digest()).toString(16);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public static byte[] md5(InputStream is) {
		if (null == is)
			return null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] buffer = new byte[100];
			int i;
			while ((i = is.read(buffer)) != -1) {
				md.update(buffer, 0, i);
			}
			return md.digest();
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public static KeyStore loadKeyStore(File file, String password) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			return loadKeyStore(fis, password);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static KeyStore loadKeyStore(InputStream is, String password) {
		return loadKeyStore("JCEKS", is, password);
	}

	public static KeyStore loadKeyStore(String type, InputStream is, String password) {
		try {
			// bc provideer didn't support, jceks, we use sun's implement
			// (no provider) when getting key store
			KeyStore store = KeyStore.getInstance(type);
			store.load(is, password == null ? null : password.toCharArray());
			return store;
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public static byte[] encrypt(byte[] data, Key secKey) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		encrypt(new ByteArrayInputStream(data), os, secKey);
		return os.toByteArray();

	}

	public static void encrypt(InputStream is, OutputStream os, Key secKey) {
		try {
			Cipher cipher = Cipher.getInstance(secKey.getAlgorithm());
			cipher.init(Cipher.ENCRYPT_MODE, secKey);
			cipher(is, os, cipher);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public static byte[] decrypt(byte[] data, Key secKey) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		decrypt(new ByteArrayInputStream(data), os, secKey);
		return os.toByteArray();
	}

	public static void decrypt(InputStream is, OutputStream os, Key secKey) {
		try {
			Cipher cipher = Cipher.getInstance(secKey.getAlgorithm());
			cipher.init(Cipher.DECRYPT_MODE, secKey);
			cipher(is, os, cipher);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	
	public static byte[] encryptRSAOAEP(byte[] data, Key secKey) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		encryptRSAOAEP(new ByteArrayInputStream(data), os, secKey);
		return os.toByteArray();

	}

	public static void encryptRSAOAEP(InputStream is, OutputStream os, Key secKey) {
		try {
			Cipher cipher = Cipher.getInstance("RSA/None/OAEPWithSHA1AndMGF1Padding",BC_RPOVIDER);
			cipher.init(Cipher.ENCRYPT_MODE, secKey);
			cipher(is, os, cipher);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}	
	
	public static byte[] decryptRSAOAEP(byte[] data, Key secKey) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		decryptRSAOAEP(new ByteArrayInputStream(data), os, secKey);
		return os.toByteArray();
	}

	public static void decryptRSAOAEP(InputStream is, OutputStream os, Key secKey) {
		try {
			Cipher cipher = Cipher.getInstance("RSA/None/OAEPWithSHA1AndMGF1Padding",BC_RPOVIDER);
			cipher.init(Cipher.DECRYPT_MODE, secKey);
			cipher(is, os, cipher);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}	
	
	private static void cipher(InputStream is, OutputStream os, Cipher cipher) {
		try {
			byte[] buffer = new byte[100];
			int i;
			CipherOutputStream cos = new CipherOutputStream(os, cipher);
			while ((i = is.read(buffer)) != -1) {
				cos.write(buffer, 0, i);
			}
			cos.close();
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public static KeyPair generateKeyPair() {
		return generateKeyPair("RSA", 2048);
	}

	public static KeyPair generateKeyPair(String algorithm, int length) {
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance(algorithm);

			keyGen.initialize(length);
			KeyPair keyPair = keyGen.generateKeyPair();
			return keyPair;
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public static byte[] encodePublicKey(PublicKey publicKey) {
		// Store Public Key.
		X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKey.getEncoded());
		return x509EncodedKeySpec.getEncoded();
	}

	public static PublicKey decodePublicKey(String algorithm, byte[] encodedPublicKey) {
		try {
			KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
			X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
			PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
			return publicKey;
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public static byte[] encodePrivateKey(PrivateKey privateKey) {
		// Store Private Key.
		PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
		return pkcs8EncodedKeySpec.getEncoded();
	}

	public static PrivateKey decodePrivateKey(String algorithm, byte[] encodedPrivateKey) {
		try {
			KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
			PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
			PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
			return privateKey;
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	// https://github.com/jclouds/jclouds/blob/master/compute/src/main/java/org/jclouds/ssh/SshKeys.java
	public static String encodeAsOpenSSH(RSAPublicKey key){
		byte[] keyBlob = keyBlob(key.getPublicExponent(), key.getModulus());
		return "ssh-rsa " + Base64.encodeBase64String(keyBlob);
	}
	public static PublicKey decodeAsOpenSSH(String pubkeyString){
		try {
			return PublicKeyReaderUtil.load(pubkeyString);
		} catch (PublicKeyParseException e) {
			throw new IllegalStateException(e.getMessage(),e);
		}
	}
	
	
	public static String getFingerPrint(RSAPublicKey key){
		return getFingerPrint(key.getPublicExponent(),key.getModulus());
	}
	
	public static String getFingerPrint(PublicKey key){
		if(key instanceof RSAPublicKey){
			return getFingerPrint((RSAPublicKey)key);
		}else if(key instanceof DSAPublicKey){
			//TODO
//			return getFingerPrint((DSAPublicKey)key);
		}
		//unsupported
		throw new IllegalStateException("unsupported key "+key.getClass());
	}
	//http://grepcode.com/file_/repo1.maven.org/maven2/org.jclouds/jclouds-core/1.5.0-beta.9/org/jclouds/crypto/Pems.java/?v=source
	public static String encodeAsPem(RSAPrivateCrtKey key){
		return pem(getEncoded(key), "-----BEGIN RSA PRIVATE KEY-----");
	}

	private static byte[] getEncoded(RSAPrivateCrtKey key){
		RSAPrivateKeyStructure keyStruct = new RSAPrivateKeyStructure(key.getModulus(), key.getPublicExponent(),
				key.getPrivateExponent(), key.getPrimeP(), key.getPrimeQ(), key.getPrimeExponentP(),
				key.getPrimeExponentQ(), key.getCrtCoefficient());

		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		ASN1OutputStream aOut = new ASN1OutputStream(bOut);

		try {
			aOut.writeObject(keyStruct);
			aOut.close();
			return bOut.toByteArray();
		} catch (IOException e) {
			throw new IllegalStateException(e.getMessage(),e);
		}
	}

	private static String pem(byte[] key, String marker) {
		return pem(key, marker, 64);
	}

	static String pem(byte[] key, String marker, int length) {
		return new StringBuilder(marker + "\n")
				.append(Joiner.on('\n').join(Splitter.fixedLength(length).split(Base64.encodeBase64String(key))))
				.append("\n" + marker.replace("BEGIN", "END") + "\n").toString().trim();
	}

	private static byte[] keyBlob(BigInteger publicExponent, BigInteger modulus) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			writeLengthFirst("ssh-rsa".getBytes(), out);
			writeLengthFirst(publicExponent.toByteArray(), out);
			writeLengthFirst(modulus.toByteArray(), out);
			return out.toByteArray();
		} catch (IOException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	// http://www.ietf.org/rfc/rfc4253.txt
	private static void writeLengthFirst(byte[] array, ByteArrayOutputStream out) throws IOException {
		out.write((array.length >>> 24) & 0xFF);
		out.write((array.length >>> 16) & 0xFF);
		out.write((array.length >>> 8) & 0xFF);
		out.write((array.length >>> 0) & 0xFF);
		if (array.length == 1 && array[0] == (byte) 0x00)
			out.write(new byte[0]);
		else
			out.write(array);
	}
	
	
	public static PrivateKey decodeAsPem(String pemkeyString){
		PEMReader reader = new PEMReader(new StringReader(pemkeyString));
		Object obj;
		try{
			while((obj=reader.readObject())!=null){
				if(obj instanceof KeyPair){
					return ((KeyPair)obj).getPrivate();
				}
			}
			throw new IllegalStateException("can't find private key");
		}catch(Exception x){
			throw new IllegalStateException(x.getMessage(),x);
		}finally{
			try {
				reader.close();
			} catch (IOException e) {}
		}
	}
	
	 /**
	    * Create a fingerprint per the following <a
	    * href="http://tools.ietf.org/html/draft-friedl-secsh-fingerprint-00" >spec</a>
	    * 
	    * @param publicExponent
	    * @param modulus
	    * 
	    * @return hex fingerprint ex. {@code 2b:a9:62:95:5b:8b:1d:61:e0:92:f7:03:10:e9:db:d9}
	    */
	private static String getFingerPrint(BigInteger publicExponent, BigInteger modulus) {
		byte[] keyBlob = keyBlob(publicExponent, modulus);
		return hexColonDelimited(Hashing.md5().hashBytes(keyBlob));
	}

	private static String hexColonDelimited(HashCode hc) {
	    return Joiner.on(':').join(Splitter.fixedLength(2).split(Strings.getHexString(hc.asBytes()).toLowerCase()));
	}
	
	/**
	 * encrypt the data with a limited dataBlock size, return cyper bytes array that contains many blocks, each block will always
	 * contains first 4bytes to indicate block size, e.g 4-size-bytes,cyperbytes,4-size-bytes,cyperbytes.
	 * the data might be decrypt by {@link #decryptWithChyperBlock(byte[], Key, int)}
	 * @param dataBlockSize dataBlockSize the data block size for secKey, for rsa it is key_length/8 - padding, e.g a 1024 bit key is (1024/8-11) = 117
	 */
	public static byte[] encryptWithDataBlock(byte[] data, Key secKey, int dataBlockSize) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int i = 0;
		while(i<data.length){
			byte[] block = new byte[i + dataBlockSize <= data.length ? dataBlockSize : data.length - i];
			System.arraycopy(data, i, block, 0, block.length);
			byte[] cypher = encrypt(block, secKey);
			try {
				//write chyper block size first
				baos.write(ByteBuffer.allocate(4).putInt(cypher.length).array());
				baos.write(cypher);
			} catch (IOException e) {
				throw new IllegalStateException(e.getMessage(),e);
			}
			i += block.length;
		}
		return baos.toByteArray(); 
	}
	
	/**
	 * decrypt the cypher that encrypte by {@link #encryptWithDataBlock(byte[], Key, int)}
	 * @return
	 */
	public static byte[] decryptWithChyperBlock(byte[] cypher, Key secKey) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int i = 0;
		while(i<cypher.length){
			//read chyperBlockSize at first 4 bytes
			int chyperBlockSize = ((ByteBuffer)ByteBuffer.allocate(4).put(cypher, i, 4).position(0)).getInt();
			byte[] block = new byte[chyperBlockSize];
			i += 4;
			System.arraycopy(cypher, i, block, 0, chyperBlockSize);
			try {			
				byte[] content = decrypt(block, secKey);
				baos.write(content);
			} catch (IOException e) {
				throw new IllegalStateException(e.getMessage(),e);
			}
			i += chyperBlockSize;
		}
		return baos.toByteArray();
	}
	/**
	 * encrypt the text by key and return as a base 64 encoded string, must be decode by {@link #decryptString(String, Key)}
	 * @param dataBlockSize the data block size for secKey, for rsa it is key_length/8 - padding, e.g a 1024 bit key is (1024/8-11) = 117
	 */
	public static String encryptString(String text, Key secKey, int dataBlockSize) {
		byte[] bytes = text.getBytes(Strings.UTF8);
		bytes = encryptWithDataBlock(bytes, secKey, dataBlockSize);
		return Base64.encodeBase64String(bytes); 
	}
	
	/**
	 * decode the base64 string and decrypt it by the key 
	 */
	public static String decryptString(String base64Cypher, Key secKey){
		byte[] bytes = Base64.decodeBase64(base64Cypher);
		bytes = decryptWithChyperBlock(bytes, secKey);
		return new String(bytes,Strings.UTF8);
	}
	
	public static SecretKey generateSecretKey() {
		//default 3des
		return generateSecretKey("DESEDE");
	}

	public static SecretKey generateSecretKey(String algorithm) {
		try{
			KeyGenerator keyGen = KeyGenerator.getInstance(algorithm);
		    SecretKey key = keyGen.generateKey();
		    return key;
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}
	
	public static String encodeSecretKey(SecretKey key){
		return key.getAlgorithm()+" "+Base64.encodeBase64String(key.getEncoded());
	}
	
	public static SecretKey decodeSecretKey(String encodedKey){
		int idx = encodedKey.indexOf(" ");
		if(idx==-1){
			throw new IllegalStateException("algorithm not found");
		}
		String algorithm = encodedKey.substring(0, idx);
		byte[] encoded = Base64.decodeBase64(encodedKey.substring(idx+1, encodedKey.length()));
		return new SecretKeySpec(encoded, 0, encoded.length, algorithm);
	}
}
