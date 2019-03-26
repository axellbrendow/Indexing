import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class RegistroDoIndice<TIPO_DAS_CHAVES extends Serializavel, TIPO_DOS_DADOS extends Serializavel> implements Serializavel
{
	private char lapide;
	private TIPO_DAS_CHAVES chave;
	private TIPO_DOS_DADOS dado;
	private short quantidadeMaximaDeBytesParaAChave;
	private short quantidadeMaximaDeBytesParaODado;
	Constructor<TIPO_DAS_CHAVES> construtorDaChave;
	Constructor<TIPO_DOS_DADOS> construtorDoDado;
	
	public RegistroDoIndice(
		char lapide,
		TIPO_DAS_CHAVES chave,
		TIPO_DOS_DADOS dado,
		short quantidadeMaximaDeBytesParaAChave,
		short quantidadeMaximaDeBytesParaODado,
		Constructor<TIPO_DAS_CHAVES> construtorDaChave,
		Constructor<TIPO_DOS_DADOS> construtorDoDado)
	{
		this.lapide = lapide;
		this.chave = chave;
		this.dado = dado;
		this.quantidadeMaximaDeBytesParaAChave = quantidadeMaximaDeBytesParaAChave;
		this.quantidadeMaximaDeBytesParaODado = quantidadeMaximaDeBytesParaODado;
		this.construtorDaChave = construtorDaChave;
		this.construtorDoDado = construtorDoDado;
	}

	public RegistroDoIndice(
		Constructor<TIPO_DAS_CHAVES> construtorDaChave,
		Constructor<TIPO_DOS_DADOS> construtorDoDado)
	{
		this('*', null, null, (short) 0, (short) 0, construtorDaChave, construtorDoDado);
	}
	
	/**
	 * Calcula o tamanho que cada registro de indice gasta no bucket.
	 * 
	 * @return o tamanho que cada registro de indice gasta no bucket.
	 */
	
	public int obterTamanhoMaximoEmBytes()
	{
		int tamanho = 0;
		
		if (quantidadeMaximaDeBytesParaAChave > 0 &&
			quantidadeMaximaDeBytesParaODado > 0)
		{
			tamanho = Character.BYTES + // tamanho da lapide
				quantidadeMaximaDeBytesParaAChave + // tamanho da chave
				quantidadeMaximaDeBytesParaODado; // tamanho do dado
		}
		
		return tamanho;
	}
	
	@Override
	public void escreverObjeto(RandomAccessFile correnteDeSaida)
	{
		try
		{
			correnteDeSaida.write(obterBytes());
		}
		
		catch (IOException ioex)
		{
			ioex.printStackTrace();
		}
	}

	@Override
	public void lerObjeto(RandomAccessFile correnteDeEntrada)
	{
		try
		{
			byte[] registro = new byte[obterTamanhoMaximoEmBytes()];
			
			correnteDeEntrada.read(registro);
			
			lerBytes(registro);
		}
		
		catch (IOException ioex)
		{
			ioex.printStackTrace();
		}
	}

	@Override
	public byte[] obterBytes()
	{
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
		
		try
		{
			dataOutputStream.writeChar(lapide);
			dataOutputStream.write(chave.obterBytes());
			dataOutputStream.write(dado.obterBytes());
			
			byteArrayOutputStream.close();
			dataOutputStream.close();
		}
		
		catch (IOException ioex)
		{
			ioex.printStackTrace();
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
			lapide = dataInputStream.readChar();
			
			byte[] byteArrayChave = new byte[quantidadeMaximaDeBytesParaAChave];
			byte[] byteArrayDado = new byte[quantidadeMaximaDeBytesParaODado];
			
			dataInputStream.read(byteArrayChave);
			dataInputStream.read(byteArrayDado);
			
			try
			{
				chave = (TIPO_DAS_CHAVES) construtorDaChave.newInstance();
				dado = (TIPO_DOS_DADOS) construtorDoDado.newInstance();
				
				chave.lerBytes(byteArrayChave);
				dado.lerBytes(byteArrayDado);
			}
			
			catch (InstantiationException e)
			{
				e.printStackTrace();
			}
			
			catch (IllegalAccessException e)
			{
				e.printStackTrace();
			}
			
			catch (IllegalArgumentException e)
			{
				e.printStackTrace();
			}
			
			catch (InvocationTargetException e)
			{
				e.printStackTrace();
			}
			
			byteArrayInputStream.close();
			dataInputStream.close();
		}
		
		catch (IOException ioex)
		{
			ioex.printStackTrace();
		}
	}
}
