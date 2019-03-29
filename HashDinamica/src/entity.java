import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class entity implements Serializavel
{
	int id;
	String nome;
	
	public entity(int id, String nome)
	{
		this.id = id;
		this.nome = nome;
	}

	public entity(String nome)
	{
		this(-1, nome);
	}
	
	public entity()
	{
		this("");
	}
	
	@Override
	public void escreverObjeto(RandomAccessFile correnteDeSaida)
	{
		try
		{
			correnteDeSaida.writeInt(id);
			correnteDeSaida.writeUTF(nome);
		}
		
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void lerObjeto(RandomAccessFile correnteDeEntrada)
	{
		try
		{
			id = correnteDeEntrada.readInt();
			nome = correnteDeEntrada.readUTF();
		}
		
		catch (IOException e)
		{
			e.printStackTrace();
		}
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
			
			byteArrayOutputStream.close();
			dataOutputStream.close();
		}
		
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return byteArrayOutputStream.toByteArray();
	}

	@Override
	public void escreverObjeto(byte[] correnteDeSaida, int deslocamento)
	{
		byte[] bytes = obterBytes();
		
		System.arraycopy(bytes, 0, correnteDeSaida, deslocamento, bytes.length);
	}

	@Override
	public void lerBytes(byte[] bytes, int deslocamento)
	{
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
		DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
		
		try
		{
			dataInputStream.skip(deslocamento);
			id = dataInputStream.readInt();
			nome = dataInputStream.readUTF();
			
			byteArrayInputStream.close();
			dataInputStream.close();
		}
		
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void lerBytes(byte[] bytes)
	{
		lerBytes(bytes, 0);
	}
	
	
}
