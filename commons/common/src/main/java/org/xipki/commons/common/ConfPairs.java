/*
 *
 * This file is part of the XiPKI project.
 * Copyright (c) 2013 - 2016 Lijun Liao
 * Author: Lijun Liao
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3
 * as published by the Free Software Foundation with the addition of the
 * following permission added to Section 15 as permitted in Section 7(a):
 * FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
 * THE AUTHOR LIJUN LIAO. LIJUN LIAO DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
 * OF THIRD PARTY RIGHTS.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * The interactive user interfaces in modified source and object code versions
 * of this program must display Appropriate Legal Notices, as required under
 * Section 5 of the GNU Affero General Public License.
 *
 * You can be released from the requirements of the license by purchasing
 * a commercial license. Buying such a license is mandatory as soon as you
 * develop commercial activities involving the XiPKI software without
 * disclosing the source code of your own applications.
 *
 * For more information, please contact Lijun Liao at this
 * address: lijun.liao@gmail.com
 */

package org.xipki.commons.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xipki.commons.common.util.ParamUtil;

/**
 * @author Lijun Liao
 * @since 2.0.0
 */

public class ConfPairs {

    public static final char NAME_TERM = '=';

    public static final char TOKEN_TERM = ',';

    private final Map<String, String> pairs = new HashMap<>();

    public ConfPairs(
            final String name,
            final String value) {
        putPair(name, value);
    }

    public ConfPairs() {
    }

    public ConfPairs(
            final String string) {
        if (string == null || string.length() < 2) {
            return;
        }

        int n = string.length();
        List<String> tokens = new LinkedList<>();

        StringBuilder tokenBuilder = new StringBuilder();

        for (int i = 0; i < n;) {
            char c = string.charAt(i);
            if (TOKEN_TERM == c) {
                if (tokenBuilder.length() > 0) {
                    tokens.add(tokenBuilder.toString());
                }
                // reset tokenBuilder
                tokenBuilder = new StringBuilder();
                i++;
                continue;
            }

            if ('\\' == c) {
                if (i == n - 1) {
                    throw new IllegalArgumentException("invalid ConfPairs '" + string + "'");
                }

                tokenBuilder.append(c);
                c = string.charAt(i + 1);
                i++;
            }

            tokenBuilder.append(c);
            i++;
        }

        if (tokenBuilder.length() > 0) {
            tokens.add(tokenBuilder.toString());
        }

        for (String token : tokens) {
            int termPosition = -1;
            n = token.length();
            for (int i = 0; i < n;) {
                char c = token.charAt(i);
                if (c == NAME_TERM) {
                    termPosition = i;
                    break;
                }

                if ('\\' == c) {
                    if (i == n - 1) {
                        throw new IllegalArgumentException("invalid ConfPairs '" + string + "'");
                    }

                    i += 2;
                } else {
                    i++;
                }
            }

            if (termPosition < 1) {
                throw new IllegalArgumentException("invalid ConfPair '" + token + "'");
            }

            tokenBuilder = new StringBuilder();
            for (int i = 0; i < termPosition;) {
                char c = token.charAt(i);
                if ('\\' == c) {
                    if (i == termPosition - 1) {
                        throw new IllegalArgumentException("invalid ConfPair '" + string + "'");
                    }

                    i += 2;
                } else {
                    tokenBuilder.append(c);
                    i++;
                }
            }

            String name = tokenBuilder.toString();

            tokenBuilder = new StringBuilder();
            for (int i = termPosition + 1; i < n;) {
                char c = token.charAt(i);
                if ('\\' == c) {
                    if (i == n - 1) {
                        throw new IllegalArgumentException("invalid ConfPair '" + string + "'");
                    }

                    c = token.charAt(i + 1);
                    i++;
                }

                tokenBuilder.append(c);
                i++;
            }

            String value = tokenBuilder.toString();
            pairs.put(name, value);
        }
    } // constructor

    public void putPair(
            final String name,
            final String value) {
        ParamUtil.assertNotBlank("name", name);
        ParamUtil.assertNotNull("value", value);

        char c = name.charAt(0);
        if (c >= '0' && c <= '9') {
            throw new IllegalArgumentException("name begin with " + c);
        }
        pairs.put(name, value);
    }

    public void removePair(
            final String name) {
        pairs.remove(name);
    }

    public String getValue(
            final String name) {
        return pairs.get(name);
    }

    public Set<String> getNames() {
        return Collections.unmodifiableSet(pairs.keySet());
    }

    public String getEncoded() {
        StringBuilder sb = new StringBuilder();
        List<String> names = new LinkedList<>();
        for (String name : pairs.keySet()) {
            String value = pairs.get(name);
            if (value.length() <= 100) {
                names.add(name);
            }
        }
        Collections.sort(names);

        for (String name : pairs.keySet()) {
            if (!names.contains(name)) {
                names.add(name);
            }
        }

        for (String name : names) {
            String value = pairs.get(name);
            sb.append(encodeNameOrValue(name));
            sb.append(NAME_TERM);
            sb.append(value == null
                    ? ""
                    : encodeNameOrValue(value));
            sb.append(TOKEN_TERM);
        }

        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    } // method getEncoded

    @Override
    public String toString() {
        return getEncoded();
    }

    @Override
    public int hashCode() {
        return getEncoded().hashCode();
    }

    @Override
    public boolean equals(
            final Object obj) {
        if (!(obj instanceof ConfPairs)) {
            return false;
        }

        ConfPairs b = (ConfPairs) obj;
        return pairs.equals(b.pairs);
    }

    private static String encodeNameOrValue(
            final String s) {
        if (s.indexOf(NAME_TERM) == -1 && s.indexOf(TOKEN_TERM) == -1) {
            return s;
        }

        final int n = s.length();
        StringBuilder sb = new StringBuilder(n + 1);
        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);
            if (c == NAME_TERM || c == TOKEN_TERM) {
                sb.append('\\');
            }
            sb.append(c);
        }
        return sb.toString();
    }

}