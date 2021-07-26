package wwwordz.puzzle;

import java.io.*;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.text.Normalizer;
import java.text.Normalizer.Form;


/**
 * "An organized collection of words, optimized for searching them. 
 * 		This class is a singleton, meaning that there is, at most, 
 * 		a single instance of this class per application.<br><br>
 * 
 * This dictionary uses a collection of Portuguese words 
 * 		loaded as a resource from a file in this package. 
 * 		It is backed by a Trie to index words and speedup searches."<br><br>
 *
 * In this case, the Trie instance is not private, since it became useful
 * 		in class Generator of this package.
 * 
 * @see https://www.dcc.fc.up.pt/~zp/aulas/1920/asw/api/wwwordz/puzzle/Dictionary.html
 */
public class Dictionary {
	private final static int WORD_MIN_LIM = 3,
			 				 WORD_MAX_LIM = 16;
	private static Dictionary dictionary = null;
	Trie trie;
	
	/**
	 * Creates (the only) instance of Dictionary 
	 * 		and a Trie that it will use.<br>
	 * 
	 * The dictionary file must be under the specified path, and it
	 * 		is parsed to obtain only the words it contains.<br>
	 * 
	 * Not all of the file's words will be used. The minimum length
	 * 		of this Dictionary's words must be 3 characters, 
	 * 		and the maximum 16. Furthermore, words with special characters, 
	 * 		such as hyphens, are also rejected.<br>
	 * 
	 * All of this Dictionary's words are stored in full upper case notation.
	 * 
	 */
	private Dictionary() {
		final String  DIC_FILE = "wwwordz/puzzle/pt-PT-AO.dic";
		
		try (
		InputStream in = ClassLoader.getSystemResourceAsStream(DIC_FILE);
		BufferedReader reader = new BufferedReader(
										new InputStreamReader(in,"UTF-8"));
		) { 
            String line, dicword;
            Pattern wordstop = Pattern.compile("/|\\s"),
                    validword = Pattern.compile("[a-zA-Z]{" 
                    									+ WORD_MIN_LIM + "," 
                    									+ WORD_MAX_LIM + "}");
            Matcher wordmatcher;

            trie = new Trie();
            line = reader.readLine();

            while ((line = reader.readLine()) != null) {
                dicword = wordstop.split(line)[0];
                wordmatcher = validword.matcher(dicword);

                if ( wordmatcher.matches() ) 
                {
                    Normalizer.normalize(dicword.toUpperCase(Locale.ENGLISH),
                                          Form.NFD).
                           replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

                    trie.put(dicword.toUpperCase(Locale.ENGLISH));
                }
            }
        } 
        catch (IOException cause) {
            cause.printStackTrace();;
        } 
    }
	
	/**
	 * Retrieves the sole instance of this class,
	 * 		also creating it if there isn't one yet.
	 * 
	 * @return the single instance of Dictionary
	 */
	public static Dictionary getInstance() {
		if (dictionary == null) {
				dictionary = new Dictionary();
		}
		return dictionary;
	}

	/**
	 * Begins and returns a Trie.Search instance.
	 * 
	 * @return a Trie.Search instance
	 * 
	 * @see wwwordz.puzzle.Trie.startSearch()
	 */
	public Trie.Search startSearch() {
		return trie.startSearch();
	}

	/**
	 * Uses the internal Trie instance to retrieve
	 * 		a random large word.
	 * 
	 * @return a word from the Dictionary's Trie
	 * 
	 * @see wwwordz.puzzle.Trie.getRandomLargeWord()
	 */
	public String getRandomLargeWord() {
		return trie.getRandomLargeWord();
	}
}
