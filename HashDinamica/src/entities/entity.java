package entities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import hash_dinamica.serializaveis.SerializavelAbstract;

public class entity extends SerializavelAbstract
{
	public static final int PADRAO_TAMANHO_MAXIMO_EM_BYTES_PARA_O_NOME = 200;
	
	int id;
	String nome;
	int tamanhoMaximoEmBytesDoNome;
	
	public entity(int id, String nome, int tamanhoMaximoEmBytesDoNome)
	{
		this.id = id;
		this.nome = nome;
		this.tamanhoMaximoEmBytesDoNome = tamanhoMaximoEmBytesDoNome;
	}

	public entity(String nome, int tamanhoMaximoEmBytesDoNome)
	{
		this(-1, nome, tamanhoMaximoEmBytesDoNome);
	}
	
	public entity(String nome)
	{
		this(nome, PADRAO_TAMANHO_MAXIMO_EM_BYTES_PARA_O_NOME);
	}
	
	public entity()
	{
		this("");
	}

	@Override
	public int obterTamanhoMaximoEmBytes()
	{
		return Integer.BYTES + tamanhoMaximoEmBytesDoNome + Integer.BYTES;
	}

	@Override
	public byte[] obterBytes()
	{
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
		
		try
		{
			dataOutputStream.writeInt(id);
			dataOutputStream.writeUTF(nome);
			dataOutputStream.writeInt(tamanhoMaximoEmBytesDoNome);
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
			id = dataInputStream.readInt();
			nome = dataInputStream.readUTF();
			tamanhoMaximoEmBytesDoNome = dataInputStream.readInt();
			
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
		return id + ", " + nome + "\n";
	}
}
