package hurricane.rdf.core.encoding;

public final class CharacterUtils {

	public static boolean isIPChar(final int codePoint) {
		return isIUnreserved(codePoint) 
				|| isSubDelimiter(codePoint)
				|| codePoint == ':'
				|| codePoint == '@';
	}
	
	public static boolean isPChar(final int codePoint) {
		return isUnreserved(codePoint) 
				|| isSubDelimiter(codePoint)
				|| codePoint == ':'
				|| codePoint == '@';
	}
	
	public static boolean isIPrivate(final int codePoint) {
		return
				(codePoint >= '\uE000' && codePoint <= '\uF8FF')
				|| (codePoint >= '\uF000' && codePoint <= '\uFFFD')
				|| (codePoint >= 0x100000 && codePoint <= 0x10FFFD);
	}
	
	public static boolean isIUnreserved(final int codePoint) {
		return isAlpha(codePoint)
				|| isDigit(codePoint)
				|| codePoint == '-'
				|| codePoint == '.'
				|| codePoint == '_'
				|| codePoint == '~'
				|| isUCSChar(codePoint);
	}
	
	public static boolean isUnreserved(final int codePoint) {
		return isAlpha(codePoint)
				|| isDigit(codePoint)
				|| codePoint == '-'
				|| codePoint == '.'
				|| codePoint == '_'
				|| codePoint == '~';
	}
	
	public static boolean isAlpha(final int codePoint) {
		return 
				(codePoint >= 'a' && codePoint <= 'z')
				|| (codePoint >= 'A' && codePoint <= 'Z');
	}
	
	public static boolean isDigit(final int codePoint) {
		return
				(codePoint >= '0' && codePoint <= '9');
	}
	
	public static boolean isUCSChar(final int codePoint) {
		return (codePoint >= 0xA0 && codePoint <= 0xD7FF)
				|| (codePoint >= 0xF900 && codePoint <= 0xFDCF)
				|| (codePoint >= 0xFDF0 && codePoint <= 0xFFEF)
        		|| (codePoint >= 0x10000 && codePoint <= 0x1FFFD) 
        		|| (codePoint >= 0x20000 && codePoint <= 0x2FFFD) 
        		|| (codePoint >= 0x30000 && codePoint <= 0x3FFFD)
        		|| (codePoint >= 0x40000 && codePoint <= 0x4FFFD)
        		|| (codePoint >= 0x50000 && codePoint <= 0x5FFFD)
        		|| (codePoint >= 0x60000 && codePoint <= 0x6FFFD)
   				|| (codePoint >= 0x70000 && codePoint <= 0x7FFFD)
				|| (codePoint >= 0x80000 && codePoint <= 0x8FFFD)
        		|| (codePoint >= 0x90000 && codePoint <= 0x9FFFD)
        		|| (codePoint >= 0xA0000 && codePoint <= 0xAFFFD)
        		|| (codePoint >= 0xB0000 && codePoint <= 0xBFFFD)
        		|| (codePoint >= 0xC0000 && codePoint <= 0xCFFFD)
        		|| (codePoint >= 0xD0000 && codePoint <= 0xDFFFD)
        		|| (codePoint >= 0xE1000 && codePoint <= 0xEFFFD);		
	}
	
	public static boolean isSubDelimiter(final int codePoint) {
		return codePoint == '!'
				|| codePoint == '$'
				|| codePoint == '&'
				|| codePoint == '\''
				|| codePoint == '('
				|| codePoint == ')'
				|| codePoint == '*'
				|| codePoint == '+'
				|| codePoint == ','
				|| codePoint == ';'
				|| codePoint == '=';
	}
}
