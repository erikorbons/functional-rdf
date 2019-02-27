package io.github.erikorbons.functionalrdf.core.encoding;

import java.nio.charset.StandardCharsets;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;

public final class PercentEncoding {
	
	public static String encode(final String input, final IntPredicate predicate) {
		return input
			.codePoints()
			.mapToObj(cp -> encodeCodePoint(cp, predicate))
			.collect(Collectors.joining());
	}
	
	public static String encodeCodePoint(final int codePoint, final IntPredicate predicate) {
		final char[] chars = Character.toChars(codePoint);
		final String value = new String(Character.toChars(codePoint));
		
		if (predicate.test(codePoint)) {
			final byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
			final StringBuilder builder = new StringBuilder();
			for (int i = 0; i < bytes.length; ++ i) {
				if (chars[i] > 0xF) {
					builder
						.append('%')
						.append(Integer.toHexString(bytes[i]));
				} else {
					builder
						.append("%0")
						.append(Integer.toHexString(bytes[i]));
				}
			}
			return builder.toString();
		} else {
			return value;
		}
	}
	
	public static String decode(final String input) {
		final byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
		final byte[] newBytes = new byte[bytes.length];
		
		int outOffset = 0;
		
		for (int i = 0; i < bytes.length; ++ i) {
			final byte c = bytes[i];
			
			// Copy characters not starting with a percent, or if there are not enough characters
			// to form a percent encoded digit:
			if (c != '%' || i + 3 > bytes.length || !isHexDigit(bytes[i + 1]) || !isHexDigit(bytes[i + 2])) {
				newBytes[outOffset ++] = c;
				continue;
			}
			
			// Add a hex digit:
			newBytes[outOffset ++] = (byte)(Character.digit(bytes[i + 1], 16) * 16 + Character.digit(bytes[i + 2], 16));
			i += 2;
		}
		
		return new String(newBytes, 0, outOffset, StandardCharsets.UTF_8);
	}
	
	private static boolean isHexDigit(final byte b) {
		return (b >= '0' && b <= '9')
			|| (b >= 'a' && b <= 'f')
			|| (b >= 'A' && b <= 'F');
	}
}
