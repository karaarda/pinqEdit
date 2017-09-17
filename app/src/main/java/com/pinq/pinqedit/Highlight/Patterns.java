package com.pinq.pinqedit.Highlight;

import java.util.LinkedHashMap;
import java.util.regex.Pattern;

/**
 * Created by arda on 09.10.2015.
 */
public class Patterns {

    static LinkedHashMap<String, Highlight> patterns;

    static String InlineCommentColor = "#ff878787";
    static String CommentColor  = "#ff447744";
    static String NumberColor   = "#FF248585";
    static String KeyColor      = "#ff6f91";
    static String SymColor      = "#FFEEAA4E";
    static String TagColor      = "#FFA8BE60";

    static String JAVA  = "java";
    static String CPP   = "cpp";
    static String PY    = "cpp";
    static String PINQ  = "pinq";
    static String LUA   = "lua";
    static String SYM   = "sym";
    static String NUM   = "num";
    static String HTML  = "html";
    static String COMMENT = "comment";
    static String INLINECOMMENT = "inlinecomment";

    static {
        patterns = new LinkedHashMap<String, Highlight>();

        String regex = "(?<=\\b)((alignas)|(alignof)|(and)|(and_eq)|(asm)|(auto)|(bitand)|(bitorbool)|(break)|(case)|(catch)|(char)|(" +
                "char16_t)|(char32_t)|(class)|(compl)|(const)|(constexpr)|(const_cast)|(continue)|(decltype" +
                ")|(default)|(delete)|(do)|(double)|(dynamic_cast)|(echo)|(else)|(enum)|(explicit)|(export)|(extern)|(" +
                "false)|(float)|(for)|(friend)|(function)|(goto)|(if)|(inline)|(int)|(mutable)|(namespace)|(new)|(noexcept)|(" +
                "not)|(not_eq)|(null)|(nullptr)|(operator)|(or)|(or_eq)|(private)|(protected)|(public)|(register)|(" +
                "reinterpret_cast)|(return)|(short)|(signed)|(sizeof)|(static)|(static_assert)|(static_cast" +
                ")|(struct)|(switch)|(template)|(this)|(thread_local)|(throw)|(true)|(try)|(typedef)|(typeid)|(typename)|(undefined" +
                ")|(union)|(unsigned)|(final)|(using)|(var)|(virtual)|(void)|(volatile)|(wchar_t)|(while)|(xor)|(xor_eq)|(Arda)|(Kara)|(Pinq))(?=\\b)";

        Highlight hi    = new Highlight();
        hi.pattern      = Pattern.compile( regex, Pattern.CASE_INSENSITIVE);
        hi.color        = KeyColor;

        patterns.put(PINQ, hi);

        hi    = new Highlight();
        hi.pattern      = Pattern.compile("(\\b(\\d*[.]?\\d+)\\b)");
        hi.color        = NumberColor;

        patterns.put(NUM, hi);

        hi    = new Highlight();
        hi.pattern      = Pattern.compile("(!|,|\\(|\\)|\\+|\\-|\\*|<|>|=|\\.|\\?|;|\\{|\\}|\\[|\\]|\\|)");
        hi.color        = SymColor;

        patterns.put(SYM, hi);

        hi    = new Highlight();
        hi.pattern      = Pattern.compile("/\\*(?:.|[\\n\\r])*?\\*/|(?<!:)//.*|#.*");
        hi.color        = InlineCommentColor;

        patterns.put(INLINECOMMENT, hi);
    }
}
