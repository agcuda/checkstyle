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

package com.google.checkstyle.test.chapter5naming.rule526parameternames;

import java.io.File;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.checkstyle.test.base.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;

public class ParameterNameTest extends BaseCheckTestSupport {

    private static final String MSG_KEY = "name.invalidPattern";
    private static String format;
    private static Configuration checkConfig;

    @Override
    protected String getPath(String fileName) throws IOException {
        return super.getPath("chapter5naming" + File.separator + "rule526parameternames"
                + File.separator + fileName);
    }

    @BeforeClass
    public static void setConfigurationBuilder() throws CheckstyleException {
        checkConfig = getCheckConfig("ParameterName");
        format = checkConfig.getAttribute("format");
    }

    @Test
    public void parameterNameTest() throws Exception {

        final String[] expected = {
            "8:21: " + getCheckMessage(checkConfig.getMessages(), MSG_KEY, "$arg1", format),
            "9:21: " + getCheckMessage(checkConfig.getMessages(), MSG_KEY, "ar$g2", format),
            "10:21: " + getCheckMessage(checkConfig.getMessages(), MSG_KEY, "arg3$", format),
            "11:21: " + getCheckMessage(checkConfig.getMessages(), MSG_KEY, "a_rg4", format),
            "12:21: " + getCheckMessage(checkConfig.getMessages(), MSG_KEY, "_arg5", format),
            "13:21: " + getCheckMessage(checkConfig.getMessages(), MSG_KEY, "arg6_", format),
            "14:21: " + getCheckMessage(checkConfig.getMessages(), MSG_KEY, "aArg7", format),
            "15:21: " + getCheckMessage(checkConfig.getMessages(), MSG_KEY, "aArg8", format),
            "16:21: " + getCheckMessage(checkConfig.getMessages(), MSG_KEY, "aar_g", format),
            "26:21: " + getCheckMessage(checkConfig.getMessages(), MSG_KEY, "bB", format),
            "49:22: " + getCheckMessage(checkConfig.getMessages(), MSG_KEY, "llll_llll", format),
            "50:21: " + getCheckMessage(checkConfig.getMessages(), MSG_KEY, "bB", format),
            "60:23: " + getCheckMessage(checkConfig.getMessages(), MSG_KEY, "p", format),
            "63:24: " + getCheckMessage(checkConfig.getMessages(), MSG_KEY, "p", format),
            "69:31: " + getCheckMessage(checkConfig.getMessages(), MSG_KEY, "p", format),
            "74:41: " + getCheckMessage(checkConfig.getMessages(), MSG_KEY, "p", format),
            "77:44: " + getCheckMessage(checkConfig.getMessages(), MSG_KEY, "p", format),
        };

        final String filePath = getPath("InputParameterNameSimple.java");

        final Integer[] warnList = getLinesWithWarn(filePath);
        verify(checkConfig, filePath, expected, warnList);
    }
}
