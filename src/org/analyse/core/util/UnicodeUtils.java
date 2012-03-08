package org.analyse.core.util;

import java.text.CharacterIterator;
import java.text.Normalizer;
import java.text.StringCharacterIterator;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UnicodeUtils
{
    private static final Map<Character, String> decomposedChars = Collections
            .unmodifiableMap(new HashMap<Character, String>() {
                private static final long serialVersionUID = 1L;
                {
                    put('\u00DF', "ss");
                    put('\u00C6', "Ae");
                    put('\u00E6', "ae");
                    put('\u00E6', "ae");
                    put('\u00D8', "O");
                    put('\u0152', "Oe");
                    put('\u0153', "oe");
                }
            });

    /**
     * Strips accents from an input String, and decompose combined characters
     * into multiple basic ASCII characters.
     * 
     * The method is based on the Unicode KD normalization form. It iterates
     * over the resulting characters, and the strips everything that is not in
     * the Basic Latin Unicode block.
     * 
     * Based on http://www.codeproject.com/KB/cs/UnicodeNormalization.aspx
     * (found while Google-ing "stripping accents unicode string"), but with
     * legacy Java 1.6 classes. Also inspired by
     * http://www.nntp.perl.org/group/perl.i18n/2008/05/msg209.html
     * 
     * @param accentedString
     *            A string that contains accents.
     * @return The same string, without accents.
     * @see Normalizer.Form.NFKD, Character.UnicodeBlock.BASIC_LATIN
     */
    public static String decomposeToBasicLatin(String accentedString)
    {
        StringBuilder unaccentedString = new StringBuilder();
        String normalizedString = Normalizer.normalize(accentedString, Normalizer.Form.NFKD);
        CharacterIterator iterator = new StringCharacterIterator(normalizedString);
        for (char c = iterator.first(); c != CharacterIterator.DONE; c = iterator.next())
            if (decomposedChars.containsKey(c))
                unaccentedString.append(decomposedChars.get(c));
            else if (Character.UnicodeBlock.BASIC_LATIN.equals(Character.UnicodeBlock.of(c)))
                unaccentedString.append(c);
        return unaccentedString.toString();
    }
}
