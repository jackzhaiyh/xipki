/*
 *
 * This file is part of the XiPKI project.
 * Copyright (c) 2014 - 2015 Lijun Liao
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

package org.xipki.ca.server.mgmt.api;

import java.io.Serializable;
import java.security.cert.X509Certificate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xipki.common.ParamChecker;
import org.xipki.common.util.LogUtil;
import org.xipki.common.util.SecurityUtil;

/**
 * @author Lijun Liao
 */

public class CmpRequestorEntry implements Serializable
{
    private static final Logger LOG = LoggerFactory.getLogger(CmpRequestorEntry.class);

    private static final long serialVersionUID = 1L;
    private final String name;
    private final String base64Cert;
    private X509Certificate cert;

    public CmpRequestorEntry(String name, String base64Cert)
    {
        ParamChecker.assertNotEmpty("name", name);
        ParamChecker.assertNotEmpty("base64Cert", base64Cert);
        this.name = name;
        this.base64Cert = base64Cert;
        try
        {
            this.cert = SecurityUtil.parseBase64EncodedCert(base64Cert);
        }catch(Throwable t)
        {
            final String message = "could not parse the certificate for requestor '" + name + "'";
            if(LOG.isErrorEnabled())
            {
                LOG.error(LogUtil.buildExceptionLogFormat(message), t.getClass().getName(), t.getMessage());
            }
            LOG.debug(message, t);
        }
    }

    public String getName()
    {
        return name;
    }

    public X509Certificate getCert()
    {
        return cert;
    }

    @Override
    public String toString()
    {
        return toString(false);
    }

    public String toString(boolean verbose)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("name: ").append(name).append('\n');
        sb.append("faulty: ").append(cert == null).append('\n');

        if(cert != null)
        {
            sb.append("cert: ").append("\n");
            sb.append("\tissuer: ").append(
                    SecurityUtil.getRFC4519Name(cert.getIssuerX500Principal())).append("\n");
            sb.append("\tserialNumber: ").append(cert.getSerialNumber()).append("\n");
            sb.append("\tsubject: ").append(
                    SecurityUtil.getRFC4519Name(cert.getSubjectX500Principal())).append('\n');
        }
        if(verbose)
        {
            sb.append("encoded cert: ").append(base64Cert);
        }

        return sb.toString();
    }
}
