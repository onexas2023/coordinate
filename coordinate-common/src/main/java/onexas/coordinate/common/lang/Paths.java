package onexas.coordinate.common.lang;

/**
 * 
 * @author Dennis Chen
 *
 */
public class Paths {

	/**
	 * merge the elements by the given separatorChar.
	 * @param separatorChar
	 * @param elements
	 * @return
	 */
	public static String merge(char separatorChar, String... elements) {
		String sc = Character.toString(separatorChar);

		StringBuilder sb = new StringBuilder();
		for (String p : elements) {
			if (sb.length() > 0) {
				if (sb.charAt(sb.length() - 1) == separatorChar) {
					if (p.startsWith(sc)) {
						sb.append(p.substring(1, p.length()));
					} else {
						sb.append(p);
					}
				} else {
					if (!p.startsWith(sc)) {
						sb.append(separatorChar);
					}
					sb.append(p);
				}
			} else {
				sb.append(p);
			}
		}
		return sb.toString();
	}

}