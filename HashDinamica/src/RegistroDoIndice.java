import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

/**
 * Classe utilitária para manusear os registros de indices em um bucket.
 * 
 * @author Axell Brendow ( https://github.com/axell-brendow )
 *
 * @param <TIPO_DAS_CHAVES> Classe da chave.
 * @param <TIPO_DOS_DADOS> Classe do dado.
 */

public class RegistroDoIndice<TIPO_DAS_CHAVES extends Serializavel, TIPO_DOS_DADOS extends Serializavel> implements Serializavel
{
	public static final char REGISTRO_ATIVADO = ' ';
	public static final char REGISTRO_DESATIVADO = '*';
	
	protected char lapide;
	protected TIPO_DAS_CHAVES chave;
	protected TIPO_DOS_DADOS dado;
	protected short quantidadeMaximaDeBytesParaAChave;
	protected short quantidadeMaximaDeBytesParaODado;
	protected Constructor<TIPO_DAS_CHAVES> construtorDaChave;
	protected Constructor<TIPO_DOS_DADOS> construtorDoDado;
	
	/**
	 * Cria um objeto que gerencia um registro de indice no bucket da hash dinâmica.
	 * 
	 * @param lapide Lapide do registro.
	 * @param chave Chave do registro.
	 * @param dado Dado que corresponde à chave.
	 * @param quantidadeMaximaDeBytesParaAChave Tamanho máximo que a chave pode gastar.
	 * @param quantidadeMaximaDeBytesParaODado Tamanho máximo que o dado pode gastar.
	 * @param construtorDaChave Construtor da chave. É necessário que a chave tenha um
	 * construtor sem parâmetros.
	 * @param construtorDoDado Construtor do dado. É necessário que o dado tenha um
	 * construtor sem parâmetros.
	 */
	
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
	
	/**
	 * Preenche com zeros a saída de dados até que o espaço
	 * usado seja igual ao tamanho máximo.
	 * 
	 * @param dataOutputStream Corrente de saída de dados.
	 * @param tamanhoUsado Quantidade de bytes já escritos.
	 * @param tamanhoMaximo Tamanho máximo desejado.
	 */
	
	private void completarEspacoNaoUsado(
		DataOutputStream dataOutputStream,
		int tamanhoUsado,
		int tamanhoMaximo)
	{
		int restante = tamanhoMaximo - tamanhoUsado;
		
		if (restante > 0)
		{
			byte[] lixo = new byte[restante];
			
			Arrays.fill(lixo, (byte) 0);
			
			try
			{
				dataOutputStream.write(lixo);
			}
			
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
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
			dataOutputStream.writeByte(lapide);
			
			byte[] byteArrayChave = chave.obterBytes();
			
			dataOutputStream.write(byteArrayChave);
			
			completarEspacoNaoUsado(
				dataOutputStream,
				byteArrayChave.length,
				quantidadeMaximaDeBytesParaAChave
			);
			
			byte[] byteArrayDado = dado.obterBytes();
			
			dataOutputStream.write(byteArrayDado);
			
			completarEspacoNaoUsado(
				dataOutputStream,
				byteArrayDado.length,
				quantidadeMaximaDeBytesParaODado
			);
			
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
			dataInputStream.skipBytes(deslocamento);
			lapide = (char) dataInputStream.readByte();
			
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

	@Override
	public void lerBytes(byte[] bytes)
	{
		lerBytes(bytes, 0);
	}
}
