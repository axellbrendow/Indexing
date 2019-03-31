package hash_dinamica.serializaveis;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class LongSerializavel extends SerializavelAbstract
{
	long dado;
	
	public LongSerializavel(long dado)
	{
		this.dado = dado;
	}

	public LongSerializavel()
	{
		this(0);
	}

	@Override
	public int obterTamanhoMaximoEmBytes()
	{
		return Long.BYTES;
	}

	@Override
	public byte[] obterBytes()
	{
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
		
		try
		{
			dataOutputStream.writeLong(dado);
			dataOutputStream.close();
		}
		
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return byteArrayOutputStream.toByteArray();
	}

	@Override
	public void lerBytes(byte[] bytes)
	{
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
		DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
		
		try
		{
			dado = dataInputStream.readLong();
			dataInputStream.close();
		}
		
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public String toString()
	{
		return "" + dado;
	}
}
