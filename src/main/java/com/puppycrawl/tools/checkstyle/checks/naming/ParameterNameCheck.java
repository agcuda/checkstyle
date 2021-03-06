////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2016 the original author or authors.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
////////////////////////////////////////////////////////////////////////////////

package com.puppycrawl.tools.checkstyle.checks.naming;

import java.util.Optional;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.Scope;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.CheckUtils;
import com.puppycrawl.tools.checkstyle.utils.ScopeUtils;

/**
* <p>
 * Checks that method and <code>catch</code> parameter names conform to a format specified
 * by the format property. The format is a
 * {@link java.util.regex.Pattern regular expression}
 * and defaults to
 * <strong>^[a-z][a-zA-Z0-9]*$</strong>.
 * </p>
 * <p>The check has the following options:</p>
 * <p><b>ignoreOverridden</b> - allows to skip methods with Override annotation from
 * validation. Default values is <b>false</b> .</p>
 * <p><b>scope</b> - visibility scope of methods to be checked.
 *  Default value is <b>anoninner</b> .</p>
 * <p><b>excludeScope</b> - visibility scope of methods not to be checked.
 *  Default value is <b>null</b> .</p>
 * <p>
 * An example of how to configure the check is:
 * </p>
 * <pre>
 * &lt;module name="ParameterName"/&gt;
 * </pre>
 * <p>
  * An example of how to configure the check for names that begin with
 * a lower case letter, followed by letters, digits, and underscores is:
 * </p>
 * <pre>
 * &lt;module name="ParameterName"&gt;
 *    &lt;property name="format" value="^[a-z][_a-zA-Z0-9]+$"/&gt;
 * &lt;/module&gt;
 * </pre>
 * <p>
 * An example of how to configure the check to skip methods with Override annotation from
 * validation:
 * </p>
 * <pre>
 * &lt;module name="ParameterName"&gt;
 *    &lt;property name="ignoreOverridden" value="true"/&gt;
 * &lt;/module&gt;
 * </pre>
 *
 * @author Oliver Burn
 * @author Andrei Selkin
 */
public class ParameterNameCheck
    extends AbstractNameCheck {

    /**
     * Allows to skip methods with Override annotation from validation.
     */
    private boolean ignoreOverridden;

    /** The visibility scope where methods are checked. */
    private Scope scope = Scope.ANONINNER;

    /** The visibility scope where methods shouldn't be checked. */
    private Scope excludeScope;

    /**
     * Creates a new {@code ParameterNameCheck} instance.
     */
    public ParameterNameCheck() {
        super("^[a-z][a-zA-Z0-9]*$");
    }

    /**
     * Sets whether to skip methods with Override annotation from validation.
     *
     * @param ignoreOverridden Flag for skipping methods with Override annotation.
     */
    public void setIgnoreOverridden(boolean ignoreOverridden) {
        this.ignoreOverridden = ignoreOverridden;
    }

    /**
     * Set the scope.
     * @param from a {@code String} value
     */
    public void setScope(String from) {
        scope = Scope.getInstance(from);
    }

    /**
     * Set the excludeScope.
     * @param excludeScope a {@code String} value
     */
    public void setExcludeScope(String excludeScope) {
        this.excludeScope = Scope.getInstance(excludeScope);
    }

    @Override
    public int[] getDefaultTokens() {
        return getAcceptableTokens();
    }

    @Override
    public int[] getAcceptableTokens() {
        return new int[] {TokenTypes.PARAMETER_DEF};
    }

    @Override
    public int[] getRequiredTokens() {
        return getAcceptableTokens();
    }

    @Override
    protected boolean mustCheckName(DetailAST ast) {
        boolean checkName = true;
        final boolean isDefault = scope == Scope.ANONINNER && excludeScope == null;

        if (ignoreOverridden && isOverriddenMethod(ast)
                || ast.getParent().getType() == TokenTypes.LITERAL_CATCH
                || CheckUtils.isReceiverParameter(ast)
                || !isDefault && !matchScope(calculateScope(ast))) {
            checkName = false;
        }
        return checkName;
    }

    /**
     * Returns the scope for the method/constructor at the specified AST. If
     * the method is in an interface or annotation block, the scope is assumed
     * to be public.
     *
     * @param ast the token of the method/constructor
     * @return the scope of the method/constructor
     */
    private static Scope calculateScope(final DetailAST ast) {
        final DetailAST params = ast.getParent();
        final DetailAST meth = params.getParent();
        Scope scope = Scope.PRIVATE;

        if (meth.getType() == TokenTypes.METHOD_DEF
            || meth.getType() == TokenTypes.CTOR_DEF) {
            if (ScopeUtils.isInInterfaceOrAnnotationBlock(ast)) {
                scope = Scope.PUBLIC;
            }
            else {
                final DetailAST mods = meth.findFirstToken(TokenTypes.MODIFIERS);
                scope = ScopeUtils.getScopeFromMods(mods);
            }
        }

        return scope;
    }

    /**
     * Checks whether a method has the correct scope to be checked.
     * @param nodeScope the scope of the method
     * @return whether the method matches the expected scope
     */
    private boolean matchScope(final Scope nodeScope) {
        return nodeScope.isIn(scope)
            && (excludeScope == null
                || !nodeScope.isIn(excludeScope));
    }

    /**
     * Checks whether a method is annotated with Override annotation.
     * @param ast method parameter definition token.
     * @return true if a method is annotated with Override annotation.
     */
    private static boolean isOverriddenMethod(DetailAST ast) {
        boolean overridden = false;

        final DetailAST parent = ast.getParent().getParent();
        final Optional<DetailAST> annotation =
            Optional.ofNullable(parent.getFirstChild().getFirstChild());

        if (annotation.isPresent() && annotation.get().getType() == TokenTypes.ANNOTATION) {
            final Optional<DetailAST> overrideToken =
                Optional.ofNullable(annotation.get().findFirstToken(TokenTypes.IDENT));
            if (overrideToken.isPresent() && "Override".equals(overrideToken.get().getText())) {
                overridden = true;
            }
        }
        return overridden;
    }
}
