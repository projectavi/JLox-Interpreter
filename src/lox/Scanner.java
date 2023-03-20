package lox;

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

    // Instantiating a hashmap for the keywords
    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and",    TokenType.AND);
        keywords.put("class",  TokenType.CLASS);
        keywords.put("else",   TokenType.ELSE);
        keywords.put("false",  TokenType.FALSE);
        keywords.put("for",    TokenType.FOR);
        keywords.put("fun",    TokenType.FUN);
        keywords.put("if",     TokenType.IF);
        keywords.put("nil",    TokenType.NIL);
        keywords.put("or",     TokenType.OR);
        keywords.put("print",  TokenType.PRINT);
        keywords.put("return", TokenType.RETURN);
        keywords.put("super",  TokenType.SUPER);
        keywords.put("this",   TokenType.THIS);
        keywords.put("true",   TokenType.TRUE);
        keywords.put("var",    TokenType.VAR);
        keywords.put("while",  TokenType.WHILE);
    }

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

    private void scanToken() {
        char c = advance(); // Current character being parsed
        switch (c) { // Switch case to handle the single character lexemes
            case '(' -> {
                addToken(TokenType.LEFT_PAREN);
                break;
            }
            case ')' -> {
                addToken(TokenType.RIGHT_PAREN);
                break;
            }
            case '{' -> {
                addToken(TokenType.LEFT_BRACE);
                break;
            }
            case '}' -> {
                addToken(TokenType.RIGHT_BRACE);
                break;
            }
            case ',' -> {
                addToken(TokenType.COMMA);
                break;
            }
            case '.' -> {
                addToken(TokenType.DOT);
                break;
            }
            case '-' -> {
                addToken(TokenType.MINUS);
                break;
            }
            case '+' -> {
                addToken(TokenType.PLUS);
                break;
            }
            case ';' -> {
                addToken(TokenType.SEMICOLON);
                break;
            }
            case '*' -> {
                addToken(TokenType.STAR);
                break;
            }
            case '!' -> {
                if (match('=')) {
                    addToken(TokenType.BANG_EQUAL);
                }
                else {
                    addToken(TokenType.BANG);
                }
                break;
            }
            case '=' -> {
                addToken(match('=')? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
                break;
            }
            case '>' -> {
                addToken(match('=')? TokenType.GREATER_EQUAL : TokenType.GREATER);
                break;
            }
            case '<' -> {
                addToken(match('=')? TokenType.LESS_EQUAL : TokenType.LESS);
                break;
            }
            case '/' -> {
                if (match('/')) {
                    // turns the entire line into a comment, not parsed into lexemes and tokens
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else if (match('*')) {
                    // creates a multiline comment to not be parsed into lexemes and tokens
//                    boolean comment_flag = true;
//                    while (comment_flag) {
//                        if (isAtEnd()) comment_flag = false;
//                        if (peek() == '*') {
//                            advance();
//                            if (isAtEnd()) comment_flag = false;
//                            else if(peek() == '/') comment_flag = false;
//                        }
//                        else {
//                            advance();
//                        }
//                    }
                    while ((peek() != '*' && peekNext() != '/') && !isAtEnd()) advance();
                    if (peek() == '*' && peekNext() == '/') advance();
                }
                else {
                    addToken(TokenType.SLASH);
                }
                break;
            }
            case ' ', '\r', '\t' -> {break;}
            case '\n' -> {
                line++;
                break;
            }
            case '"' -> {
                string();
                break;
            }
            case 'o' -> {
                if (peek() == 'r') addToken(TokenType.OR);
                break;
            }


            default -> {
                if (isDigit(c)) {
                    number();
                }
                else if (isAlpha(c)) {
                    identifier();
                }
                else {
                    Lox.error(line, "Unexpected Character.");
                    break;
                }
            }
        }
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) advance();

        String identifier_text = source.substring(start, current);
        TokenType keyword = keywords.get(identifier_text);
        if (keyword == null) keyword = TokenType.IDENTIFIER;
        addToken(keyword);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private void number() {
        while (isDigit(peek())) {
            advance();
        }

        if (peek() == '.' && isDigit(peekNext())) {
            advance();

            while (isDigit(peek())) {
                advance();
            }
        }

        addToken(TokenType.NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private boolean isDigit(char c) {
        return (c >= '0' && c <= '9');
    }

    private void string() {
        while (peek()!= '"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }

        if (isAtEnd()) {
            Lox.error(line, "unterminated string");
            return;
        }

        advance();

        // Remove the encapsulating quotes from the string literal
        String value = source.substring(start + 1, current - 1);
        addToken(TokenType.STRING, value);
    }

    private char peek() {
        if (isAtEnd()) return '\0';
        // This is a look ahead that looks at the next character without passing over "consuming" it
        return source.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private boolean match(char exp){
        if (isAtEnd()) { return false; }
        if (source.charAt(current) != exp) { return false; }

        current++;
        return true;
    }

    // helper method for scanToken to advance the current character position/char being read
    private char advance() {
        return source.charAt(current++); // consumes the next character in the source file
    }

    // helper method for scanToken to add the scanned token in
    private void addToken(TokenType tokenType) {
        addToken(tokenType, null);
    }

    // overload of addToken to handle the main cases
    private void addToken(TokenType tokenType, Object literal) {
        String sub = source.substring(start, current); // grab the part of the source file which represents the lexeme
        // of the token that has just been parsed
        tokens.add(new Token(tokenType, sub, literal, line));
    }


    private boolean isAtEnd() {
        return current >= source.length();
    }
}

