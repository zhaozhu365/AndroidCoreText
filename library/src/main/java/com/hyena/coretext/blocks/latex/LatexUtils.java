package com.hyena.coretext.blocks.latex;

import maximsblog.blogspot.com.jlatexmath.core.DefaultTeXFont;
import maximsblog.blogspot.com.jlatexmath.core.Glue;
import maximsblog.blogspot.com.jlatexmath.core.MacroInfo;
import maximsblog.blogspot.com.jlatexmath.core.ParseException;
import maximsblog.blogspot.com.jlatexmath.core.SymbolAtom;
import maximsblog.blogspot.com.jlatexmath.core.TeXFormula;
import maximsblog.blogspot.com.jlatexmath.core.TeXParser;

/**
 * Created by yangzc on 17/6/27.
 */

public class LatexUtils {

    public static void initLatex() {
        try {
            SymbolAtom.get("");
        } catch (Throwable e) {}
        DefaultTeXFont.getSizeFactor(1);
        try {
            Glue.get(1, 1, null);
        } catch (Throwable e) {}
        try {
            TeXFormula.get("");
        } catch (Throwable e) {}
    }

}
