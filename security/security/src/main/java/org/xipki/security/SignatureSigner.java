/*
 * Copyright (c) 2014 Lijun Liao
 *
 * TO-BE-DEFINE
 *
 */

package org.xipki.security;

import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.RuntimeOperatorException;
import org.xipki.common.ParamChecker;

/**
 * @author Lijun Liao
 */

public class SignatureSigner implements ContentSigner
{
    private final AlgorithmIdentifier sigAlgId;
    private final Signature signer;
    private final SignatureStream stream = new SignatureStream();
    private final PrivateKey key;

    public SignatureSigner(AlgorithmIdentifier sigAlgId, Signature signer, PrivateKey key)
    {
        ParamChecker.assertNotNull("sigAlgId", sigAlgId);
        ParamChecker.assertNotNull("signer", signer);
        ParamChecker.assertNotNull("key", key);

        this.sigAlgId = sigAlgId;
        this.signer = signer;
        this.key = key;
    }

    @Override
    public AlgorithmIdentifier getAlgorithmIdentifier()
    {
        return sigAlgId;
    }

    @Override
    public OutputStream getOutputStream()
    {
        try
        {
            signer.initSign(key);
        } catch (InvalidKeyException e)
        {
            throw new RuntimeOperatorException("Could not initSign", e);
        }
        return stream;
    }

    @Override
    public byte[] getSignature()
    {
        try
        {
            return stream.getSignature();
        }
        catch (SignatureException e)
        {
            throw new RuntimeOperatorException("exception obtaining signature: " + e.getMessage(), e);
        }
    }

    private class SignatureStream extends OutputStream
    {
        public byte[] getSignature()
        throws SignatureException
        {
            return signer.sign();
        }

        @Override
        public void write(int b)
        throws IOException
        {
            try
            {
                signer.update((byte) b);
            }catch(SignatureException e)
            {
                throw new IOException(e);
            }
        }

        @Override
        public void write(byte[] b)
        throws IOException
        {
            try
            {
                signer.update(b);
            }catch(SignatureException e)
            {
                throw new IOException(e);
            }
        }

        @Override
        public void write(byte[] b, int off, int len)
        throws IOException
        {
            try
            {
                signer.update(b, off, len);
            }catch(SignatureException e)
            {
                throw new IOException(e);
            }
        }
    }

}