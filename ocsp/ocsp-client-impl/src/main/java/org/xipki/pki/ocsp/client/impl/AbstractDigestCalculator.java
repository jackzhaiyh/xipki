/*
 *
 * This file is part of the XiPKI project.
 * Copyright (c) 2014 - 2016 Lijun Liao
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

package org.xipki.pki.ocsp.client.impl;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.operator.DigestCalculator;

/**
 * @author Lijun Liao
 */

public abstract class AbstractDigestCalculator implements DigestCalculator {

    private ByteArrayOutputStream bOut = new ByteArrayOutputStream();

    protected abstract ASN1ObjectIdentifier getObjectIdentifier();

    protected abstract Digest getDigester();

    public AlgorithmIdentifier getAlgorithmIdentifier() {
        return new AlgorithmIdentifier(getObjectIdentifier());
    }

    public OutputStream getOutputStream() {
        return bOut;
    }

    public byte[] getDigest() {
        byte[] bytes = bOut.toByteArray();

        bOut.reset();

        Digest digester = getDigester();

        digester.update(bytes, 0, bytes.length);

        byte[] digest = new byte[digester.getDigestSize()];

        digester.doFinal(digest, 0);

        return digest;
    }

}
