import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    // offsets indexing into the string
    private int start = 0; // first character in lexeme
    private int current = 0; // current character in lexeme
    private int line = 1; // source line of current so tokens know location

    Scanner(String source) {
        this.source = source;
    }

    // Generate contents for the token list parsed from the source string
    List<Token> scanTokens() {
        while (!isAtEnd()) {
            // at the beginning of each lexeme
            start = current;
            scanToken();
        }

        // after parsing all the lexemes into tokens, add the end of file token
        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }
}

